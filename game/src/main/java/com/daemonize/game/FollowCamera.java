package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.Movable;

import java.util.ArrayList;
import java.util.List;

public class FollowCamera implements Camera2D {

    private final int xOffset, yOffset;
    private Movable target;

    public FollowCamera(int width, int height/*, int borderX, int borderY*/) {
        this.xOffset = width / 2;
        this.yOffset = height / 2;
    }

    @Override
    public int getX() {
        return target.getLastCoordinates().getFirst().intValue() - xOffset;
    }

    @Override
    public int getY() {
        return target.getLastCoordinates().getSecond().intValue() - yOffset;
    }

    public FollowCamera setTarget(Movable target) {
        this.target = target;
        return this;
    }
}
