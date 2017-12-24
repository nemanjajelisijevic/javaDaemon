package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;

@Daemonize(returnDaemonInstance = true)
public interface ImageMover {

    class PositionedBitmap {
        public Bitmap image;
        public float positionX;
        public float positionY;
    }

    class Direction {

        public float coeficientX;
        public float coeficientY;

        public Direction(float coeficientX, float coeficientY) {
            this.coeficientX = coeficientX;
            this.coeficientY = coeficientY;
        }
    }

    void checkCollisionAndBounce(Pair<Float, Float> colliderCoordinates, float velocity, Direction direction);

    void setDirection(Direction direction);

    void setTouchDirection(float x, float y);

    void setVelocity(float velocity);

    void setBorders(float x, float y);

    void pause();

    void resume();

    @SideQuest(SLEEP = 30)
    PositionedBitmap move();

}
