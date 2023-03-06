package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.imagemovers.Movable;

public class FollowingCamera implements Camera2D<FollowingCamera> {

    private final int xOffset, yOffset;
    private Movable target;

    public FollowingCamera(int width, int height/*, int borderX, int borderY*/) {
        this.xOffset = width / 2;
        this.yOffset = height / 2;
    }

    @Override
    public FollowingCamera setX(int x) {
        throw new IllegalStateException("Cannot mutate FollowingCamera");
    }

    @Override
    public FollowingCamera setY(int y) {
        throw new IllegalStateException("Cannot mutate FollowingCamera");
    }

    @Override
    public int getRenderingX() {
        return target.getLastCoordinates().getFirst().intValue() - xOffset;
    }

    @Override
    public int getRenderingY() {
        return target.getLastCoordinates().getSecond().intValue() - yOffset;
    }

    @Override
    public int getCenterX() {
        return target.getLastCoordinates().getFirst().intValue();
    }

    @Override
    public int getCenterY() {
        return target.getLastCoordinates().getSecond().intValue();
    }

    public FollowingCamera setTarget(Movable target) {
        this.target = target;
        return this;
    }

    @Override
    public int getWidth() {
        return 2 * xOffset;
    }

    @Override
    public int getHeight() {
        return 2 * yOffset;
    }
}
