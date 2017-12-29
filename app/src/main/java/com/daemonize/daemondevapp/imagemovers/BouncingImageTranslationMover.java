package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

public class BouncingImageTranslationMover extends ImageTranslationMover {

    public BouncingImageTranslationMover(
            List<Bitmap> sprite,
            float velocity,
            Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }

    public PositionedBitmap checkCollisionAndBounce(
            Pair<Float, Float> colliderCoordinates,
            float velocity,
            Direction direction
    ) {

        if(Math.abs(lastX - colliderCoordinates.first) < 50 && Math.abs(lastY - colliderCoordinates.second) < 50) {
            setVelocity(velocity);
            setDirection(new Direction(
                    (direction.coeficientX + currentDirX) / 2 ,
                    (direction.coeficientY + currentDirY) / 2
            ));

            return move();
        }

        return null;
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
