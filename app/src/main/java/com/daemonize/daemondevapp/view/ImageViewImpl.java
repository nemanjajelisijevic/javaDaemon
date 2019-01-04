package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.images.Image;

public class ImageViewImpl implements ImageView, Comparable<ImageViewImpl> {

    private volatile int zIndex;
    private volatile boolean showing;

    private volatile Image image;
    private volatile float x;
    private volatile float y;

    private float xOffset;
    private float yOffset;

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setZindex(int zindex) {
        this.zIndex = zindex;
        return this;
    }

    @Override
    public int getZindex() {
        return zIndex;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setX(float x) {
        this.x = x;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setY(float y) {
        this.y = y;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setImage(Image image) {
        this.image = image;
        this.xOffset = image.getWidth() / 2;
        this.yOffset = image.getHeight() / 2;
        return this;
    }

    public Image getImage() {
        return image;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl hide() {
        this.showing = false;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl show() {
        this.showing = true;
        return this;
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public int compareTo(ImageViewImpl o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
