package com.daemonize.javafxmain;

import com.daemonize.game.images.Image;
import com.daemonize.game.images.imageloader.ImageLoader;

import java.io.IOException;

public class JavaFxImageLoader implements ImageLoader {

    private String pathToAssetsFromRoot;

    public JavaFxImageLoader(String pathToAssetsFromRoot) {
        this.pathToAssetsFromRoot = pathToAssetsFromRoot;
    }

    @Override
    public Image loadImageFromAssets(String name, int width, int height) throws IOException {
        return new JavaFxImage(new javafx.scene.image.Image("file:" + pathToAssetsFromRoot + name, width, height, false, false));
    }
}
