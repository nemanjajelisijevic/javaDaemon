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
            Velocity velocity
    ) {

        if(!isExploading() && Math.abs(lastX - colliderCoordinates.first) < proximityDistance
                && Math.abs(lastY - colliderCoordinates.second) < proximityDistance) {
            setVelocity(new Velocity(velocity));
        }
    }

    @Override
    public BouncingImageTranslationMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public PositionedBitmap move() {

        try {

            awaitForMovement();

            if(velocity.intensity > 0) {

                velocity.intensity -= 0.3;

                PositionedBitmap ret = new PositionedBitmap();
                ret.image = iterateSprite();

                //check borders and recalculate
                if (lastX <= 0) {
                    velocity.direction.coeficientX = - velocity.direction.coeficientX;
                    lastX = 0;
                } else if (lastX >= borderX) {
                    velocity.direction.coeficientX = - velocity.direction.coeficientX;
                    lastX = borderX;
                }

                if(lastY <= 0) {
                    velocity.direction.coeficientY = - velocity.direction.coeficientY;
                    lastY = 0;
                } else if( lastY >= borderY) {
                    velocity.direction.coeficientY = - velocity.direction.coeficientY;
                    lastY = borderY;
                }


                lastX += velocity.intensity * (velocity.direction.coeficientX * 0.01f);
                lastY += velocity.intensity * (velocity.direction.coeficientY * 0.01f);


                ret.positionX = lastX;
                ret.positionY = lastY;

                return ret;

            }

        } catch (InterruptedException e) {
            //
        }

        return null;
    }
}
