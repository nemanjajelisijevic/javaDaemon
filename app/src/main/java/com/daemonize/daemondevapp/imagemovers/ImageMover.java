package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemonize(returnDaemonInstance = true)
public interface ImageMover {

    class Momentum {
        public float velocity;
        public Direction direction;

        public Momentum(float velocity, Direction direction) {
            this.velocity = velocity;
            this.direction = direction;
        }

        //copy construct
        public Momentum(Momentum other) {
            this.velocity = other.velocity;
            this.direction = new Direction(other.direction.coeficientX, other.direction.coeficientY);
        }
    }

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

    PositionedBitmap setLastCoordinates(float lastX, float lastY);

    void checkCollisionAndBounce(Pair<Float, Float> colliderCoordinates, Momentum momentum);

    void setDirection(Direction direction);

    void setMomentum(Momentum momentum);

    void setTouchDirection(float x, float y);

    void setVelocity(float velocity);

    @CallingThread
    <K extends ImageMover> K setBorders(float x, float y);

    @CallingThread
    void pause();

    @CallingThread
    void resume();

    @SideQuest(SLEEP = 25)
    PositionedBitmap move();

}
