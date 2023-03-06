package com.daemonize.graphics2d.scene;

import com.daemonize.graphics2d.scene.views.ImageView;

public interface SceneDrawer {
    void drawView(ImageView view, float x, float y);
    void drawView(ImageView view);
    void drawScene(Scene2D scene2D);
}

