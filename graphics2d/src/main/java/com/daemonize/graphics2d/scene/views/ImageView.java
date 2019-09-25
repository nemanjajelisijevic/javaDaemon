package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.images.Image;

import java.util.List;

public interface ImageView extends Comparable<ImageView> {

    String getName();

    <V extends ImageView> V setAbsoluteX(float absoluteX);
    <V extends ImageView> V setAbsoluteY(float absoluteY);

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

    <V extends ImageView> V setImageWithoutOffset(Image image);
    <V extends ImageView> V setImage(Image image);
    Image getImage();

    <V extends ImageView> V hide();
    <V extends ImageView> V show();

    <V extends ImageView> V setZindex(int zindex);
    int getZindex();

    boolean isShowing();
    boolean checkCoordinates(float x, float y);

    @Override
    int compareTo(ImageView o);

    List<ImageView> getAllViews ();

}
