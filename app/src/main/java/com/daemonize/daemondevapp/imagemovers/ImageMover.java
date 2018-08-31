package com.daemonize.daemondevapp.imagemovers;


import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;


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

    class PositionedImage {
        public Image image;
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

    PositionedImage setLastCoordinates(float lastX, float lastY);

    void setDirection(Direction direction);

    void setVelocity(Velocity velocity);

    void setDirectionAndMove(float x, float y, float velocityInt);

    void setVelocity(float velocity);

    @CallingThread
    <K extends ImageMover> K setBorders(float x, float y);

    @SideQuest(SLEEP = 25)
    PositionedImage animate();

}
