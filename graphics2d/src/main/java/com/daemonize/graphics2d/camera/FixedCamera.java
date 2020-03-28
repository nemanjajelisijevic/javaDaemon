package com.daemonize.graphics2d.camera;

public class FixedCamera implements Camera2D {

    private final int x, y;

    public FixedCamera(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }
}
