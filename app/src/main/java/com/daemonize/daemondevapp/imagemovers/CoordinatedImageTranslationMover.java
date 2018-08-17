package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class CoordinatedImageTranslationMover extends CachedSpriteImageTranslationMover {

    private Lock coordinateLock = new ReentrantLock();
    private Condition coordinateReachedCondition = coordinateLock.newCondition();
    private volatile boolean coordinatesReached = false;

    private volatile float targetX;
    private volatile float targetY;

    public CoordinatedImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos, Pair<Float, Float> targetCoord) {
        super(sprite, velocity, startingPos);
        this.targetX = targetCoord.first;
        this.targetY = targetCoord.second;
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
    public PositionedBitmap move() {

        if (Math.abs(lastX - targetX)  < velocity.intensity
                && Math.abs(lastY - targetY)  < velocity.intensity) {//TODO unhardcode this
            coordinateLock.lock();
            coordinatesReached = true;
            coordinateReachedCondition.signal();
            coordinateLock.unlock();
        }

        return super.move();
    }
}
