package com.daemonize.daemondevapp.view;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class AndroidImageView implements DaemonView {

    private ImageView view;
    public AndroidImageView(ImageView view) {
        this.view = view;
    }

    @Override
    public void setX(float x) {
        view.setX(x);
    }

    @Override
    public void setY(float y) {
        view.setY(y);
    }

    @Override
    public void setImage(Bitmap image) {
        view.setImageBitmap(image);
    }
}
