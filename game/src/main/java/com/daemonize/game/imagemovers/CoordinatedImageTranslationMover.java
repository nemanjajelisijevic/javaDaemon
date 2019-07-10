package com.daemonize.game.imagemovers;


import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonengine.utils.Pair;
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

        if (!ret) {
            return ret;
        }

        coordinateLock.lock();

        setTargetCoordinates(x, y);
        animateSemaphore.subscribe();

        try {
            while (!coordinatesReached)
                coordinateReachedCondition.await();
        } finally {
            coordinatesReached = false;
            animateSemaphore.unsubscribe();
            coordinateLock.unlock();
        }

        return ret;
    }

    @CallingThread
    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
        setTargetCoordinates(Float.MIN_VALUE, Float.MIN_VALUE);
    }

    @Override
    public PositionedImage animate() throws InterruptedException {

        PositionedImage ret = super.animate();

        Pair<Float, Float> lastCoord = getLastCoordinates();

        if (Math.abs(lastCoord.getFirst() - targetX)  <= velocity.intensity * getdXY()
                && Math.abs(lastCoord.getSecond() - targetY)  <= velocity.intensity * getdXY()) {
            coordinateLock.lock();
            coordinatesReached = true;
            setTargetCoordinates(Float.MIN_VALUE, Float.MIN_VALUE);
            coordinateReachedCondition.signal();
            coordinateLock.unlock();
            return null;
        }

        return ret;
    }

}
