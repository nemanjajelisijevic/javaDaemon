package com.daemonize.daemondevapp.imagemovers.collider;

import com.daemonize.daemondevapp.imagemovers.ImageMover;

public class PositionUpdate {

    private volatile boolean isAlive;
    private volatile float x;
    private volatile float y;
    private volatile float velocity;
    private ImageMover.Direction direction;

    public boolean isAlive() {
        return isAlive;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelocity() {
        return velocity;
    }

    public ImageMover.Direction getDirection() {
        return direction;
    }

    public PositionUpdate(boolean isAlive, float x, float y, float velocity, ImageMover.Direction direction) {
        this.isAlive = isAlive;
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.direction = direction;
    }
}
