package com.daemonize.daemondevapp;


import com.daemonize.daemondevapp.imagemovers.CachedArraySpriteImageMover;
import com.daemonize.daemondevapp.imagemovers.RotatingSpriteImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.Arrays;
import java.util.List;


@Daemonize(doubleDaemonize = true)
public class Tower extends RotatingSpriteImageMover {

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

    private TowerLevel towerLevel = new TowerLevel(1,2,2000);
    private ImageView view;
    private Game.TowerScanClosure scanClosure; //TODO check dis
    private float range;

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
    public void setScanClosure(Game.TowerScanClosure scanClosure) {
        this.scanClosure = scanClosure;
    }

    @CallingThread
    public Game.TowerScanClosure getScanClosure() {
        return scanClosure;
    }

    @CallingThread
    public ImageView getView() {
        return view;
    }

    @CallingThread
    public void setView(ImageView view) {
        this.view = view;
    }

    public Tower(Image[] rotationSprite,  Pair<Float, Float> startingPos, float range) {
        super(rotationSprite, 0, startingPos);
        this.range = range;
//        this.level = level;
//        this.scanInterval = scanIntervalInMillis;
    }

    public boolean sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
        return true;
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

    @DedicatedThread
    public Pair<Boolean, EnemyDoubleDaemon> scan (List<EnemyDoubleDaemon> activeEnemies) throws InterruptedException {

        scanSemaphore.await();

        for (EnemyDoubleDaemon enemy : activeEnemies) {
            if (Math.abs( lastX - enemy.getPrototype().getLastCoordinates().getFirst()) < range
                    && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().getSecond()) < range) {
                rotateTowards(
                        enemy.getPrototype().getLastCoordinates().getFirst(),
                        enemy.getPrototype().getLastCoordinates().getSecond()
                );
                return Pair.create(true, enemy);
            }
        }

        return Pair.create(false, null);
    }

    @CallingThread
    @Override
    public void pause() {
        super.pause();
        scanSemaphore.subscribe();
    }

    @CallingThread
    @Override
    public void cont() {
        super.cont();
        scanSemaphore.unsubscribe();
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedImage animate() {
        try {
            semaphore.await();
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
