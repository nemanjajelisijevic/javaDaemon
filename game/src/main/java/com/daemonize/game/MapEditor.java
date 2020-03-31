package com.daemonize.game;

import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.game.controller.MouseController;
import com.daemonize.game.controller.MouseControllerDaemon;
import com.daemonize.game.controller.MovementController;
import com.daemonize.game.controller.MovementControllerDaemon;
import com.daemonize.game.game.DaemonApp;
import com.daemonize.game.grid.Grid;
import com.daemonize.game.interactables.Interactable;
import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;

import java.io.IOException;
import java.util.Random;

import daemon.com.commandparser.CommandParser;
import daemon.com.commandparser.CommandParserDaemon;

public class MapEditor implements DaemonApp<MapEditor> {

    //game consumer threads
    private Renderer2D renderer;
    private DaemonConsumer mainConsumer;

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
    private Grid<Interactable<CenterPointerDaemon>> grid;
    private int rows;
    private int columns;
    private ImageView[][] gridViewMatrix;

    private int fieldWidth;

    private Image accessibleField;
    private Image inaccessibleField;

    //camera
    private Camera2D camera;

    //cmd parser
    private CommandParserDaemon commandParser;

    //random int
    private Random random = new Random();

    private int getRandomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    //resolution scaling attribute
    private float dXY;

    //movement movementController
    private MovementControllerDaemon movementController;

    //mouse controller
    private MouseControllerDaemon mouseController;

    @Daemon(implementPrototypeInterfaces = true)
    public static class CenterPointer extends CoordinatedImageTranslationMover implements Movable {
        public CenterPointer(Pair<Float, Float> startingPos, float dXY) {
            super(new Image[]{null}, startingPos, dXY);
        }
    }

    private CenterPointerDaemon centerPointer;

    public MapEditor(
            Renderer2D renderer,
            ImageManager imageManager,
            MovementController movementController,
            MouseController mouseController,
            String mapName,
            int cameraWidth,
            int cameraHeight,
            int fieldWidth,
            int cameraToMapRatio
    ) {

        this.mainConsumer = new DaemonConsumer("Main Consumer");
        this.renderer = renderer;
        this.imageManager = imageManager;
        this.scene = new Scene2D();

        this.borderX = cameraWidth * cameraToMapRatio;
        this.borderY = cameraHeight * cameraToMapRatio;

        try {

            this.backgroundImage = imageManager.loadImageFromAssets(mapName, borderX, borderY);
            this.accessibleField = imageManager.loadImageFromAssets("greenOctagon.png", fieldWidth, fieldWidth);
            this.inaccessibleField = imageManager.loadImageFromAssets("redOctagon.png", fieldWidth, fieldWidth);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        this.camera = new FollowingCamera(cameraWidth, cameraHeight);
        this.dXY = ((float) cameraWidth) / 1000;

        movementController.setControllable(
                new CenterPointerDaemon(
                        mainConsumer,
                        new CenterPointer(
                                Pair.create(
                                        ((float) cameraWidth) / 2,
                                        ((float) cameraHeight) / 2
                                ),
                                dXY
                        )
                ).setName("Center pointer").start()
        );

        this.movementController = new MovementControllerDaemon(mainConsumer, movementController).setName("Movement movementController");
        this.mouseController = new MouseControllerDaemon(mainConsumer, mouseController).setName("Mouse Controller");

        this.fieldWidth = fieldWidth;

        this.rows = borderY / fieldWidth;
        this.columns = borderX / fieldWidth;

        this.grid = new Grid<Interactable<CenterPointerDaemon>>(
                rows,
                columns,
                Pair.create(0, 0),
                Pair.create(rows - 1, columns - 1),
                0,
                0,
                fieldWidth
        );

        gridViewMatrix = new ImageView[rows][columns];

        for (int j = 0; j < rows; ++j ) {
            for (int i = 0; i < columns; ++i)
                gridViewMatrix[j][i] = scene.addImageView(new ImageViewImpl("Field View [" + j + "][" + i +"]"))
                        .setAbsoluteX(grid.getGrid()[j][i].getCenterX())
                        .setAbsoluteY(grid.getGrid()[j][i].getCenterY())
                        .setImage(inaccessibleField)
                        .setZindex(1)
                        .show();
        }

        this.centerPointer = new CenterPointerDaemon(mainConsumer, new CenterPointer(Pair.create((float) borderX / 2, (float) borderY / 2), dXY));

        this.mouseController.setControlSideQuest();
        this.movementController.setControlSideQuest();
        this.movementController.setControllable(centerPointer);

        this.renderer.setScene(scene.lockViews());
    }

    @Override
    public MapEditor run() {
        mainConsumer.start().consume(() -> {
            commandParser = new CommandParserDaemon(new CommandParser(this));
            commandParser.setParseSideQuest();
            commandParser.start();
            renderer.start();
            movementController.start();
            mouseController.start();
            mainConsumer.consume(stateChain::run);
        });
        return this;
    }

    {
        stateChain.addState(() -> { // controller setup state

           mouseController.setOnClick((x, y, mouseButton) -> {

                if (mouseButton.equals(MouseController.MouseButton.LEFT)) {



                } else if (mouseButton.equals(MouseController.MouseButton.RIGHT)) {



                }

           });

           movementController.setDirMapper(dir -> {




                return null;
           });

        });
    }
}

