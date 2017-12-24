package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;

@Daemonize
public interface ImageMover {

    class PositionedBitmap {
        public boolean wasted = false;
        public Bitmap image;
        public float positionX;
        public float positionY;
    }

    class Direction {

        public float coeficientX;
        public float coeficientY;

        public Direction(float coeficientX, float coeficientY) {

//            if ((coeficientX > 100 || coeficientX < -100) || (coeficientY > 100 || coeficientX < -100)) {
//                throw new IllegalArgumentException("coeficient invalid: X - " + String.valueOf(coeficientX) +  ", Y - " + String.valueOf(coeficientY));
//            }
//
//            if((Math.abs(coeficientX + coeficientY)) > 100) {
//                throw new IllegalArgumentException("Math.abs(coeficientX) + Math.abs(coeficientY) should be <= 100. X:" + String.valueOf(coeficientX) +  ", Y:" + String.valueOf(coeficientY));
//            }

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
