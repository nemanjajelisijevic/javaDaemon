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

    @Override
    public Image[] cutSpriteSheet(String name, int rows, int columns, int imageWidth, int imageHeight) throws IOException {
        AndroidBitmapImage spriteSheet = ((AndroidBitmapImage) loadImageFromAssets(name, imageWidth, imageHeight));

        int x = 0;
        int y = 0;

        int pieceWidth = imageWidth / columns;
        int pieceHeight = imageHeight / rows;

        Image[] sprite = new Image[rows * columns];

        int cnt = 0;

        for(int i =0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                sprite[cnt++] = new AndroidBitmapImage(Bitmap.createBitmap(spriteSheet.getImageImp(), x, y, pieceWidth, pieceHeight));
                x = j * pieceWidth;
                y = i * pieceHeight;
            }
        }


        return sprite;
    }
}
