package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import java.util.List;

public class MainImageTranslationMover extends ImageTranslationMover {

    private final List<ImageMoverDaemon> observers;

    public enum Mode {
        NONE,
        CHASE,
        COLLIDE
    }

    private Mode mode;

    public MainImageTranslationMover(
            List<Bitmap> sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            List<ImageMoverDaemon> observers, Mode mode) {
        super(sprite, velocity, startingPos);
        this.observers = observers;
        this.mode = mode;
    }

    @Override
    public MainImageTranslationMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public PositionedBitmap move() {

        if(velocity > 0 ) {

            for (ImageMoverDaemon observer : observers) {
                if(mode.equals(Mode.CHASE)) {
                    observer.setTouchDirection(lastX, lastY); //TODO CHASER
                } else if (mode.equals(Mode.COLLIDE)) {
                    observer.checkCollisionAndBounce(Pair.create(lastX, lastY), velocity, new Direction(currentDirX, currentDirY)); //TODO Collisions
                }
            }

            PositionedBitmap ret = new PositionedBitmap();

            ret.image = iterateSprite();

            //check borders and recalculate
            if (lastX <= 0) {
                currentDirX = -currentDirX;
                lastX = 0;
            } else if (lastX >= borderX) {
                currentDirX = -currentDirX;
                lastX = borderX;
            }

            if (lastY <= 0) {
                currentDirY = -currentDirY;
                lastY = 0;
            } else if (lastY >= borderY) {
                currentDirY = -currentDirY;
                lastY = borderY;
            }

            lastX = lastX + velocity * (currentDirX * 0.01f);
            lastY = lastY + velocity * (currentDirY * 0.01f);

            ret.positionX = lastX;
            ret.positionY = lastY;

            velocity -= 0.1;
            return ret;
        }

        return null;
    }

    @Override
    public void setTouchDirection(float x, float y) {

        float diffX = x - lastX;
        float diffY = y - lastY;

        float a;
        boolean signY = diffY >= 0;
        boolean signX = diffX >= 0;

        if (Math.abs(diffY) >= Math.abs(diffX)) {
            a = Math.abs((100*diffX)/diffY);
            float aY =  100 - a;
            setDirection(new Direction(signX ? a : - a, signY ? aY : - aY));
        } else {
            a = Math.abs((100*diffY)/diffX);
            float aX =  100 - a;
            setDirection(new Direction(signX ? aX : -aX, signY ? a : -a));
        }

        setVelocity(initVelocity);
    }


}
