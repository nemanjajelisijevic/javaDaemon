package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class FullColliderImageMover extends BouncingImageTranslationMover {

    private float proximityDistance = 50;
    private List<ImageMoverDaemon> obs = new ArrayList<>(40);

    private MainImageTranslationMover.Mode mode;

    public FullColliderImageMover(
            List<Bitmap> sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            MainImageTranslationMover.Mode mode
    ) {
        super(sprite, velocity, startingPos);
        this.mode = mode;
    }

    public void setObserver(ImageMoverDaemon observers) {
        this.obs.add(observers);
    }

    public void checkCollisionAndBounce(
            Pair<Float, Float> colliderCoordinates,
            float velocity,
            Direction direction
    ) {

        if(Math.abs(lastX - colliderCoordinates.first) < proximityDistance
                && Math.abs(lastY - colliderCoordinates.second) < proximityDistance) {
            setVelocity(velocity);
            setDirection(new Direction(
                    (direction.coeficientX + currentDirX) / 2 ,
                    (direction.coeficientY + currentDirY) / 2
            ));
        }
    }

    @Override
    public PositionedBitmap move() {
        if (velocity > 0 && mode.equals(MainImageTranslationMover.Mode.COLLIDE)) {
            for (ImageMoverDaemon imageMoverDaemon : obs) {
                if (!imageMoverDaemon.getPrototype().equals(this)) {
                    imageMoverDaemon.getPrototype().checkCollisionAndBounce(
                                Pair.create(lastX, lastY),
                                velocity,
                                new Direction(currentDirX, currentDirY)
                        ); //TODO Collisions
                }
            }
            return super.move();
        }
        return null;
    }
}
