package com.daemonize.androidgraphics2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;

import java.io.IOException;

public class AndroidImageManager implements ImageManager {

    private Context context;

    public AndroidImageManager(Context context) {
        this.context = context;
    }

    @Override
    public Image loadImageFromAssets(String name, int width, int height) throws IOException {
        return new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getClass().getResource("/" + name).openStream()), width, height, false));
    }

    @Override
    public Image rescaleImage(Image image, int width, int height) {
        return new AndroidBitmapImage(Bitmap.createScaledBitmap((Bitmap)image.getImageImp(), width, height, false));
    }
}
