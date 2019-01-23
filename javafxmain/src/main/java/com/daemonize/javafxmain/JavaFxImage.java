package com.daemonize.javafxmain;

import com.daemonize.game.images.Image;

public class JavaFxImage implements Image<javafx.scene.image.Image>{

    private javafx.scene.image.Image image;

    public JavaFxImage(javafx.scene.image.Image image) {
        this.image = image;
    }

    @Override
    public int getWidth() {
        return (int) image.getWidth();
    }

    @Override
    public int getHeight() {
        return (int) image.getHeight();
    }

    @Override
    public javafx.scene.image.Image getImageImp() {
        return image;
    }
}
