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
            Momentum momentum
    ) {

        if(Math.abs(lastX - colliderCoordinates.first) < proximityDistance
                && Math.abs(lastY - colliderCoordinates.second) < proximityDistance) {
            setMomentum(momentum);
        }
    }

    @Override
    public BouncingImageTranslationMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public PositionedBitmap move() {
        if(momentum.velocity > 0) {
            momentum.velocity -= 0.3;
            return super.move();
        }
        return null;
    }
}
