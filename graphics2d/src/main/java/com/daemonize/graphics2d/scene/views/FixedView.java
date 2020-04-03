package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.scene.SceneDrawer;

public class FixedView extends ImageViewImpl {

    private final int fixedX, fixedY;

    public FixedView(String name, int fixedX, int fixedY, int zIndex, float width, float height) {
        super(name, zIndex, fixedX, fixedY, width, height);
        this.fixedX = fixedX;
        this.fixedY = fixedY;
    }

    @Override
    public float getRenderingX() {
        return fixedX;
    }

    @Override
    public float getRenderingY() {
        return fixedY;
    }

    @Override
    public float getEndX() {
        return fixedX + 2 * xOffset;
    }

    @Override
    public float getEndY() {
        return fixedY + 2 * yOffset;
    }

    @Override
    public void draw(SceneDrawer sceneDrawer) {
        if (showing)
            sceneDrawer.drawView(this, fixedX - xOffset, fixedY - yOffset);
    }
}