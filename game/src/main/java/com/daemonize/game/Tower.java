package com.daemonize.game;


import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.view.ImageView;

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


@Daemonize(doubleDaemonize = true)
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

    private DaemonSemaphore animateSemaphore = new DaemonSemaphore();

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

    public Tower(Image[] rotationSprite,  Pair<Float, Float> startingPos, float range, TowerType type) {
        super(rotationSprite, 0, startingPos);
        this.range = range;
        this.towertype = type;
        this.targetQueue = new LinkedList<>();
        this.targetLock = new ReentrantLock();
        this.targetCondition = targetLock.newCondition();
    }

    @GenerateRunnable
    public void reload(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    @CallingThread
    @Override
    public Pair<Float, Float> getLastCoordinates() {
        return super.getLastCoordinates();
    }

    @CallingThread
    @Override
    public void setRotationSprite(Image[] rotationSprite) {
        super.setRotationSprite(rotationSprite);
    }

    @Override
    public void rotateTowards(float x, float y) throws InterruptedException {
        super.rotateTowards(x, y);
    }

    private Pair<TowerType, EnemyDoubleDaemon> scanRet = Pair.create(null, null);

    @DedicatedThread
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
                    && (Math.abs(target.getLastCoordinates().getFirst() - lastX) < range
                    && Math.abs(target.getLastCoordinates().getSecond() - lastY) < range)
            )
                scanRet = Pair.create(towertype, target);
            else
                targetQueue.poll();

        } finally {
            targetLock.unlock();
        }

        animateSemaphore.go();

        try {
            rotateTowards(
                    target.getLastCoordinates().getFirst(),
                    target.getLastCoordinates().getSecond()
            );
        } finally {
            animateSemaphore.stop();
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

    public PositionedImage updateSprite() {//hack but improves performance
        ret.image = iterateSprite();
        ret.positionX = lastX;
        ret.positionY = lastY;
        return ret;
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedImage animate() {
        try {
            animateSemaphore.await();
            return updateSprite();
        } catch (InterruptedException ex) {
            return null;
        }
    }

    @CallingThread
    public TowerType getTowertype() {
        return towertype;
    }
}
