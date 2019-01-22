package com.daemonize.daemondevapp.images.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.daemonize.daemondevapp.images.AndroidBitmapImage;
import com.daemonize.daemondevapp.images.Image;

import java.io.IOException;

public class AndroidImageLoader implements ImageLoader {

    private Context context;

    public AndroidImageLoader(Context context) {
        this.context = context;
    }

    @Override
    public Image loadImageFromAssets(String name, int width, int height) throws IOException {
        return new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(context.getAssets().open(name)), width, height, false));
    }
}
