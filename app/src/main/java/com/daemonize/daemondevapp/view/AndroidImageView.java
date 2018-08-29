package com.daemonize.daemondevapp.view;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.daemonize.daemondevapp.images.Image;

public class AndroidImageView implements DaemonView {

    private ImageView view;
    public AndroidImageView(ImageView view) {
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
}
