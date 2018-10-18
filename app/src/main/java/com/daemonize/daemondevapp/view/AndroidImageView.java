package com.daemonize.daemondevapp.view;

import android.graphics.Bitmap;
import android.view.View;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

public class AndroidImageView implements ImageView {

    private android.widget.ImageView view;
    public AndroidImageView(android.widget.ImageView view) {
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView setAbsoluteX(float absoluteX) {
        view.setX(absoluteX);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView setAbsoluteY(float absoluteY) {
        view.setY(absoluteY);
        return this;
    }

    @Override
    public Image getImage() { throw new IllegalStateException("Can not get android Bitmap from ImageView");}

    @Override
    public float getAbsoluteX() {
        return view.getX();
    }

    @Override
    public float getAbsoluteY() {
        return view.getY();
    }

    @Override
    public float getxOffset() {
        return 0;
    }

    @Override
    public float getyOffset() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView setImageWithoutOffset(Image image) {
        return setImage(image);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView setImage(Image image) {
        view.setImageBitmap((Bitmap)image.getImageImp());
        view.invalidate();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView hide() {
        view.setVisibility(View.INVISIBLE);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView show() {
        view.setVisibility(View.VISIBLE);
        return this;
    }

    @Override
    public <K extends ImageView> K setZindex(int zindex) {
        throw new IllegalStateException("Stub");
    }

    @Override
    public int getZindex() {
        throw new IllegalStateException("Stub");
    }

    @Override
    public boolean isShowing() {
        return view.isShown();
    }

    @Override
    public int compareTo(ImageView o) {
        return 0;
    }

    @Override
    public boolean checkCoordinates(float x, float y) {
        return false;
    }

    @Override
    public void addChild(ImageView child) {
        throw new IllegalStateException("Cannot add child to this type of ImageView");
    }

    @Override
    public void addChild(ImageView child, Pair<Integer, Integer> coordinates) {
        throw new IllegalStateException("Cannot add child to this type of ImageView");
    }
}
