package com.daemonize.graphics2d.camera;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class FixedCamera implements Camera2D<FixedCamera> {

    private volatile int x, y;
    private final int cameraWidth, cameraHeight;

    public FixedCamera(int x, int y,int cameraWidth, int cameraHeight) {
        this.x = x;
        this.y = y;
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
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
    public int getRenderingX() {
        return x;
    }

    @Override
    public int getRenderingY() {
        return y;
    }

    @Override
    public int getCenterX() {
        return x + cameraWidth / 2;
    }

    @Override
    public int getCenterY() {
        return y + cameraHeight / 2;
    }

    @Override
    public int getWidth() {
        return cameraWidth;
    }

    @Override
    public int getHeight() {
        return cameraHeight;
    }
}
