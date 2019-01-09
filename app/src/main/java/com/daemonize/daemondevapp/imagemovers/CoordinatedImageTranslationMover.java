package com.daemonize.daemondevapp.imagemovers;


import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class CoordinatedImageTranslationMover extends CachedArraySpriteImageMover {

    private Lock coordinateLock = new ReentrantLock();//TODO do dis with a semaphore
    private Condition coordinateReachedCondition = coordinateLock.newCondition();
    private volatile boolean coordinatesReached = false;

    protected volatile float targetX;
    protected volatile float targetY;

    public CoordinatedImageTranslationMover(
            Image [] sprite,
            float velocity,
            Pair<Float, Float> startingPos
    ) {
        super(sprite, velocity, startingPos);
    }

    public void goTo(float x, float y, float velocityInt) throws InterruptedException {
        super.setDirectionAndMove(x, y, velocityInt);
        coordinateLock.lock();

        targetX = x;
        targetY = y;

        try {
            while (!coordinatesReached) {
                coordinateReachedCondition.await();
            }
        } finally {
            coordinatesReached = false;
            coordinateLock.unlock();
        }
    }

    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
        targetX = 0;
        targetY = 0;
    }

    @Override
    public PositionedImage animate() throws InterruptedException {
        if (Math.abs(lastX - targetX)  <= velocity.intensity
                && Math.abs(lastY - targetY)  <= velocity.intensity) {
            coordinateLock.lock();
            coordinatesReached = true;
            targetX = 0;
            targetY = 0;
            coordinateReachedCondition.signal();
            coordinateLock.unlock();
        }

        return super.animate();
    }
}
