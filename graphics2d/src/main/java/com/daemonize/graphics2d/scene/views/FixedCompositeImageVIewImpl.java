package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.SceneDrawer;

public class FixedCompositeImageVIewImpl extends CompositeImageViewImpl {

    private final float fixedX, fixedY;

    public FixedCompositeImageVIewImpl(String name, float fixedX, float fixedY, int z, Image image) {
        super(name,fixedX, fixedY, z, image);
        this.fixedX = fixedX;
        this.fixedY = fixedY;
    }

    public FixedCompositeImageVIewImpl(String name, int z, float width, float height, float fixedX, float fixedY) {
        super(name, fixedX, fixedY, z, width, height);
        this.fixedX = fixedX;
        this.fixedY = fixedY;
    }

    @Override
    public float getStartingX() {
        return fixedX;
    }

    @Override
    public float getStartingY() {
        return fixedY;
    }

    @Override
    public void draw(SceneDrawer sceneDrawer) {
        if (view.isShowing())
            sceneDrawer.drawView(this, fixedX - view.xOffset, fixedY - view.yOffset);
    }
}
