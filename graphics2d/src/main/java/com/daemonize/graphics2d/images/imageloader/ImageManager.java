package com.daemonize.graphics2d.images.imageloader;

import com.daemonize.graphics2d.images.Image;

import java.io.IOException;

public interface ImageManager {
    Image loadImageFromAssets(String name, int width, int height) throws IOException;
    Image rescaleImage(Image image, int width, int height);
}
