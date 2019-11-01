package com.daemonize.javafxmain;

import com.daemonize.game.Game;

import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.javafxgraphics2d.JavaFXRenderer;
import com.daemonize.javafxgraphics2d.JavaFxImageManager;
import com.daemonize.javafxsound.JavaFxSoundManager;
import com.daemonize.sound.SoundManager;

import javafx.application.Application;

import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {

    private Game game;

    @Override
    public void start(Stage primaryStage) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////


        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        int borderX = (int) primaryScreenBounds.getWidth() / 2 > 800 ? (int) primaryScreenBounds.getWidth() / 2 : 800;
        //int borderY = 200;

        int rows = 6;
        int columns = 9;

        int gridWidth = (borderX * 70) / 100;

        int width = gridWidth / columns;
        int height = width; //160

        int borderY = (rows + 2) * height;

        Canvas canvas = new Canvas(borderX, borderY);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Renderer2D renderer = new JavaFXRenderer(gc, borderX, borderY);
        ImageManager imageManager = new JavaFxImageManager("");

        SoundManager soundManager = new JavaFxSoundManager(4);

        game = new Game(renderer, imageManager, soundManager, borderX, borderY, rows, columns,50,50);

        Group root = new Group(canvas);
        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);
        primaryStage.setTitle("Tower Defense");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        scene.setCursor(Cursor.HAND);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> game.onTouch((float) event.getSceneX(), (float) event.getSceneY()));

//        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//
//            switch (event.getCode()) {
//
//                case A:
//                    game.left();
//                    break;
//                case S:
//                    game.down();
//                    break;
//                case D:
//                    game.right();
//                    break;
//                case W:
//                    game.up();
//                    break;
//                case SPACE:
//                    game.startStopTracer();
//                    break;
//                default:
//                    break;
//
//            }
//
//        });

//            scene.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
//                if (game.isPaused())
//                    game.cont();
//            });
//
//            scene.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
//                if (!game.isPaused())
//                    game.pause();
//            });

        if(!game.isRunning())
            game.run();

    }

    @Override
    public void stop() throws Exception {
        game.stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

