package com.daemonize.game.imagemovers;


import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.game.Bullet;
import com.daemonize.game.Enemy;
import com.daemonize.game.Pair;
import com.daemonize.game.images.Image;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class CoordinatedImageTranslationMover extends CachedArraySpriteImageMover {

    private Lock coordinateLock = new ReentrantLock();//TODO do dis with a semaphore
    private Condition coordinateReachedCondition = coordinateLock.newCondition();
    private volatile boolean coordinatesReached = false;

    private volatile float targetX;
    private volatile float targetY;

    private void setTargetCoordinates(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @CallingThread
    public Pair<Float, Float> getTargetCoordinates() {
        return Pair.create(targetX, targetY);
    }

    public  CoordinatedImageTranslationMover(
            Image [] sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        super(sprite, velocity, startingPos, dXY);
    }

    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {

        boolean ret = super.setDirectionAndMove(x, y, velocityInt);

        if (!ret)
            return ret;

        animateSemaphore.subscribe();

        coordinateLock.lock();

        setTargetCoordinates(x, y);

        try {
            while (!coordinatesReached)
                coordinateReachedCondition.await();
        } finally {
            coordinatesReached = false;
            coordinateLock.unlock();
            animateSemaphore.unsubscribe();
        }

        return ret;
    }

    @CallingThread
    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
        setTargetCoordinates(0F, 0F);
    }

    @Override
    public PositionedImage animate() throws InterruptedException {

        PositionedImage ret = super.animate();

        Pair<Float, Float> lastCoord = getLastCoordinates();

        if (Math.abs(lastCoord.getFirst() - targetX)  <= velocity.intensity
                && Math.abs(lastCoord.getSecond() - targetY)  <= velocity.intensity) {
            coordinateLock.lock();
            coordinatesReached = true;
            setTargetCoordinates(0F, 0F);
            coordinateReachedCondition.signal();
            coordinateLock.unlock();
            return null;
        }

        return ret;
    }

}
