package com.daemonize.javafxmain;

import com.daemonize.game.ShooterGame;
import com.daemonize.game.controller.DirectionController;
import com.daemonize.game.controller.DirectionControllerDaemon;
import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.javafxgraphics2d.JavaFXRenderer;
import com.daemonize.javafxgraphics2d.JavaFxImageManager;

import javafx.application.Application;

import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    //private Game game;
    private ShooterGame game;

    @Override
    public void start(Stage primaryStage) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //int borderX = ((int) primaryScreenBounds.getWidth()) - 200;

        int borderX = Math.min((int)primaryScreenBounds.getWidth(), ((int) primaryScreenBounds.getHeight()));

        //(int)primaryScreenBounds.getWidth() > ((int) primaryScreenBounds.getHeight()) ? ((int) primaryScreenBounds.getHeight()) : (int)primaryScreenBounds.getWidth();
        //(int) primaryScreenBounds.getWidth() / 2 > 800 ? (int) primaryScreenBounds.getWidth() / 2 : 800;
        //int borderY = 200;
//
//        int rows = 6;
//        int columns = 9;
//
//        int gridWidth = (borderX * 70) / 100;
//
//        int width = gridWidth / columns;
//        int height = width; //160

        int borderY = borderX * 3 / 4;
        //int borderY = (rows + 2) * height;

        //int borderY = ((int) primaryScreenBounds.getHeight()) - 200;

        Canvas canvas = new Canvas(borderX, borderY);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Renderer2D renderer = new JavaFXRenderer(gc, borderX, borderY, 200);
        ImageManager imageManager = new JavaFxImageManager("");

        //SoundManager soundManager = new JavaFxSoundManager(4);
        //game = new Game(renderer, imageManager, soundManager, borderX, borderY, rows, columns,50,50);

        game = new ShooterGame(renderer, imageManager, borderX, borderY);

        Group root = new Group(canvas);
        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);
        //primaryStage.setTitle("Tower Defense");
        primaryStage.setTitle("Shooter Game");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

//        scene.setCursor(Cursor.HAND);
//        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> game.onTouch((float) event.getSceneX(), (float) event.getSceneY()));

//        scene.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
//            if (game.isPaused())
//                game.cont();
//        });
//
//        scene.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
//            if (!game.isPaused())
//                game.pause();
//        });



//        if(!game.isRunning())
//            game.run();


        game.run();


        DirectionControllerDaemon controller = game.getController();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            switch(event.getCode()) {

                case W:
                    controller.pressDirection(DirectionController.Direction.UP);
                    break;
                case S:
                    controller.pressDirection(DirectionController.Direction.DOWN);
                    break;
                case A:
                    controller.pressDirection(DirectionController.Direction.LEFT);
                    break;
                case D:
                    controller.pressDirection(DirectionController.Direction.RIGHT);
                    break;
                case SPACE:
                    controller.speedUp();
                    break;
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            switch(event.getCode()) {

                case W:
                    controller.releaseDirection(DirectionController.Direction.UP);
                    break;
                case S:
                    controller.releaseDirection(DirectionController.Direction.DOWN);
                    break;
                case A:
                    controller.releaseDirection(DirectionController.Direction.LEFT);
                    break;
                case D:
                    controller.releaseDirection(DirectionController.Direction.RIGHT);
                    break;
                case SPACE:
                    controller.speedDown();
                    break;
            }
        });
    }

    @Override
    public void stop() throws Exception {
        //game.stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

