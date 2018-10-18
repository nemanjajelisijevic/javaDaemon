package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

public class ImageViewImpl implements ImageView, Comparable<ImageView> {

    private volatile int zIndex;
    private volatile boolean showing;

    private volatile Image image;
    private volatile float absoluteX;
    private volatile float absoluteY;

    private float xOffset;
    private float yOffset;

    public ImageViewImpl() {}

    public ImageViewImpl(
            int zIndex,
            boolean showing,
            float absoluteX,
            float absoluteY,
            float xOffset,
            float yOffset
    ) {
        this.zIndex = zIndex;
        this.showing = showing;
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

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
    public ImageViewImpl setAbsoluteX(float absoluteX) {
        this.absoluteX = absoluteX;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setAbsoluteY(float absoluteY) {
        this.absoluteY = absoluteY;
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
    public float getAbsoluteX() {
        return absoluteX;
    }

    @Override
    public float getAbsoluteY() {
        return absoluteY;
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
    public boolean checkCoordinates(float x, float y) {
        if (x >= (getAbsoluteX() - getxOffset()) && x <= (getAbsoluteX() + getxOffset())) {
            if (y >= (getAbsoluteY() - getyOffset()) && y <= (getAbsoluteY() + getyOffset()))
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(ImageView o) {
        if (o instanceof ImageViewImpl)
            return Integer.compare(this.zIndex, ((ImageViewImpl) o).zIndex);
        else
            return 0;
    }

    @Override
    public void addChild(ImageView child) {
        throw new IllegalStateException("Cannot add child to this type of ImageView");
    }

    @Override
    public void addChild(ImageView child, Pair<Integer, Integer> coordinates) {//
        throw new IllegalStateException("Cannot add child to this type of ImageView");
    }
}
