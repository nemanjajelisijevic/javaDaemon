package com.daemonize.daemondevapp.imagemovers;

import android.util.Log;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.List;
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
            Pair<Float, Float> startingPos,
            Pair<Float, Float> targetCoord
    ) {
        super(sprite, velocity, startingPos);
        this.targetX = targetCoord.getFirst();
        this.targetY = targetCoord.getSecond();
    }

    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {

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

        return true;
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
