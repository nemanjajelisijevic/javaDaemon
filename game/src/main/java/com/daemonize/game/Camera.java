package com.daemonize.game;


import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.graphics2d.renderer.DrawConsumer;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.Movable;

import java.util.ArrayList;
import java.util.List;

@Daemon
public class Camera {

    private volatile int x, y;

    private final int borderXOffset, borderYOffset;
    private final int xOffset, yOffset;

    private Movable target;
    private List<ImageView> staticViews;
    private Renderer2D renderer;

    public Camera(int width, int height, int borderX, int borderY) {
        this.xOffset = width / 2;
        this.yOffset = height / 2;
        this.borderXOffset = borderX / 2;
        this.borderYOffset = borderY / 2;
        this.staticViews = new ArrayList<>(1);
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public Camera addStaticView(ImageView view) {
        this.staticViews.add(view);
        return this;
    }

    public Camera setTarget(Movable target) {
        this.target = target;
        this.x = target.getLastCoordinates().getFirst().intValue();
        this.y = target.getLastCoordinates().getSecond().intValue();
        return this;
    }

    public Camera setRenderer(Renderer2D renderer) {
        this.renderer = renderer;
        return this;
    }

    @SideQuest(SLEEP = 25)
    public void follow() throws InterruptedException {

        target.getAnimationWaiter().await();

        Pair<Float, Float> targetLastCoordinates = target.getLastCoordinates();

        int currentTargetX = targetLastCoordinates.getFirst().intValue();
        int currentTargetY = targetLastCoordinates.getSecond().intValue();

        x = currentTargetX - xOffset;
        y = currentTargetY - yOffset;

        renderer.consume(() -> {
            for (ImageView staticView : staticViews)
                staticView.setAbsoluteX(borderXOffset - x)
                        .setAbsoluteY(borderYOffset - y);
        });
    }
}
