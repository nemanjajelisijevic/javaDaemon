package com.daemonize.game.scene.views;

import com.daemonize.game.images.Image;

import java.util.List;

public interface ImageView extends Comparable<ImageView> {

    String getName();

    <K extends ImageView> K setAbsoluteX(float absoluteX);
    <K extends ImageView> K setAbsoluteY(float absoluteY);

    float getAbsoluteX();
    float getAbsoluteY();

    float getStartingX();
    float getStartingY();

    float getEndX();
    float getEndY();

    float getxOffset();
    float getyOffset();

    float getWidth();
    float getHeight();

    <K extends ImageView> K setImageWithoutOffset(Image image);
    <K extends ImageView> K setImage(Image image);
    Image getImage();

    <K extends ImageView> K hide();
    <K extends ImageView> K show();

    <K extends ImageView> K setZindex(int zindex);
    int getZindex();

    boolean isShowing();
    boolean checkCoordinates(float x, float y);

    @Override
    int compareTo(ImageView o);

    List<ImageView> getAllViews ();

}
