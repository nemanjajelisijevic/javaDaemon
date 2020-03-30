package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.scene.SceneDrawer;

public class FixedView extends ImageViewImpl {

    private final int fixedX, fixedY;

    public FixedView(String name, int fixedX, int fixedY) {
        super(name);
        this.fixedX = fixedX;
        this.fixedY = fixedY;
    }

    @Override
    public void draw(SceneDrawer sceneDrawer) {
        sceneDrawer.drawView(this, fixedX - xOffset, fixedY - yOffset);
    }
}