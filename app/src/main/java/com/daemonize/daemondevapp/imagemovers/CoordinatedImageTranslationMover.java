package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Daemonize(doubleDaemonize = true)
public class CoordinatedImageTranslationMover extends ImageTranslationMover {

    private Lock coordinateLock = new ReentrantLock();
    private Condition coordinateReachedCondition = coordinateLock.newCondition();
    private volatile boolean coordinatesReached = false;

    private float targetX;
    private float targetY;

    public CoordinatedImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }

    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {

        super.goTo(x, y, velocityInt);
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

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedBitmap move() {
        coordinateLock.lock();
        if (Math.abs(lastX - targetX)  < 20 && Math.abs(lastY - targetY)  < 20) {
            coordinatesReached = true;
            coordinateReachedCondition.signal();
        }
        coordinateLock.unlock();
        return super.move();
    }
}
