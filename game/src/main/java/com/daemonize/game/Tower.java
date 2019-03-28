package com.daemonize.game;


import com.daemonize.daemonprocessor.annotations.ConsumerArg;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.scene.views.ImageView;

import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.LinkedList;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Daemonize(doubleDaemonize = true, daemonizeBaseMethods = true)
public class Tower extends RotatingSpriteImageMover {

    public enum TowerType {
        TYPE1,
        TYPE2,
        TYPE3
    }

    public static class TowerLevel {

        public int currentLevel;
        public int bulletDamage;
        public int reloadInterval;

        public TowerLevel(int currentLevel, int bulletDamage, int reloadInterval) {
            this.currentLevel = currentLevel;
            this.bulletDamage = bulletDamage;
            this.reloadInterval = reloadInterval;
        }
    }

    private TowerType towertype;
    private TowerLevel towerLevel = new TowerLevel(1,2,1500);
    private ImageView view;

    private volatile Queue<EnemyDoubleDaemon> targetQueue;
    private Lock targetLock;
    private Condition targetCondition;

    @CallingThread
    public boolean addTarget(EnemyDoubleDaemon target) {
        boolean ret = false;
        targetLock.lock();
        if (!targetQueue.contains(target)) {
            if (targetQueue.isEmpty()) {
                ret = targetQueue.add(target);
                targetCondition.signalAll();
            } else {
                ret = targetQueue.add(target);
            }
        }
        targetLock.unlock();
        return ret;
    }

    @CallingThread
    public float getRange() {
        return range;
    }

    private volatile float range;

    @CallingThread
    public void levelUp(){
        switch (++towerLevel.currentLevel) {
            case 2:{//midle level
                towerLevel.bulletDamage += 3;
                towerLevel.reloadInterval -=500;
                break;
            }
            case 3: { //top level
                towerLevel.bulletDamage += 5;
                towerLevel.reloadInterval -= 800;
                break;
            }
            default: {
                towerLevel.currentLevel = 3; // TODO currentLevel should be equal max level
            }
        }
    }

    @CallingThread
    public TowerLevel getTowerLevel() {
        return towerLevel;
    }

    @CallingThread
    public void setTowerLevel(TowerLevel towerLevel) {
        this.towerLevel = towerLevel;
    }

    private DaemonSemaphore scanSemaphore = new DaemonSemaphore();

    @CallingThread
    public ImageView getView() {
        return view;
    }

    @CallingThread
    public void setView(ImageView view) {
        this.view = view;
    }

    public Tower(Image[] rotationSprite,  Pair<Float, Float> startingPos, float range, TowerType type, float dXY) {
        super(rotationSprite, 0, startingPos, dXY);
        this.range = range;
        this.towertype = type;
        this.targetQueue = new LinkedList<>();
        this.targetLock = new ReentrantLock();
        this.targetCondition = targetLock.newCondition();
        this.animateSemaphore.stop();
    }

    @GenerateRunnable
    public void reload(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    @CallingThread
    @Override
    public void setRotationSprite(Image[] rotationSprite) {
        super.setRotationSprite(rotationSprite);
    }

    private Pair<TowerType, EnemyDoubleDaemon> scanRet = Pair.create(null, null);

    @DedicatedThread(name = "scan")
    public Pair<TowerType, EnemyDoubleDaemon> scan() throws InterruptedException {

        //pause scan semaphore
        scanSemaphore.await();

        EnemyDoubleDaemon target;
        scanRet = Pair.create(null, null);

        targetLock.lock();
        try {

            while (targetQueue.isEmpty())
                targetCondition.await();

            target = targetQueue.peek();

            if (target.isShootable()
                    && (Math.abs(target.getLastCoordinates().getFirst() - getLastCoordinates().getFirst()) < range
                    && Math.abs(target.getLastCoordinates().getSecond() - getLastCoordinates().getSecond()) < range)
            )
                scanRet = Pair.create(towertype, target);
            else
                targetQueue.poll();

        } finally {
            targetLock.unlock();
        }

        if (target.isShootable()) {

            animateSemaphore.subscribe();

            try {
                rotateTowards(
                        target.getLastCoordinates().getFirst(),
                        target.getLastCoordinates().getSecond()
                );
            } finally {
                animateSemaphore.unsubscribe();
            }
        }

        return scanRet;
    }


    @CallingThread
    @Override
    public void pause() {
        super.pause();
        pauseScan();
    }

    @CallingThread
    @Override
    public void cont() {
        super.cont();
        contScan();
        targetLock.lock();
        targetCondition.signalAll();
        targetLock.unlock();
    }

    @CallingThread
    @Override
    public void setCurrentAngle(int currentAngle) {
        super.setCurrentAngle(currentAngle);
    }

    @CallingThread
    public void pauseScan() {
        scanSemaphore.stop();
    }

    @CallingThread
    public void contScan() {
        scanSemaphore.go();
    }

    private volatile PositionedImage ret = new PositionedImage();

    @ConsumerArg
    public PositionedImage updateSprite() {//hack but improves performance
        Pair<Float, Float> lastCoord = getLastCoordinates();
        ret.image = iterateSprite();
        ret.positionX = lastCoord.getFirst();
        ret.positionY = lastCoord.getSecond();
        return ret;
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedImage animate() throws InterruptedException {
        try {
            animateSemaphore.await();
            return updateSprite();
        } catch (InterruptedException ex) {
            return null;
        }
        //return super.animate();
    }

    @CallingThread
    public TowerType getTowertype() {
        return towertype;
    }
}
