package com.daemonize.imagemovers;

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

    public  CoordinatedImageTranslationMover(
            Image [] sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        super(sprite, velocity, startingPos, dXY);
    }

    public boolean redirect(float x, float y) {
        setTargetCoordinates(x, y);
        return setDirectionToPoint(x, y);
    }

    @Daemonize
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {

        //check borders
        if (x < borderX1)
            x = borderX1;
        else if (x > borderX2)
            x = borderX2;

        if (y < borderY1)
            y = borderY1;
        else if (y > borderY2)
            y = borderY2;

        if (!super.setDirectionAndMove(x, y, velocityInt))
            return false;

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
        if (Math.abs(ret.positionX - targetX)  <= 2 * velocity.intensity * getdXY()
                && Math.abs(ret.positionY - targetY)  <= 2 * velocity.intensity * getdXY()) {
            coordinateLock.lock();
            coordinatesReached = true;
            setTargetCoordinates(Float.NaN, Float.NaN);
            coordinateReachedCondition.signal();
            coordinateLock.unlock();
            return null;
        }

        return ret;
    }

}
