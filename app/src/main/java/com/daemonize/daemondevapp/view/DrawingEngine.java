package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.images.Image;

public interface DrawingEngine {

    <K extends DrawingEngine> K setWindowSize(int x, int y);
    <K extends DrawingEngine> K setBackgroundImage(Image image);
    <K extends DrawingEngine> K start();
    <K extends DrawingEngine> K stop();

}
