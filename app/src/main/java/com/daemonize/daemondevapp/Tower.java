package com.daemonize.daemondevapp;


import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.RotatingSpriteImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Daemonize(doubleDaemonize = true)
public class Tower extends RotatingSpriteImageMover {

    public static enum TowerType {
        TYPE1,
        TYPE2,
        TYPE3;
    };

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
    //private volatile boolean targetFlag;

    @CallingThread
    public boolean addTarget(EnemyDoubleDaemon target) {
        boolean ret = false;
        targetLock.lock();
        if (!targetQueue.contains(target)) {
            if (targetQueue.isEmpty()) {
                ret = targetQueue.add(target);
                targetCondition.signal();
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

    private DaemonCountingSemaphore scanSemaphore = new DaemonCountingSemaphore();

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
        this.targetQueue = new LinkedList<EnemyDoubleDaemon>();
        this.targetLock = new ReentrantLock();
        this.targetCondition = targetLock.newCondition();
    }

    public boolean reload(long millis) throws InterruptedException {
        Thread.sleep(millis);
        return true;
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

//    public Pair<TowerType, EnemyDoubleDaemon> scan (List<EnemyDoubleDaemon> activeEnemies) throws InterruptedException {
//
//        scanSemaphore.await();
//
//        for (EnemyDoubleDaemon enemy : activeEnemies) {
//            if (Math.abs( lastX - enemy.getPrototype().getLastCoordinates().getFirst()) < range
//                    && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().getSecond()) < range) {
//                rotateTowards(
//                        enemy.getPrototype().getLastCoordinates().getFirst(),
//                        enemy.getPrototype().getLastCoordinates().getSecond()
//                );
//                return Pair.create(towertype, enemy);
//            }
//        }
//
//        return Pair.create(null, null);
//    }

    @DedicatedThread
    public Pair<TowerType, EnemyDoubleDaemon> scan() throws InterruptedException {

        scanSemaphore.await();

        EnemyDoubleDaemon target = null;
        Pair<TowerType, EnemyDoubleDaemon> ret = Pair.create(null, null);

        targetLock.lock();
        try {

            while (targetQueue.isEmpty())
                targetCondition.await();

            target = targetQueue.peek();

            if (target.isShootable() && (Math.abs(target.getLastCoordinates().getFirst() - lastX) < range && Math.abs(target.getLastCoordinates().getSecond() - lastY) < range)) {
                ret = Pair.create(towertype, target);
            } else {
                targetQueue.poll();
            }

        } finally {
            targetLock.unlock();
        }

        if (target != null)
            rotateTowards(
                    target.getLastCoordinates().getFirst(),
                    target.getLastCoordinates().getSecond()
            );

        return ret;
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
    }

    @CallingThread
    public void pauseScan() {
        scanSemaphore.subscribe();
    }

    @CallingThread
    public void contScan() {
        scanSemaphore.unsubscribe();
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedImage animate() {
        try {
            pauseSemaphore.await();
            PositionedImage ret = new PositionedImage();
            ret.image = iterateSprite();
            ret.positionX = lastX;
            ret.positionY = lastY;
            return ret;
        } catch (InterruptedException ex) {
            return null;
        }
    }
}
