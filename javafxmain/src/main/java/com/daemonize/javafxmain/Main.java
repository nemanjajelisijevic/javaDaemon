package com.daemonize.javafxmain;

import com.daemonize.game.KeyBoardMovableController;
import com.daemonize.game.KeyBoardMovementControllerImpl;
import com.daemonize.game.PlayerDaemon;
import com.daemonize.game.ShooterGame;
import com.daemonize.game.controller.KeyboardMovementController;
import com.daemonize.game.controller.MovementController;
import com.daemonize.game.app.DaemonApp;
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


public class Main extends Application {

    private DaemonApp game;
    private KeyboardMovementController<PlayerDaemon> controller;

    @Override
    public void start(Stage primaryStage) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        int cameraWidth = ((int) primaryScreenBounds.getWidth()) - 300;

        //int borderX = Math.min((int)primaryScreenBounds.getWidth(), ((int) primaryScreenBounds.getHeight()));

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

        int cameraHeight = cameraWidth / 2;

        //int borderY = borderX * 3 / 4;
        //int borderY = (rows + 2) * height;

        //int borderY = ((int) primaryScreenBounds.getHeight()) - 200;

        Canvas canvas = new Canvas(cameraWidth, cameraHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Renderer2D renderer = new JavaFXRenderer(gc, cameraWidth, cameraHeight, 200);
        ImageManager imageManager = new JavaFxImageManager("");

        //SoundManager soundManager = new JavaFxSoundManager(4);
        //game = new TowerDefenseGame(renderer, imageManager, soundManager, borderX, borderY, rows, columns,50,50);



        //controller = new KeyBoardMovementControllerImpl();

        controller = new KeyBoardMovableController<PlayerDaemon>();

        game = new ShooterGame(renderer, imageManager, controller, cameraWidth, cameraHeight, 5);

//        game = new MapEditor(
//                renderer,
//                imageManager,
//                controller,
//                new ClickController(),
//                "map_1.png", cameraWidth, cameraHeight, cameraWidth / 20, 5);

        Group root = new Group(canvas);
        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);
        //primaryStage.setTitle("Tower Defense");
        primaryStage.setTitle("Shooter Game");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        //primaryStage.initStyle(StageStyle.UNDECORATED);
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


        //controller = ((KeyboardMovementController) ((MapEditor) game).getMovementController().getPrototype());

        controller = ((KeyboardMovementController) ((ShooterGame) game).getMovementController().getPrototype());

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            switch(event.getCode()) {

                case W:
                    controller.pressDirection(MovementController.Direction.UP);
                    break;
                case S:
                    controller.pressDirection(MovementController.Direction.DOWN);
                    break;
                case A:
                    controller.pressDirection(MovementController.Direction.LEFT);
                    break;
                case D:
                    controller.pressDirection(MovementController.Direction.RIGHT);
                    break;
                case SPACE:
                    controller.speedUp();
                    break;
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            switch(event.getCode()) {

                case W:
                    controller.releaseDirection(MovementController.Direction.UP);
                    break;
                case S:
                    controller.releaseDirection(MovementController.Direction.DOWN);
                    break;
                case A:
                    controller.releaseDirection(MovementController.Direction.LEFT);
                    break;
                case D:
                    controller.releaseDirection(MovementController.Direction.RIGHT);
                    break;
                case SPACE:
                    controller.speedDown();
                    break;
            }
        });

//        MouseController mouseControllerDaemon = ((MapEditor) game).getMouseController();
//
//        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
//
//            if(event.getButton().equals(MouseButton.PRIMARY)) {
//                mouseControllerDaemon.onClick(MouseController.MouseButton.LEFT, ((float) event.getRenderingX()), ((float) event.getRenderingY()));
//            } else if(event.getButton().equals(MouseButton.SECONDARY)) {
//                mouseControllerDaemon.onClick(MouseController.MouseButton.RIGHT, ((float) event.getRenderingX()), ((float) event.getRenderingY()));
//            }
//
//        });
//
//        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
//
//            if(event.getButton().equals(MouseButton.PRIMARY)) {
//                mouseControllerDaemon.onRelease(MouseController.MouseButton.LEFT);
//            } else if(event.getButton().equals(MouseButton.SECONDARY)) {
//                mouseControllerDaemon.onRelease(MouseController.MouseButton.RIGHT);
//            }
//
//        });
//
//        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
//            mouseControllerDaemon.onMove(((float) event.getRenderingX()), ((float) event.getRenderingY()));
//        });

    }

    @Override
    public void stop() throws Exception {
        //g
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

