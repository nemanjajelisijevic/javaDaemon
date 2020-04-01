package com.daemonize.game;

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
    public int getX() {
        return
                target
                        .getLastCoordinates()
                        .getFirst()
                        .intValue()
                        - xOffset;
    }

    @Override
    public int getY() {
        return target.getLastCoordinates().getSecond().intValue() - yOffset;
    }

    public FollowingCamera setTarget(Movable target) {
        this.target = target;
        return this;
    }
}
