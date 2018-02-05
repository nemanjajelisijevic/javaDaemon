package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

public class BouncingImageTranslationMover extends ImageTranslationMover {

    private float proximityDistance = 50;

    public BouncingImageTranslationMover(
            List<Bitmap> sprite,
            float velocity,
            Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
        if (sprite != null && !sprite.isEmpty()) {
            proximityDistance = sprite.get(0).getHeight() > sprite.get(0).getWidth()
                    ? sprite.get(0).getHeight() / 2 : sprite.get(0).getWidth() / 2;
        }
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
    public BouncingImageTranslationMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public PositionedBitmap move() {
        if(velocity > 0) {
            velocity -= 0.3;
            return super.move();
        }
        return null;
    }
}
