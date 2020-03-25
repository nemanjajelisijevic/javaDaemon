package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.controller.DirectionControllerDaemon;
import com.daemonize.game.grid.Grid;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;
import com.daemonize.imagemovers.ImageMover;

import java.io.IOException;
import java.util.Random;

import daemon.com.commandparser.CommandParser;
import daemon.com.commandparser.CommandParserDaemon;

public class ShooterGame {

    //animate closure def
    private static class PlayerCameraClosure implements Closure<ImageMover.PositionedImage[]> {

        private ImageView mainView, hpView;

        public PlayerCameraClosure(ImageView mainView, ImageView hpView) {
            this.mainView = mainView;
            this.hpView = hpView;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage[]> ret) {
            ImageMover.PositionedImage[] result = ret.runtimeCheckAndGet();
            mainView.setAbsoluteX(result[0].positionX)
                    .setAbsoluteY(result[0].positionY)
                    .setImage(result[0].image);
            hpView.setAbsoluteX(result[1].positionX)
                    .setAbsoluteY(result[1].positionY)
                    .setImage(result[1].image);
        }
    }

    private static class CameraImageAnimateClosure implements Closure<ImageMover.PositionedImage> {

        private ImageView view;
        private CameraDaemon camera;

        public CameraImageAnimateClosure(CameraDaemon camera, ImageView view) {
            this.camera = camera;
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage> aReturn) {
            ImageMover.PositionedImage posBmp = aReturn.get();
            view.setAbsoluteX(posBmp.positionX - camera.getX())
                    .setAbsoluteY(posBmp.positionY - camera.getY())
                    .setImage(posBmp.image);
        }
    }

    private static class CameraMultiViewAnimateClosure implements Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {

        private CameraDaemon camera;

        public CameraMultiViewAnimateClosure(CameraDaemon camera) {
            this.camera = camera;
        }

        @Override
        public void onReturn(Return<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> aReturn) {
            GenericNode.forEach(aReturn.runtimeCheckAndGet(), arg -> {
                ImageMover.PositionedImage image = arg.getFirst();
                arg.getSecond().setAbsoluteX(image.positionX - camera.getX())
                        .setAbsoluteY(image.positionY - camera.getY())
                        .setImage(image.image);
            });
        }
    }

    //running flag
    private volatile boolean running;

    //pause flag
    private volatile boolean paused;

    //game consumer threads
    private Renderer2D renderer;
    private DaemonConsumer gameConsumer;

    //image loader
    private ImageManager imageManager;

    //daemonState holder
    private DaemonChainScript stateChain = new DaemonChainScript();

    //Scene
    private Scene2D scene;

    //BackgroundImage
    private Image backgroundImage;
    private ImageView backgroundView;

    //map borders
    private Integer borderX;
    private Integer borderY;

    //screen borders
    int cameraWidth, cameraHeight;

    //grid
    private Grid<TowerDaemon> grid;
    private int rows;
    private int columns;
    private ImageView[][] gridViewMatrix;

    //camera
    private CameraDaemon camera;

    //cmd parser
    private CommandParserDaemon commandParser;

    //random int
    private Random random = new Random();

    private int getRandomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    //resolution scaling attribute
    private float dXY;

    //player
    private PlayerDaemon player;

    private Image[] playerSprite;
    private Image[] healthBarSprite;

    //controller
    private DirectionControllerDaemon controller;

    public DirectionControllerDaemon getController() {
        return controller;
    }

    //construct
    public ShooterGame(Renderer2D renderer, ImageManager imageManager, int width, int height) {

        this.renderer = renderer;
        this.imageManager = imageManager;

        this.gameConsumer = new DaemonConsumer("Game Consumer");

        this.cameraWidth = width;
        this.cameraHeight = height;

        int screenToMapRatio = 5;
        this.borderX = width * screenToMapRatio;
        this.borderY = height * screenToMapRatio;

        this.camera = new CameraDaemon(gameConsumer, new Camera(width, height, borderX, borderY));

        this.scene = new Scene2D();
        this.dXY = ((float) cameraWidth) / 1000;

        this.controller = new DirectionControllerDaemon(gameConsumer, new PlayerController(null)).setName("Player Controller");
    }

    public ShooterGame run() {

        gameConsumer.start().consume(() -> {

            this.running = true;
            this.paused = false;
            commandParser = new CommandParserDaemon(new CommandParser(this));
            commandParser.setParseSideQuest();
            //commandParser.start();
            gameConsumer.consume(stateChain::run);
        });

        return this;
    }

    {
        stateChain.addState(()->{ //image loading

            try {

                backgroundImage = imageManager.loadImageFromAssets(
                        "map_1.png",
                        this.borderX,
                        this.borderY
                );

                ImageView backgroundView = new ImageViewImpl("Background View")
                        .setAbsoluteX(borderX / 2)
                        .setAbsoluteY(borderY / 2)
                        .setImage(backgroundImage)
                        .setZindex(0)
                        .show();

                scene.addImageView(backgroundView);

                camera.addStaticView(backgroundView);

                //init player sprites
                int enemyWidth = cameraWidth / 10;
                int enemyHeight = cameraHeight / 10;

                playerSprite = new Image[36];

                for (int i = 0; i < 36; i++) {
                    playerSprite[i] = imageManager.loadImageFromAssets(
                            "plane" + i + "0.png",
                            enemyWidth,
                            enemyHeight
                    );
                }

                int width_hp = (enemyWidth * 3) / 4;
                int height_hp = enemyHeight / 5;

                healthBarSprite = new Image[10];
                for (int i = 0; i < healthBarSprite.length; ++i) {
                    healthBarSprite[i] = imageManager.loadImageFromAssets(
                            "health_bar_" + (i + 1) + "0.png",
                            width_hp, height_hp
                    );
                }

                //init player
                player = new PlayerDaemon(
                        gameConsumer,
                        new Player(
                                playerSprite,
                                healthBarSprite,
                                Pair.create((float)(borderX / 2), (float) (borderY / 2)),
                                dXY,
                                cameraWidth / 2,
                                cameraHeight / 2,
                                100,
                                100
                        )
                ).setName("Player");

                {
                    ImageView mainView = scene.addImageView(new ImageViewImpl("Player Main View"))
                            .setImage(playerSprite[0])
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2)
                            .setZindex(10);

                    ImageView hpView = scene.addImageView(new ImageViewImpl("Player HP View"))
                            .setImage(healthBarSprite[0])
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2)
                            .setZindex(10);

                    renderer.consume(() -> {
                        mainView.show();
                        hpView.show();
                    });

                    player.setAnimatePlayerSideQuest(renderer).setClosure(new PlayerCameraClosure(mainView, hpView));
                }

                renderer.setScene(scene.lockViews()).start();

                camera.setFollowSideQuest();
                camera.setRenderer(renderer).setTarget(player).start();

                controller.setPrototype(new PlayerController(player.start()));
                controller.setControlSideQuest();
                controller.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
