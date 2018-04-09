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

        if(momentum.velocity > 0 ) {

            for (ImageMoverDaemon observer : observers) {
                if(mode.equals(Mode.CHASE)) {
                    observer.setTouchDirection(lastX, lastY); //TODO CHASER
                } else if (mode.equals(Mode.COLLIDE)) {
                    observer.checkCollisionAndBounce(Pair.create(lastX, lastY), momentum); //TODO Collisions
                }
            }

            momentum.velocity -= 0.1;
            return super.move();

//            PositionedBitmap ret = new PositionedBitmap();
//            ret.image = iterateSprite();
//
//            //check borders and recalculate
//            if (lastX <= 0) {
//                momentum.direction.coeficientX = - momentum.direction.coeficientX;
//                lastX = 0;
//            } else if (lastX >= borderX) {
//                momentum.direction.coeficientX = - momentum.direction.coeficientX;
//                lastX = borderX;
//            }
//
//            if (lastY <= 0) {
//                momentum.direction.coeficientY = - momentum.direction.coeficientY;
//                lastY = 0;
//            } else if (lastY >= borderY) {
//                momentum.direction.coeficientY = - momentum.direction.coeficientY;
//                lastY = borderY;
//            }
//
//            lastX += momentum.velocity * (momentum.direction.coeficientX * 0.01f);
//            lastY += momentum.velocity * (momentum.direction.coeficientY * 0.01f);
//
//            ret.positionX = lastX;
//            ret.positionY = lastY;
//
//            momentum.velocity -= 0.1;
//            return ret;

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
