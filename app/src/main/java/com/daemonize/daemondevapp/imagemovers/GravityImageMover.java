package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

public class GravityImageMover extends ImageTranslationMover {

    private boolean touched = false;
    private boolean falling = false;

    public GravityImageMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }

    @Override
    public GravityImageMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public void setDirectionAndMove(float x, float y, float velocityInt) {
        velocity.intensity = initVelocity;
        touched = true;
        super.setDirectionAndMove(x, y, velocityInt);
    }

    @Override
    public PositionedBitmap animate() {

            if(velocity.intensity <= 0) {
                falling = true;
                setDirectionAndMove(lastX, borderY, velocity.intensity);
                velocity.intensity = initVelocity;
                return super.animate();
            }

            if (Math.abs(lastY - borderY) < 50) {
                falling = false;
                if (touched) {
                    touched = false;
                } else {
                    return null;
                }
            }

            if(!falling)
                velocity.intensity -= 0.3;

            return super.animate();
    }
}
