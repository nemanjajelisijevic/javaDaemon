package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.SceneDrawer;

import java.util.List;

public interface ImageView<V extends ImageView> extends Comparable<ImageView> {

    String getName();

    V setAbsoluteX(float absoluteX);
    V setAbsoluteY(float absoluteY);

    float getAbsoluteX();
    float getAbsoluteY();

    float getRenderingX();
    float getRenderingY();

    float getEndX();
    float getEndY();

    float getxOffset();
    float getyOffset();

    float getWidth();
    float getHeight();

    V setImageWithoutOffset(Image image);
    V setImage(Image image);
    Image getImage();

    V hide();
    V show();

    V setZindex(int zindex);
    int getZindex();

    boolean isShowing();
    boolean checkCoordinates(float x, float y);

    @Override
    int compareTo(ImageView o);

    List<ImageView> getAllViews ();

    void draw(SceneDrawer sceneDrawer);
}

