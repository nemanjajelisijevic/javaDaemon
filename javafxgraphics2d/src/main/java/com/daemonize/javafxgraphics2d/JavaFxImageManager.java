package com.daemonize.javafxgraphics2d;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;

import java.io.IOException;

import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class JavaFxImageManager implements ImageManager {

    private String pathToAssetsFromRoot;

    public JavaFxImageManager(String pathToAssetsFromRoot) {
        this.pathToAssetsFromRoot = pathToAssetsFromRoot;
    }

    @Override
    public Image loadImageFromAssets(String name, int width, int height) throws IOException {
        return new JavaFxImage(new javafx.scene.image.Image(/*"file:" + */getClass().getResource("/" + name).toString(), width, height, false, true));
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

    public Image[] cutSpriteSheet(String name, int rows, int columns, int imageWidth, int imageHeight) throws IOException {
        JavaFxImage spriteSheet = ((JavaFxImage) loadImageFromAssets(name, imageWidth, imageHeight));

        PixelReader reader = spriteSheet.getImageImp().getPixelReader();
        int x = 0;
        int y = 0;

        int pieceWidth = imageWidth / columns;
        int pieceHeight = imageHeight / rows;

        Image[] sprite = new Image[rows * columns];

        int cnt = 0;

        for(int i =0; i < rows; ++i) {
            for(int j = 0; j < columns; ++j) {
                WritableImage piece = new WritableImage(reader, x, y, pieceWidth, pieceHeight);
                sprite[cnt++] = new JavaFxImage(piece);
                x = j * pieceWidth;
                y = i * pieceHeight;
            }
        }


        return sprite;
    }
}
