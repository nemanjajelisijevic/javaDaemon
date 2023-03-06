package com.daemonize.graphics2d.camera;

public interface Camera2D<C extends Camera2D> {

    C setX(int x);
    C setY(int y);

    int getRenderingX();
    int getRenderingY();

    int getCenterX();
    int getCenterY();

    int getWidth();
    int getHeight();
}
