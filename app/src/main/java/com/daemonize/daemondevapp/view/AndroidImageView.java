package com.daemonize.daemondevapp.view;

import android.graphics.Bitmap;
import android.view.View;

import com.daemonize.daemondevapp.images.Image;

public class AndroidImageView implements ImageView {

    private android.widget.ImageView view;
    public AndroidImageView(android.widget.ImageView view) {
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView setX(float x) {
        view.setX(x);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidImageView setY(float y) {
        view.setY(y);
        return this;
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
}
