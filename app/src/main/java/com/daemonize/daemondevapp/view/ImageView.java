package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.images.Image;

public interface ImageView {

    <K extends ImageView> K setX(float x);
    <K extends ImageView> K setY(float y);
    <K extends ImageView> K setImage(Image image);
    <K extends ImageView> K hide();
    <K extends ImageView> K show();
    <K extends ImageView> K setZindex(int zindex);
    int getZindex();
    boolean isShowing();
    boolean checkCoordinates(float x, float y);

}
