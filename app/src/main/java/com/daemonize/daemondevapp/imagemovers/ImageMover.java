package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;

@Daemonize(returnDaemonInstance = true)
public interface ImageMover {

    class Velocity {
        public float intensity;
        public Direction direction;

        public Velocity(float intensity, Direction direction) {
            this.intensity = intensity;
            this.direction = direction;
        }

        //copy construct
        public Velocity(Velocity other) {
            this.intensity = other.intensity;
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

    @CallingThread
    Pair<Float, Float> getLastCoordinates();

    @CallingThread
    Velocity getVelocity();

    PositionedBitmap setLastCoordinates(float lastX, float lastY);

    void checkCollisionAndBounce(Pair<Float, Float> colliderCoordinates, Velocity velocity);

    void setDirection(Direction direction);

    void setVelocity(Velocity velocity);

    void setTouchDirection(float x, float y, float velocityInt);

    void setVelocity(float velocity);

    @CallingThread
    <K extends ImageMover> K setBorders(float x, float y);

    @CallingThread
    void pause();

    @CallingThread
    void resume();

    @SideQuest(SLEEP = 25)
    PositionedBitmap move();

    PositionedBitmap explode(List<Bitmap> explodeSprite, Closure<PositionedBitmap> update) throws InterruptedException;

}
