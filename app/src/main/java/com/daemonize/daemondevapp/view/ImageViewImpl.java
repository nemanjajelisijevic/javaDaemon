package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.images.Image;

public class ImageViewImpl implements ImageView {

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
    public ImageViewImpl setImageWithoutOffset(Image image) {
        this.image = image;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setImage(Image image) {
        this.xOffset = image.getWidth() / 2;
        this.yOffset = image.getHeight() / 2;
        return setImageWithoutOffset(image);
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
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
    public int compareTo(ImageView o) {
        if (o instanceof ImageViewImpl)
            return Integer.compare(this.zIndex, ((ImageViewImpl) o).zIndex);
        else
            return 0;
    }
}
