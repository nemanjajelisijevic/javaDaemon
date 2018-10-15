package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.images.Image;

public interface ImageView extends Comparable<ImageView> {

    <K extends ImageView> K setX(float x);
    <K extends ImageView> K setY(float y);

    float getX();
    float getY();

    float getxOffset();
    float getyOffset();

    <K extends ImageView> K setImageWithoutOffset(Image image);
    <K extends ImageView> K setImage(Image image);
    Image getImage();

    <K extends ImageView> K hide();
    <K extends ImageView> K show();

    <K extends ImageView> K setZindex(int zindex);
    int getZindex();

    boolean isShowing();

    @Override
    int compareTo(ImageView o);
}
