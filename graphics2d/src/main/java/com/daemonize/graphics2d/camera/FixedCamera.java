package com.daemonize.graphics2d.camera;

public class FixedCamera implements Camera2D<FixedCamera> {

    private volatile int x, y;

    public FixedCamera(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public FixedCamera setX(int x) {
        this.x = x;
        return this;
    }

    @Override
    public FixedCamera setY(int y) {
        this.y = y;
        return this;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
