package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.collider.PositionUpdate;
import com.daemonize.daemonprocessor.CallingThread;
import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.LogExecutionTime;
import com.daemonize.daemonprocessor.SideQuest;
import com.daemonize.daemonprocessor.TimeUnits;

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

    @CallingThread
    void updatePosition(int id, PositionUpdate update);

    PositionedBitmap setLastCoordinates(float lastX, float lastY);

    void checkCollisionAndBounce(Pair<Float, Float> colliderCoordinates, float velocity, Direction direction);

    void setDirection(Direction direction);

    void setTouchDirection(float x, float y);

    void setVelocity(float velocity);

    @CallingThread
    <K extends ImageMover> K setBorders(float x, float y);
    //void addBorders(Border border);

    void pause();

    void resume();

    @SideQuest(SLEEP = 25)
    PositionedBitmap move();

}
