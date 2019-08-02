package com.daemonize.javafxgraphics2d;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;

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
    public Image rescaleImage(Image image, int width, int height) {//TODO FIX
        javafx.scene.image.Image original = (javafx.scene.image.Image)image.getImageImp();
        javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(original);
//        imgView.setFitWidth(width);
//        imgView.setFitHeight(height);
        imgView.setScaleX(width/original.getWidth());
        imgView.setScaleY(height/original.getHeight());
        return new JavaFxImage(imgView.getImage());
    }
}
