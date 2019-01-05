package com.daemonize.daemondevapp.renderer;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.scene.view.ImageView;

public interface Renderer {

    <K extends Renderer> K setWindowSize(int x, int y);
    <K extends Renderer> K setBackgroundImage(Image image);
    <K extends Renderer> K start();
    <K extends Renderer> K stop();

    ImageView createImageView(int zIndex);
    int viwesSize();
}
