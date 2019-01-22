package com.daemonize.daemondevapp.images.imageloader;

import com.daemonize.daemondevapp.images.Image;

import java.io.IOException;

public interface ImageLoader {
    Image loadImageFromAssets(String name, int width, int height) throws IOException;
}
