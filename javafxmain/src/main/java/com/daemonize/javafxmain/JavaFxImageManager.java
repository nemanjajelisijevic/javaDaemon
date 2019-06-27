package com.daemonize.javafxmain;

import com.daemonize.game.images.Image;
import com.daemonize.game.images.imageloader.ImageManager;

import java.io.IOException;

public class JavaFxImageManager implements ImageManager {

    private String pathToAssetsFromRoot;

    public JavaFxImageManager(String pathToAssetsFromRoot) {
        this.pathToAssetsFromRoot = pathToAssetsFromRoot;
    }

    @Override
    public Image loadImageFromAssets(String name, int width, int height) throws IOException {
        return new JavaFxImage(new javafx.scene.image.Image(/*"file:" + */getClass().getResource("/" + name).toString(), width, height, false, false));
    }

    @Override
    public Image rescaleImage(Image image, int width, int height) {
        javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView((javafx.scene.image.Image)image.getImageImp());
        imgView.setFitWidth(width);
        imgView.setFitHeight(height);
        return new JavaFxImage(imgView.getImage());
    }
}
