package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.graphics2d.images.Image;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CoordinatedImageTranslationMover extends CachedArraySpriteImageMover {

    private Lock coordinateLock = new ReentrantLock();//TODO do dis with a semaphore
    private Condition coordinateReachedCondition = coordinateLock.newCondition();
    private volatile boolean coordinatesReached = false;

    private volatile float targetX;
    private volatile float targetY;

    private synchronized void setTargetCoordinates(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public Pair<Float, Float> getTargetCoordinates() {
        return Pair.create(targetX, targetY);
    }

    public CoordinatedImageTranslationMover(
            Image [] sprite,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        super(sprite, startingPos, dXY);
        setTargetCoordinates(Float.NaN, Float.NaN);
    }

    public CoordinatedImageTranslationMover(
            Image startImage,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        this(new Image[]{startImage}, startingPos, dXY);
    }

    public boolean redirect(float x, float y) {
        setTargetCoordinates(x, y);
        return setDirectionToPoint(x, y);
    }

    @Daemonize
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {

        if (!super.setDirectionToPoint(x, y))
            return false;

        coordinateLock.lock();
        setTargetCoordinates(x, y);
        setVelocity(velocityInt);

        try {
            while (!coordinatesReached)
                coordinateReachedCondition.await();
        } finally {
            coordinatesReached = false;
            setVelocity(0);
            coordinateLock.unlock();
        }

        return true;
    }

    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
        setTargetCoordinates(Float.NaN, Float.NaN);
    }

    @Override
    public PositionedImage animate() throws InterruptedException {

        PositionedImage ret = super.animate();

        if (Math.abs(ret.positionX - targetX)  <= velocity.intensity * getdXY()
                && Math.abs(ret.positionY - targetY)  <= velocity.intensity * getdXY()) {
            coordinateLock.lock();
            coordinatesReached = true;
            ret.positionX = targetX;
            ret.positionY = targetY;
            setCoordinates(targetX, targetY);
            coordinateReachedCondition.signalAll();
            coordinateLock.unlock();
        }

        return ret;
    }
}
