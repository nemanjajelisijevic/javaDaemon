package com.daemonize.game;

import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.game.controller.MouseController;
import com.daemonize.game.controller.MouseControllerDaemon;
import com.daemonize.game.controller.MovementController;
import com.daemonize.game.controller.MovementControllerDaemon;
import com.daemonize.game.game.DaemonApp;
import com.daemonize.game.grid.Field;
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
import java.util.List;
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
    private Grid<Interactable<PlayerDaemon>> grid;
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


    public MovementControllerDaemon getMovementController() {
        return movementController;
    }

    //mouse controller
    private MouseControllerDaemon mouseController;

    public MouseControllerDaemon getMouseController() {
        return mouseController;
    }

    private PlayerDaemon centerPointer;

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

        this.dXY = ((float) cameraWidth) / 1000;
        this.borderX = cameraWidth * cameraToMapRatio;
        this.borderY = cameraHeight * cameraToMapRatio;

        try {

            this.backgroundImage = imageManager.loadImageFromAssets(mapName, borderX, borderY);
            this.accessibleField = imageManager.loadImageFromAssets("greenOctagon.png", fieldWidth, fieldWidth);
            this.inaccessibleField = imageManager.loadImageFromAssets("redOctagon.png", fieldWidth, fieldWidth);

            ImageView backgroundView = new ImageViewImpl("Background View")
                    .setAbsoluteX(borderX / 2)
                    .setAbsoluteY(borderY / 2)
                    .setImage(backgroundImage)
                    .setZindex(0)
                    .show();

            scene.addImageView(backgroundView);

            Image centerImage = imageManager.loadImageFromAssets("greenPhoton.png", cameraWidth / 50, cameraWidth / 50);

            ImageView centerView = scene.addImageView(new ImageViewImpl("Center View"))
                    .setAbsoluteX(borderX / 2)
                    .setAbsoluteY(borderY / 2)
                    .setImage(centerImage)
                    .setZindex(5)
                    .show();

            this.centerPointer = new PlayerDaemon(
                    mainConsumer,
                    new Player(
                            new Image[]{centerImage},
                            null,
                            null,
                            Pair.create((float)(borderX / 2), (float) (borderY / 2)),
                            dXY,
                            cameraWidth / 2,
                            cameraHeight / 2,
                            100,
                            10
                    )
            ).setName("Center pointer");

            centerPointer.setAnimatePlayerSideQuest(renderer).setClosure(ret ->{
                ImageMover.PositionedImage[] positionedImages = ret.runtimeCheckAndGet();
                centerView.setAbsoluteX(positionedImages[0].positionX)
                        .setAbsoluteY(positionedImages[0].positionY)
                        .setImage(positionedImages[0].image);

            });
            centerPointer.start();

            movementController.setControllable(centerPointer);

            this.camera = new FollowingCamera(cameraWidth, cameraHeight).setTarget(centerPointer);
            this.renderer.setCamera(this.camera);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        this.movementController = new MovementControllerDaemon(mainConsumer, movementController).setName("Movement movementController");
        this.mouseController = new MouseControllerDaemon(mainConsumer, mouseController).setName("Mouse Controller");

        this.fieldWidth = fieldWidth;

        this.rows = borderY / fieldWidth;
        this.columns = borderX / fieldWidth;

        this.grid = new Grid<Interactable<PlayerDaemon>>(
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

        //this.centerPointer = new CenterPointerDaemon(mainConsumer, new CenterPointer(Pair.create((float) borderX / 2, (float) borderY / 2), dXY));

        ((ClickController) this.mouseController.getPrototype()).setCamera(camera);
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

               Field currentField = grid.getField(x, y);

               if(currentField == null)
                    return;

               if (mouseButton.equals(MouseController.MouseButton.LEFT)) {

                   currentField.setWalkable(true);
                   renderer.consume(() -> gridViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(accessibleField));

               } else if (mouseButton.equals(MouseController.MouseButton.RIGHT)) {

                   currentField.setWalkable(false);
                   renderer.consume(() -> gridViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(inaccessibleField));

               }
           });

           movementController.setDirMapper(dir -> {

               Field currentField = grid.getField(
                       centerPointer.getLastCoordinates().getFirst(),
                       centerPointer.getLastCoordinates().getSecond()
               );

               Pair<Float, Float> ret = null;

               if (currentField.getRow() == 0) {
                    if(dir.equals(MovementController.Direction.UP ) || dir.equals(MovementController.Direction.UP_LEFT) || dir.equals(MovementController.Direction.UP_RIGHT))
                        return Pair.create(currentField.getCenterX(), currentField.getCenterY());
               } else if (currentField.getRow() == rows - 1) {
                   if(dir.equals(MovementController.Direction.DOWN ) || dir.equals(MovementController.Direction.DOWN_LEFT) || dir.equals(MovementController.Direction.DOWN_RIGHT))
                       return Pair.create(currentField.getCenterX(), currentField.getCenterY());
               } else if (currentField.getColumn() == 0) {
                   if(dir.equals(MovementController.Direction.LEFT ) || dir.equals(MovementController.Direction.DOWN_LEFT) || dir.equals(MovementController.Direction.UP_LEFT))
                       return Pair.create(currentField.getCenterX(), currentField.getCenterY());
               } else if (currentField.getColumn() == columns - 1) {
                   if(dir.equals(MovementController.Direction.RIGHT ) || dir.equals(MovementController.Direction.DOWN_RIGHT) || dir.equals(MovementController.Direction.UP_RIGHT))
                       return Pair.create(currentField.getCenterX(), currentField.getCenterY());

                   //return Pair.create(currentField.getCenterX(), currentField.getCenterY());
               }

               List<Field> neighbors = grid.getNeighbors(currentField);

               switch (dir) {
                   case UP:
                       ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                       break;
                   case DOWN:
                       ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
                       break;
                   case RIGHT:
                       ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                       break;
                   case LEFT:
                       ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                       break;
                   case UP_RIGHT:
                       ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                       break;
                   case UP_LEFT:
                       ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                       break;
                   case DOWN_RIGHT:
                       ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                       break;
                   case DOWN_LEFT:
                       ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                       break;
                   default:
                       throw new IllegalStateException("No dir: " + dir);

               }

               return ret;
           });


            Field firstField = grid.getField(rows / 2, columns / 2);

            centerPointer.rotateTowards(firstField.getCenterX(), firstField.getCenterY())
                    .go(firstField.getCenterX(), firstField.getCenterY(), 2F);


        });
    }
}

