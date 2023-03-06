package com.daemonize.game;

import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.controller.MouseController;
import com.daemonize.game.controller.MovementController;
import com.daemonize.game.controller.MovementControllerDaemon;
import com.daemonize.game.app.DaemonApp;
import com.daemonize.game.grid.Field;
import com.daemonize.game.grid.Grid;
import com.daemonize.game.interactables.Interactible;
import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.FixedButton;
import com.daemonize.graphics2d.scene.views.FixedView;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;
import com.daemonize.imagemovers.ImageMover;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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
    private Grid<Interactible> grid;
    private int rows;
    private int columns;

    private ImageView[][] gridViewMatrix;

    private ImageView[][] gridZElevationViewMatrix;

    private int fieldWidth;

    private Image accessibleField;
    private Image inaccessibleField;
    private Image multipleZField;

    private Image[] digits;

    private volatile int currentElevation = 0;

    @FunctionalInterface
    public interface ZElevationController {
        void onZElevationChange(int zElevation);
    }

    public ZElevationController elevationController = new ZElevationController() {
        @Override
        public void onZElevationChange(int zElevation) {
            if (zElevation >= 0 && zElevation < 10)
                currentElevation = zElevation;
        }
    };

    //camera
    private Camera2D camera;

    //cmd parser
    //private CommandParserDaemon commandParser;

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
    private MouseController mouseController;

    public MouseController getMouseController() {
        return mouseController;
    }

    private PlayerDaemon centerPointer;

    private FixedButton saveButton;
    private Image saveButtonImage;

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
            this.multipleZField = imageManager.loadImageFromAssets("blueOctagon.png", fieldWidth, fieldWidth);

            this.digits = new Image[10];

            for (int i = 0; i < 10; i ++){
                digits[i] = imageManager.loadImageFromAssets(Integer.toString(i)+"b.png",fieldWidth / 2, fieldWidth / 2);
            }


            ImageView backgroundView = new ImageViewImpl("Background View")
                    .setAbsoluteX(borderX / 2)
                    .setAbsoluteY(borderY / 2)
                    .setImage(backgroundImage)
                    .setZindex(0)
                    .show();

            scene.addImageView(backgroundView);

            Image centerImage = imageManager.loadImageFromAssets("greenPhoton.png", cameraWidth / 100, cameraWidth / 100);

            ImageView centerView = scene.addImageView(new FixedView("Center View", cameraWidth / 2, cameraHeight /2, 5, cameraWidth, cameraHeight))
                    .setAbsoluteX(borderX / 2)
                    .setAbsoluteY(borderY / 2)
                    .setImage(centerImage)
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

            this.saveButtonImage = imageManager.loadImageFromAssets("ButtonSale.png", cameraWidth / 10, cameraHeight / 10);

            this.saveButton = new FixedButton(
                    "Save Button",
                    cameraWidth * 8 / 10,
                    cameraHeight * 8 / 10,
                    3,
                    saveButtonImage
            );
            this.saveButton.enable().show();

            scene.addImageView(saveButton);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        this.movementController = new MovementControllerDaemon(mainConsumer, movementController).setName("Movement movementController");
        this.mouseController = mouseController;

        this.fieldWidth = fieldWidth;

        this.rows = borderY / fieldWidth;
        this.columns = borderX / fieldWidth;

//        this.grid = new Grid<Interactible<PlayerDaemon>>(
//                rows,
//                columns,
//                Pair.create(0, 0),
//                Pair.create(rows - 1, columns - 1),
//                0,
//                0,
//                fieldWidth
//        );

        this.grid = new Grid<Interactible>(
                rows,
                columns,
                borderX,
                borderY
        ).setMapName("Maze map");

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

        gridZElevationViewMatrix = new ImageView[rows][columns];

        for (int j = 0; j < rows; ++j ) {
            for (int i = 0; i < columns; ++i)
                gridZElevationViewMatrix[j][i] = scene.addImageView(new ImageViewImpl("Field Z Elev View [" + j + "][" + i +"]"))
                        .setAbsoluteX(grid.getGrid()[j][i].getCenterX())
                        .setAbsoluteY(grid.getGrid()[j][i].getCenterY())
                        .setImage(digits[0])
                        .setZindex(1)
                        .hide();
        }

        ((ClickController) this.mouseController).setCamera(camera);
        this.mouseController.setConsumer(mainConsumer);

        this.movementController.setControlSideQuest();
        this.movementController.setControllable(centerPointer);

        this.renderer.setScene(scene.lockViews());
    }

    @Override
    public MapEditor run() {
        mainConsumer.start().consume(() -> {
//            commandParser = new CommandParserDaemon(new CommandParser(this));
//            commandParser.setParseSideQuest();
//            commandParser.start();
            renderer.start();
            movementController.start();
            mainConsumer.consume(stateChain::run);
        });
        return this;
    }

    {
        stateChain.addState(() -> { //controller setup state

            ((ClickController) mouseController).addButton(saveButton);

            mouseController.setOnClick((x, y, mouseButton) -> {

                Field currentField = grid.getField(x, y);

                if(currentField == null)
                    return;

                if (mouseButton.equals(MouseController.MouseButton.LEFT)) {

                    currentField.addZElevation(currentElevation);

                    if (currentField.zElevationsSize() > 1) {
                        grid.multipleZFieldSet.add(currentField);
                        renderer.consume(() -> {
                            gridViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(multipleZField);
                        });
                    } else {
                        renderer.consume(() -> {
                            gridViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(accessibleField);
                            gridZElevationViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(digits[currentElevation]).show();
                        });
                    }

                    currentField.setWalkable(true);

                } else if (mouseButton.equals(MouseController.MouseButton.RIGHT)) {

                    currentField.clearElevations();

                    if (grid.multipleZFieldSet.contains(currentField))
                        grid.multipleZFieldSet.remove(currentField);

                    currentField.setWalkable(false);

                    renderer.consume(() -> {
                        gridViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(inaccessibleField);
                        gridZElevationViewMatrix[currentField.getRow()][currentField.getColumn()].setImage(digits[0]).hide();
                    });

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

            saveButton.onClick(()-> {
                System.out.println(DaemonUtils.tag() + saveButton.getName() + "clicked");
            });


            Field firstField = grid.getField(rows / 2, columns / 2);

            centerPointer.rotateTowards(firstField.getCenterX(), firstField.getCenterY())
                    .go(firstField.getCenterX(), firstField.getCenterY(), 2F);

            saveButton.onClick(() -> {
//
//                ObjectMapper objectmapper = new ObjectMapper();
//
//                try {
//                    objectmapper.writeValue(Paths.get("E:\\test.json").toFile(), grid);
//                } catch (IOException e) {
//                    throw new IllegalStateException(e);
//                }


            });

            DummyDaemon.create(mainConsumer, 1000).setClosure(() -> {



                for (Field current : grid.multipleZFieldSet) {

                    int currentZElev = current.iterateElevate();

                    renderer.consume(() -> {
                        gridZElevationViewMatrix[current.getRow()][current.getColumn()]
                                .setImage(digits[currentZElev]);
                    });


                }

            }).start();


        });
    }
}

