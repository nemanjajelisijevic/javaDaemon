package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

import java.util.List;

public interface ImageView extends Comparable<ImageView> {

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
