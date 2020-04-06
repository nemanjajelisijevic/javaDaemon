package com.daemonize.javafxgraphics2d;

import com.daemonize.daemonengine.utils.DaemonUtils;
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
        return new JavaFxImage(
                new javafx.scene.image.Image(
                        /*"file:" + */getClass().getResource("/" + name).toString(),
                        width,
                        height,
                        false,
                        true
                )
        );
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

    public Image[] loadSheet(String name, int rows, int columns, int pieceWidth, int pieceHeight) throws IOException {

        int originalWidth = columns * pieceWidth;
        int originalHeight = rows * pieceHeight;

        Image[] ret = new Image[rows * columns];

        int currentX = 0, currentY = 0;

        String url = /*"file:" + */getClass().getResource("/" + name).toString();

        javafx.scene.image.Image original = new javafx.scene.image.Image(
                url,
                originalWidth,
                originalHeight,
                false,
                true
        );

        PixelReader reader = original.getPixelReader();

        for(int r = 0; r < rows; ++r) {
            for(int c =0; c < columns; ++c) {

                int currentIndex = r * columns + c;

//                System.err.println(DaemonUtils.tag()
//                        + "Current index: " + currentIndex
//                        + ", ROw: " + r
//                        + ", Column: " + c
//                        + ", CurrentX: " + currentX
//                        + ", CurrentY: " + currentY
//                + ", originalWidth: " + original.getWidth()
//                + ", originalHeight: " + original.getHeight());


                WritableImage piece = new WritableImage(reader, currentX, currentY, pieceWidth, pieceHeight);
                ret[currentIndex] = new JavaFxImage(piece);
                currentX += pieceWidth;
            }
            currentX = 0;
            currentY += pieceHeight;
        }

        return ret;
    }

}
