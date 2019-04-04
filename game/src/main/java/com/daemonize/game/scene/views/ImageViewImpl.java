package com.daemonize.game.scene.views;

import com.daemonize.game.images.Image;

import java.util.List;

public class ImageViewImpl implements ImageView, Comparable<ImageView> {

    protected volatile String viewName;
    private volatile int zIndex;
    private volatile boolean showing;

    private volatile Image image;

    //center coords
    protected float absoluteX;
    protected float absoluteY;

    protected volatile float startingX;
    protected volatile float startingY;

    protected float xOffset;//TODO check if this is really neccessary
    protected float yOffset;//TODO check if this is really neccessary

    public ImageViewImpl(String name) {
        this.viewName = name;
    }

    public ImageViewImpl(
            String name,
            int zIndex,
            float absoluteX,
            float absoluteY,
            float xOffset,
            float yOffset
    ) {
        this(name);
        this.zIndex = zIndex;
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.startingX = absoluteX - xOffset;
        this.startingY = absoluteY - yOffset;
    }

    @Override
    public String getName() {
        return viewName;
    }

    @Override
    public Image getImage() {
        return image;
    }

    public float getxOffset() {
        return xOffset;
    }

    public float getyOffset() {
        return yOffset;
    }

    @Override
    public float getStartingX() {
        return startingX;
    }

    @Override
    public float getStartingY() {
        return startingY;
    }

    @Override
    public float getEndX() {
        return absoluteX + xOffset;
    }

    @Override
    public float getEndY() {
        return absoluteY + yOffset;
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
        this.startingX = absoluteX - xOffset;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageViewImpl setAbsoluteY(float absoluteY) {
        this.absoluteY = absoluteY;
        this.startingY = absoluteY - yOffset;
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
        this.startingX = this.absoluteX - this.xOffset;
        this.startingY = this.absoluteY - this.yOffset;
        return setImageWithoutOffset(image);
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
        if (x >= getStartingX() && x <= getEndX()) {
            if (y >= getStartingY() && y <= getEndY())
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

    public List<ImageView> getAllViews () {
        throw new IllegalStateException("ImageViewImpl has no children.");
    }

    @Override
    public float getWidth() {
        return getEndX() - getStartingX();
    }

    @Override
    public float getHeight() {
        return getEndY() - getStartingY();
    }

    @Override
    public String toString() {
        return viewName + ", isShowing: " + showing + ", Z index: " + zIndex;
    }
}
