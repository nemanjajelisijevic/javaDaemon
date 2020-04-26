package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.daemonengine.quests.VoidQuest;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.game.controller.MouseController;
import com.daemonize.game.controller.MovementController;
import com.daemonize.game.controller.MovementControllerDaemon;
import com.daemonize.game.app.DaemonApp;
import com.daemonize.game.grid.Field;
import com.daemonize.game.grid.Grid;
import com.daemonize.game.interactables.Interactible;
import com.daemonize.game.interactables.health.HealthPack;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.FixedCompositeImageVIewImpl;
import com.daemonize.graphics2d.scene.views.FixedView;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;
import com.daemonize.imagemovers.AngleToSpriteArray;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.ImageTranslationMover;
import com.daemonize.imagemovers.Movable;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import daemon.com.commandparser.CommandParser;
import daemon.com.commandparser.CommandParserDaemon;
import javafx.fxml.FXMLLoader;

public class ShooterGame implements DaemonApp<ShooterGame> {

    private static class ZombieAnimateClosure implements Closure<ImageMover.PositionedImage> {

        private ImageView view;

        public ZombieAnimateClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage> ret) {
            ImageMover.PositionedImage posImg = ret.runtimeCheckAndGet();
            view.setAbsoluteX(posImg.positionX)
                    .setAbsoluteY(posImg.positionY)
                    .setImage(posImg.image);
        }
    }

    private static class ZombieSpriteAnimateClosure implements Closure<ImageMover.PositionedImage[]> {

        private ImageView view;

        public ZombieSpriteAnimateClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage[]> ret) {
            ImageMover.PositionedImage[] posImg = ret.runtimeCheckAndGet();
            view.setAbsoluteX(posImg[0].positionX)
                    .setAbsoluteY(posImg[0].positionY)
                    .setImage(posImg[0].image);
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

    @FunctionalInterface
    private interface SpriteSheetCutter {
        Image[] cut(int startIndex, int endIndex, Image[] spriteSheet);
    }

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
    private int cameraWidth, cameraHeight;

    private Image dark;

    //grid Interactibles
    public static class FieldContent implements Interactible {

        private Set<Target> targets;
        private Interactible interactible;

        public FieldContent subscribe(Target target) {
            targets.add(target);
            return this;
        }

        public FieldContent unsubscribe(Target target) {
            targets.remove(target);
            return this;
        }

        public int targetsSize() {
            return targets.size();
        }

        public Set<Target> getTargets() {
            return targets;
        }

        public FieldContent setInteractible(Interactible interactible) {
            this.interactible = interactible;
            return this;
        }

        public Interactible getInteractible() {
            return interactible;
        }

        public FieldContent() {
            this.targets = new HashSet<>();
        }

        @Override
        public boolean interact() {
            return interactible.interact();
        }
    }

    //grid
    private Grid<FieldContent> grid;
    private int rows;
    private int columns;
    //private ImageView[][] gridViewMatrix;

    private int fieldWidth;

    private Image accessibleField;
    private Image inaccessibleField;

    //following camera
    private FollowingCamera followingCamera;
    //fixed camera
    //private FixedCamera fixedCamera;

    //private DummyDaemon cameraSwitcher;

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
    private DummyPlayerDaemon dummyPlayer;

    @Daemon(eager = true)
    public static class PlayerRotationBlocker {
        @Daemonize
        public void block() throws InterruptedException {
            Thread.sleep(500);
        }
    }

    private PlayerRotationBlockerDaemon rotationBlocker;

    private Image[] explosionSprite;
    private Image[] miniExplosionSprite;

    private Image[] playerSprite;
    private Image[] healthBarSprite;
    private Image searchlight;

    private Map<String, ImageView> playerViewMap = new TreeMap<>();
    private PlayerCameraClosure playerAnimateClosure;

    private Image healthPackImage;
    private ImageView healthPackView;

    private List<Field<FieldContent>> healthPackFields;

    //zombie
    private Image[] zombieSprite;

    private Image[] zombieMove0;
    private Image[] zombieMove45;
    private Image[] zombieMove90;
    private Image[] zombieMove135;
    private Image[] zombieMove180;
    private Image[] zombieMove225;
    private Image[] zombieMove270;
    private Image[] zombieMove315;

    AngleToSpriteArray zombieFallAnimation;

    private ImageView[] zombieViews;
    //private List<SpriteAnimatorDaemon<ConstantSpriteAnimator>> zombieAnimators;

    //controller
    private MovementControllerDaemon controller;

    //mouse controller
    private MouseController aimController;

    public MovementControllerDaemon getMovementController() {
        return controller;
    }

    public MouseController getAimController() {
        return aimController;
    }

    //test
    //private UnholyTrinity<SpriteAnimatorDaemon<ConstantSpriteAnimator>> testTrinity = new UnholyTrinity<>();
    private UnholyTrinity<DummyDaemon> streetLamp = new UnholyTrinity<>();

    //bullets
    private Image bulletImage;
    private Queue<ImageView> bulletViews = new LinkedList<>();

    private Image crosshair;
    private ImageView crosshairView;

    private volatile int bulletDamage = 40;


    //construct
    public ShooterGame(
            Renderer2D renderer,
            ImageManager imageManager,
            MovementController controller,
            MouseController aimController,
            int width,
            int height,
            int cameraToMapRatio
    ) {

        this.renderer = renderer;
        this.imageManager = imageManager;

        this.gameConsumer = new DaemonConsumer("Shooter Game Consumer");

        this.cameraWidth = width;
        this.cameraHeight = height;


        this.followingCamera = new FollowingCamera(width, height);
        //this.fixedCamera = new FixedCamera(borderX / 2, borderY / 2 , width, height);

        this.scene = new Scene2D();
        this.dXY = ((float) cameraWidth) / 1000;

        this.controller = new MovementControllerDaemon(gameConsumer, controller).setName("Player controller");
        this.aimController = aimController;

        ((ClickController) this.aimController).setCamera(followingCamera);
        this.aimController.setConsumer(gameConsumer);
//
//        this.grid = new Grid<Interactible<PlayerDaemon>>(
//                rows,
//                columns,
//                Pair.create(0, 0),
//                Pair.create(rows - 1, columns - 1),
//                0,
//                0,
//                borderX / columns,
//                borderX,
//                borderY
//        );

        ObjectMapper gridLoader = new ObjectMapper();

        //URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();

        //        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
//        try {
//            String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
//            activeSoundManager.setJarResourceLocation(jarPath, "");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        InputStream in = getClass().getResourceAsStream("/test.json");

        try {

            this.grid = gridLoader.readValue(in, Grid.class).calculateFieldWidth();

            this.fieldWidth = grid.getFieldWidth();

            this.borderX = grid.getGridWidth();
            this.borderY = grid.getGridHeight();


            this.rows = borderY / fieldWidth;
            this.columns = borderX / fieldWidth;

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        this.healthPackFields = new LinkedList<>();
    }

    @Override
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
                        //"emptyMap.png", //"map_1.png",
                        "mazeMap.jpg",
                        grid.getGridWidth(),
                        grid.getGridHeight()
                );

                ImageView backgroundView = new ImageViewImpl("Background View")
                        .setAbsoluteX(borderX / 2)
                        .setAbsoluteY(borderY / 2)
                        .setImage(backgroundImage)
                        .setZindex(0)
                        .show();

                scene.addImageView(backgroundView);

                dark = imageManager.loadImageFromAssets("dark.png", cameraWidth *4 / 3, cameraHeight*4 / 3);

                FixedView darkView = new FixedView("Dark", cameraWidth / 2, cameraHeight / 2, 20, cameraWidth, cameraHeight);

                scene.addImageView(darkView.setImage(dark).show());

                //zombie sprite
                zombieSprite = imageManager.loadSheet("zombieSheet.png", 8, 36, cameraWidth / 7, cameraWidth / 7);

                SpriteSheetCutter sheetCutter = (start, end, sheet) -> {

                    Image[] ret = new Image[end - start];

                    for(int i = start; i < end; i++)
                        ret[i - start] = sheet[i];

                    return ret;
                };

                //controllable zombie
                zombieMove0 = sheetCutter.cut(148, 156, zombieSprite);
                zombieMove45 = sheetCutter.cut(112, 120, zombieSprite);
                zombieMove90 = sheetCutter.cut(76, 84, zombieSprite);
                zombieMove135 = sheetCutter.cut(40, 48, zombieSprite);
                zombieMove180 = sheetCutter.cut(4, 12, zombieSprite);
                zombieMove225 = sheetCutter.cut(256, 264, zombieSprite);
                zombieMove270 = sheetCutter.cut(220, 228, zombieSprite);
                zombieMove315 = sheetCutter.cut(184, 192, zombieSprite);

                explosionSprite = new Image[33];
                for (int i = 0; i < explosionSprite.length; ++i)
                    explosionSprite[i] = imageManager.loadImageFromAssets("Explosion" + (i + 1) + ".png", fieldWidth, fieldWidth);

                miniExplosionSprite = new Image[20];
                for (int i = 0; i < miniExplosionSprite.length; ++i) {
                    miniExplosionSprite[i] = imageManager.loadImageFromAssets("Bild-0000" + (i < 10 ? "0" + (i + 1) : (i + 1)) + ".png", fieldWidth / 2, fieldWidth / 2);
                }
                //init grid views
                accessibleField = imageManager.loadImageFromAssets("greenOctagon.png", fieldWidth, fieldWidth);
                inaccessibleField = imageManager.loadImageFromAssets("redOctagon.png", fieldWidth, fieldWidth);

                //init player sprites
                int playerWidth = cameraWidth / 10;
                int playerHeight = cameraHeight / 10;

                playerSprite = new Image[36];

                for (int i = 0; i < 36; i++) {
                    playerSprite[i] = imageManager.loadImageFromAssets(
                            "plane" + i + "0.png",
                            playerWidth,
                            playerHeight
                    );
                }

                int width_hp = (playerWidth * 3) / 4;
                int height_hp = playerHeight / 5;

                healthBarSprite = new Image[10];
                for (int i = 0; i < healthBarSprite.length; ++i) {
                    healthBarSprite[i] = imageManager.loadImageFromAssets(
                            "health_bar_" + (i + 1) + "0.png",
                            width_hp, height_hp
                    );
                }

                searchlight = imageManager.loadImageFromAssets("searchlight.png", playerWidth / 2, playerHeight);

                //test sprite
                Image[] explodeSprite = new Image[33];
                for (int i = 0; i < explodeSprite.length; ++i) {
                    explodeSprite[i] = imageManager.loadImageFromAssets("Explosion" + (i + 1) + ".png", playerWidth, playerHeight);
                }


                crosshair = imageManager.loadImageFromAssets("target.png", cameraWidth / 20, cameraWidth / 20);
                crosshairView = scene.addImageView(new ImageViewImpl("Crosshair View"))
                        .setAbsoluteX(Float.NaN)
                        .setAbsoluteY(Float.NaN)
                        .setImage(crosshair)
                        .hide();

                Image streetLampImage = imageManager.loadImageFromAssets("streetLamp.png", playerWidth, playerWidth );
                Image lampLightImage = imageManager.loadImageFromAssets("searchlight.png", playerWidth * 2, playerWidth); //* 4 / 3);

                Field lampField = grid.getField(rows - 10, columns - 18);
                Field lampField2 = grid.getField(12, 44);
                Field lampField3 = grid.getField(8, 6);

                streetLamp.addView(
                        "streetLamp",
                        scene.addImageView(new ImageViewImpl("Street Lamp View"))
                                .setAbsoluteX(lampField.getCenterX())
                                .setAbsoluteY(lampField.getCenterY())
                                .setImage(streetLampImage)
                                .setZindex(5)
                                .show()
                );

                streetLamp.addView(
                        "streetLamp2",
                        scene.addImageView(new ImageViewImpl("Street Lamp View 2"))
                                .setAbsoluteX(lampField2.getCenterX())
                                .setAbsoluteY(lampField2.getCenterY())
                                .setImage(streetLampImage)
                                .setZindex(5)
                                .show()
                );

                streetLamp.addView(
                        "streetLamp3",
                        scene.addImageView(new ImageViewImpl("Street Lamp View 3"))
                                .setAbsoluteX(lampField3.getCenterX())
                                .setAbsoluteY(lampField3.getCenterY())
                                .setImage(streetLampImage)
                                .setZindex(5)
                                .show()
                );

                streetLamp.addView(
                        "streetLampLight",
                        scene.addImageView(new ImageViewImpl("Street Lamp Light"))
                                .setAbsoluteX(lampField.getCenterX())
                                .setAbsoluteY(lampField.getCenterY() + playerHeight / 4)
                                .setImage(lampLightImage)
                                .setZindex(6)
                                .show()

                );

                streetLamp.addView(
                        "streetLampLight2",
                        scene.addImageView(new ImageViewImpl("Street Lamp Light 2"))
                                .setAbsoluteX(lampField2.getCenterX())
                                .setAbsoluteY(lampField2.getCenterY() + playerHeight / 4)
                                .setImage(lampLightImage)
                                .setZindex(6)
                                .show()

                );

                streetLamp.addView(
                        "streetLampLight3",
                        scene.addImageView(new ImageViewImpl("Street Lamp Light 3"))
                                .setAbsoluteX(lampField3.getCenterX())
                                .setAbsoluteY(lampField3.getCenterY() + playerHeight / 4)
                                .setImage(lampLightImage)
                                .setZindex(6)
                                .show()

                );

                DaemonUtils.IntervalRegulator lampBlinking = new DaemonUtils.IntervalRegulator() {

                    private List<Long> intervals = new LinkedList<>();
                    private Iterator<Long> intervalIterator;

                    {
                        intervals.add(1000L);
                        intervals.add(100L);
                        intervals.add(200L);
                        intervals.add(1000L);
                        intervals.add(300L);
                        intervals.add(150L);
                        intervals.add(3000L);
                        intervals.add(200L);
                        intervalIterator = intervals.iterator();
                    }

                    @Override
                    public long getSleepInterval() {
                        if (!intervalIterator.hasNext())
                            intervalIterator = intervals.iterator();
                        return intervalIterator.next();
                    }
                };

                streetLamp.setDaemon(DummyDaemon.create(renderer, lampBlinking));
                streetLamp.getDaemon().setClosure(() -> {

                    ImageView lightView = streetLamp.getView("streetLampLight");
                    ImageView lightView2 = streetLamp.getView("streetLampLight2");
                    ImageView lightView3 = streetLamp.getView("streetLampLight3");

                    if(lightView.isShowing()) {
                        lightView.hide();
                        lightView2.hide();
                        lightView3.hide();
                    } else {
                        lightView.show();
                        lightView2.show();
                        lightView3.show();
                    }

                });

                streetLamp.getDaemon().start();

                Field initial = grid.getField(99, 199);

                //init player
                player = new PlayerDaemon(
                        gameConsumer,
                        new Player(
                                playerSprite,
                                healthBarSprite,
                                searchlight,
                                Pair.create((float)(borderX / 2), (float) (borderY / 2)),
                                //Pair.create(initial.getCenterX(), initial.getCenterY()),
                                dXY,
                                cameraWidth / 2,
                                cameraHeight / 2,
                                1200,
                                1000
                        )
                ).setName("Player").setPlayerCoordinateExporter((x, y) -> {

                    Field current = grid.getField(x, y);
                    grid.setStartAndRecalculate(current.getRow(), current.getColumn());

                }).exportCoordinates(12000, new Runnable() {
                    @Override
                    public void run() {
                        player.exportCoordinates(30, this::run);
                    }
                });

                //player views init
                {
                    ImageView mainView = scene.addImageView(new FixedView("Player Main View", cameraWidth / 2, cameraHeight / 2, 10, playerSprite[0].getWidth(), playerSprite[0].getHeight()))
                            .setImage(playerSprite[0])
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2);

                    ImageView hpView = scene.addImageView(new FixedView("Player HP View", cameraWidth / 2, cameraHeight / 2 - playerSprite[0].getHeight() / 2, 10, healthBarSprite[0].getWidth(), healthBarSprite[0].getHeight()))
                            .setImage(healthBarSprite[0])
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2);

                    ImageView searchlightView = scene.addImageView(
                            new FixedView(
                                    "Player Searchlight View",
                                    cameraWidth / 2,
                                    cameraHeight / 2 + playerSprite[0].getHeight() / 2,
                                    9,
                                    searchlight.getWidth(),
                                    searchlight.getHeight()
                            ).setImage(searchlight)
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2)
                    );

                    ImageView mainViewFixedCam = scene.addImageView(
                            new ImageViewImpl(
                                    "Player Main View Fixed Cam",
                                    10,
                                    borderX / 2,
                                    borderY / 2,
                                    playerSprite[0].getWidth(),
                                    playerSprite[0].getHeight()
                            )
                    ).setImage(playerSprite[0]).hide();

                    ImageView hpViewFixedCam = scene.addImageView(
                            new ImageViewImpl(
                                    "Player HP View Fixed Cam",
                                    10,
                                    borderX / 2,
                                    borderY / 2 - playerSprite[0].getHeight() / 2,
                                    healthBarSprite[0].getWidth(),
                                    healthBarSprite[0].getHeight()
                            )
                    ).setImage(healthBarSprite[0]).hide();

                    ImageView searchlightViewFixedCam = scene.addImageView(
                            new ImageViewImpl("Player Searchlight View Fixed Cam",
                                    9,
                                    borderX / 2,
                                    borderY / 2 + playerSprite[0].getHeight() / 2,
                                    searchlight.getWidth(),
                                    searchlight.getHeight()
                            ).setImage(searchlight).hide()
                    );

                    playerViewMap.put("main", mainView);
                    playerViewMap.put("hp", hpView);
                    playerViewMap.put("searchlightView", searchlightView);

                    playerViewMap.put("mainFC", mainViewFixedCam);
                    playerViewMap.put("hpFC", hpViewFixedCam);
                    playerViewMap.put("searchlightFC", searchlightViewFixedCam);

                    renderer.consume(() -> {
                        mainView.show();
                        hpView.show();
                        searchlightView.show();
                    });

                    playerAnimateClosure = new PlayerCameraClosure(mainView, hpView, searchlightView);

                    player.setAnimatePlayerSideQuest(renderer).setClosure(playerAnimateClosure);
                }

                Field firstField = grid.getField(100, 200);

                followingCamera.setTarget(player);

                renderer.setCamera(followingCamera);

                controller.getPrototype().setControllable(player.start());

                com.daemonize.game.KeyBoardMovableController<PlayerDaemon> controllerPrototype = ((com.daemonize.game.KeyBoardMovableController<PlayerDaemon>) controller.getPrototype());

                controllerPrototype.setConsumer(gameConsumer);

                controllerPrototype.setDirMapper(new MovementController.DirectionToCoordinateMapper() {
                    @Override
                    public Pair<Float, Float> map(MovementController.Direction dir) {

                        int currentZElevation = player.getZElevation();

                        Field currentField = grid.getField(
                                player.getLastCoordinates().getFirst(),
                                player.getLastCoordinates().getSecond()
                        );

                        List<Field> neighbors = grid.getNeighbors(currentField);

                        Pair<Float, Float> ret = player.getLastCoordinates();



                            Field firstNeighbour;

                            switch (dir) {
                                case UP:

                                    firstNeighbour = neighbors.get(1);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {
                                        if (firstNeighbour.isWalkable())
                                            ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                                        else if (neighbors.get(0).isWalkable())
                                            ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                                        else if (neighbors.get(2).isWalkable())
                                            ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                                    }
                                    break;
                                case DOWN:

                                    firstNeighbour = neighbors.get(6);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {

                                        if (neighbors.get(6).isWalkable())
                                            ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
                                        else if (neighbors.get(5).isWalkable())
                                            ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                                        else if (neighbors.get(7).isWalkable())
                                            ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                                    }
                                    break;
                                case RIGHT:

                                    firstNeighbour = neighbors.get(4);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {


                                        if (neighbors.get(4).isWalkable())
                                            ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                                        else if (neighbors.get(2).isWalkable())
                                            ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                                        else if (neighbors.get(7).isWalkable())
                                            ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                                    }
                                    break;
                                case LEFT:

                                    firstNeighbour = neighbors.get(3);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {

                                        if (neighbors.get(3).isWalkable())
                                            ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                                        else if (neighbors.get(0).isWalkable())
                                            ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                                        else if (neighbors.get(5).isWalkable())
                                            ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                                    }
                                    break;
                                case UP_RIGHT:

                                    firstNeighbour = neighbors.get(2);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {

                                        if (neighbors.get(2).isWalkable())
                                            ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                                        else if (neighbors.get(1).isWalkable())
                                            ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                                        else if (neighbors.get(4).isWalkable())
                                            ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                                    }
                                    break;
                                case UP_LEFT:

                                    firstNeighbour = neighbors.get(0);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {

                                        if (neighbors.get(0).isWalkable())
                                            ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                                        else if (neighbors.get(3).isWalkable())
                                            ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                                        else if (neighbors.get(1).isWalkable())
                                            ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                                    }
                                    break;
                                case DOWN_RIGHT:

                                    firstNeighbour = neighbors.get(7);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {

                                        if (neighbors.get(7).isWalkable())
                                            ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                                        else if (neighbors.get(4).isWalkable())
                                            ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                                        else if (neighbors.get(6).isWalkable())
                                            ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
                                    }
                                    break;
                                case DOWN_LEFT:

                                    firstNeighbour = neighbors.get(5);

                                    if (firstNeighbour.getRow() < rows && firstNeighbour.getColumn() >= 0
                                            && firstNeighbour.getColumn() < columns && firstNeighbour.getColumn() >= 0
                                    ) {


                                        if (neighbors.get(5).isWalkable())
                                            ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                                        else if (neighbors.get(3).isWalkable())
                                            ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                                        else if (neighbors.get(6).isWalkable())
                                            ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
                                    }
                                    break;
                                default:
                                    throw new IllegalStateException("No dir: " + dir);

                            }


                        return ret;
                    }
                });

                controllerPrototype.setMovingAction((player, parameterPack) -> {

                    if (player.getLastCoordinates().equals(parameterPack.nextCoords)) {
                        parameterPack.rotationClosure.run();
                        parameterPack.translationClosure.onReturn(new Return<>(true));
                    } else {
                        if (rotationBlocker.getEnginesState().get(0).equals(DaemonState.IDLE)) {
                            player.rotateTowards(parameterPack.nextCoords, parameterPack.rotationClosure)
                                    .goTo(parameterPack.nextCoords, parameterPack.velocity, parameterPack.translationClosure);
                            player.setAttackable(true);

                        } else {

                            player.goTo(parameterPack.nextCoords, parameterPack.velocity, parameterPack.translationClosure);
                            parameterPack.rotationClosure.run();
                        }
                    }

                });

                controllerPrototype.setMovementCallback(player -> {

                    Field<FieldContent> field = grid.getField(
                            player.getLastCoordinates().getFirst(),
                            player.getLastCoordinates().getSecond()
                    );

                    //System.out.println(DaemonUtils.timedTag() + player.getName() + " at " + field);

                    Interactible item = field.getObject().getInteractible();

                    if (item != null) {
                        boolean result = item.interact();
                        if (item instanceof HealthPack && result)
                            renderer.consume(((HealthPack) item).getView()::hide);
                        field.getObject().setInteractible(null);
                    }
                });

                controller.setControlSideQuest();
                controller.start();



                AngleToSpriteArray zombieMoveAnimation = new AngleToSpriteArray(8).mapAllAngles(angle -> {
                    switch (angle) {
                        case 0:
                            return zombieMove0;
                        case 45:
                            return zombieMove45;
                        case 90:
                            return zombieMove90;
                        case 135:
                            return zombieMove135;
                        case 180:
                            return zombieMove180;
                        case 225:
                            return zombieMove225;
                        case 270:
                            return zombieMove270;
                        case 315:
                            return zombieMove315;
                        default:
                            throw new IllegalArgumentException("Angle: " +angle);
                    }
                });

                AngleToSpriteArray zombieAttackAnimation = new AngleToSpriteArray(8).mapAllAngles(angle -> {

                    switch (angle) {
                        case 0:
                            return sheetCutter.cut(156, 166, zombieSprite);
                        case 45:
                            return sheetCutter.cut(120, 130, zombieSprite);
                        case 90:
                            return sheetCutter.cut(84, 94, zombieSprite);
                        case 135:
                            return sheetCutter.cut(48, 58, zombieSprite);
                        case 180:
                            return sheetCutter.cut(12, 22, zombieSprite);
                        case 225:
                            return sheetCutter.cut(264, 274, zombieSprite);
                        case 270:
                            return sheetCutter.cut(228, 238, zombieSprite);
                        case 315:
                            return sheetCutter.cut(192, 202, zombieSprite);
                        default:
                            throw new IllegalArgumentException("Angle: " +angle);
                    }
                });

                zombieFallAnimation = new AngleToSpriteArray(8).mapAllAngles(angle -> {

                    switch (angle) {
                        case 0:
                            return sheetCutter.cut(166, 172, zombieSprite);
                        case 45:
                            return sheetCutter.cut(130, 136, zombieSprite);
                        case 90:
                            return sheetCutter.cut(94, 100, zombieSprite);
                        case 135:
                            return sheetCutter.cut(58, 64, zombieSprite);
                        case 180:
                            return sheetCutter.cut(22, 28, zombieSprite);
                        case 225:
                            return sheetCutter.cut(274, 280, zombieSprite);
                        case 270:
                            return sheetCutter.cut(238, 244, zombieSprite);
                        case 315:
                            return sheetCutter.cut(202, 208, zombieSprite);
                        default:
                            throw new IllegalArgumentException("Angle: " +angle);
                    }
                });

                AngleToSpriteArray zombieRiseAnimation = new AngleToSpriteArray(8).mapAllAngles(angle -> {

                    Image[] angleSprite = zombieFallAnimation.getSpriteByAngle(angle);
                    Image[] ret = new Image[angleSprite.length];

                    for(int i = 0; i < angleSprite.length; ++i)
                        ret[i] = angleSprite[angleSprite.length - (i + 1)];

                    return ret;
                });

                List<Field<FieldContent>> walkableFields = new ArrayList<>();

                for(int i = 0; i < rows; ++i)
                    for(int j = 0; j < columns; ++j)
                        if(grid.getField(i, j).isWalkable())
                            walkableFields.add(grid.getField(i, j));

                System.err.println(DaemonUtils.timedTag() + "Walkable fields: " + walkableFields.size());

                //picakbles
                healthPackImage = imageManager.loadImageFromAssets("healthPack.png", playerWidth / 2, playerWidth /2 );

                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));

                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));
                healthPackFields.add(walkableFields.get(getRandomInt(0, walkableFields.size())));

                for (int i = 0; i < rows; ++i) {
                    for(int j = 0; j < columns; ++j) {
                        grid.getField(i, j).setObject(new FieldContent());
                    }
                }

                for(Field<FieldContent> current : healthPackFields) {
                    current.getObject().setInteractible(HealthPack.generateHealthPack(
                            player,
                            200,
                            ((int) current.getCenterX()),
                            ((int) current.getCenterY()),
                            healthPackImage, scene
                    ));
                }

                int noOfZombies = walkableFields.size() / 50;

                zombieViews = new ImageView[noOfZombies];

                for(int i = 0; i < noOfZombies; ++i) {

                    int walkableFieldNo = getRandomInt(0, walkableFields.size());

                    Field zombieSpawningField = walkableFields.get(walkableFieldNo);

                    walkableFields.remove(walkableFieldNo);

                    int currentZombieX = ((int) zombieSpawningField.getCenterX());
                    int currentZombieY = ((int) zombieSpawningField.getCenterY());

                    zombieViews[i] = scene.addImageView(new ImageViewImpl("Zombie View No. " + i))
                            .setAbsoluteX(currentZombieX)
                            .setAbsoluteY(currentZombieY)
                            .setZindex(6)
                            .setImage(zombieMove270[0])
                            .show();

                    float zombieVelocity = 8.0F;

                    float zombieInstanceVelocity = zombieVelocity + getRandomInt(-3 , 3);

                    ZombieDaemon zombie = new ZombieDaemon(
                            gameConsumer,
                            new Zombie(
                                    zombieRiseAnimation.getByAngle(getRandomInt(0, 359)),
                                    zombieMoveAnimation.clone(),
                                    zombieAttackAnimation.clone(),
                                    zombieInstanceVelocity,
                                    Pair.create(zombieViews[i].getAbsoluteX(), zombieViews[i].getAbsoluteY()),
                                    dXY
                            )
                    ).setName( i % 50 ==0 ? "DebugZombie no: " + i : "Zombie");

                    zombie.setMaxHp(80).setHp(80);

                    zombie.setAnimateZombieSideQuest(renderer)
                            .setSleepInterval(zombieInstanceVelocity > 10 ? 50 : zombieInstanceVelocity > 8 ? 70 : zombieInstanceVelocity > 5 ? 80 : zombieInstanceVelocity > 3 ? 90 : zombieInstanceVelocity > 0 ? 100 : 0)
                            .setClosure(new ZombieSpriteAnimateClosure(zombieViews[i]));

                    Field<FieldContent> zeroField = grid.getField(zombie.getLastCoordinates());

                    FieldContent zeroFieldContent  = zeroField.getObject();

                    zeroFieldContent.subscribe(zombie);
                    zombie.setCurrentField(zeroField);

                    final int zombieRiseProximity = 70;
                    final int zombieFallDistance = 80;

                    zombie.start().rotateTowards(getRandomInt(0, borderX), getRandomInt(0, borderY)).animateDirectionalSprite(zombieFallAnimation, new Runnable() {
                        @Override
                        public void run() {

                            Runnable zombieFallClosure = this;

                            zombie.sleepAndRet(500, () -> {

                                Field current = grid.getField(zombie.getLastCoordinates());

                                if (current.gCost > zombieRiseProximity) {
                                    zombie.sleepAndRet(500, zombieFallClosure);
                                } else {

                                    zombie.animateDirectionalSprite(zombieRiseAnimation, () -> {

                                        Field<FieldContent> curr = grid.getField(zombie.getLastCoordinates());
                                        Field<FieldContent> next = grid.getMinWeightOfNeighbors(curr);

                                        zombie.rotateTowards(next.getCenterCoords())
                                                .goTo(next.getCenterCoords(), zombie.getPrototype().recommendedVelocity, new Closure<Boolean>(){
                                                    @Override
                                                    public void onReturn(Return<Boolean> goToReturn) {

                                                        goToReturn.runtimeCheckAndGet();

                                                        Field<FieldContent> curr2 = grid.getField(zombie.getLastCoordinates());

                                                        { //field subscription
                                                            zombie.getCurrentField().getObject().unsubscribe(zombie);
                                                            zombie.setCurrentField(curr2);
                                                            curr2.getObject().subscribe(zombie);
                                                        }

                                                        if (curr2.gCost > zombieFallDistance) {
                                                            zombie.animateDirectionalSprite(zombieFallAnimation, zombieFallClosure);
                                                            return;
                                                        }

                                                        Field next2 = grid.getMinWeightOfNeighbors(curr2);

                                                        if (ImageTranslationMover.absDistance(player.getLastCoordinates(), zombie.getLastCoordinates()) <= fieldWidth) {

                                                            if (player.isAttackable()) {

                                                                player.setAttackable(false);

                                                                if (player.getHp() - 100 < 1)
                                                                    throw new IllegalStateException("You ded!");
                                                                else
                                                                    player.setHp(player.getHp() - 100);

                                                                player.sleep(400).pushSprite(explosionSprite, () -> {});

                                                                zombie.attack(() -> {
                                                                    player.setAttackable(true);
                                                                    zombie.rotateTowards(next2.getCenterCoords())
                                                                            .goTo(next2.getCenterCoords(), zombie.getPrototype().recommendedVelocity, this::onReturn);
                                                                });

                                                            } else {
                                                                zombie.sleep(1000)
                                                                        .rotateTowards(next2.getCenterCoords())
                                                                        .goTo(next2.getCenterCoords(), zombie.getPrototype().recommendedVelocity, this::onReturn);
                                                            }

                                                        } else {
                                                            zombie.rotateTowards(next2.getCenterCoords())
                                                                    .goTo(next2.getCenterCoords(), zombie.getPrototype().recommendedVelocity, this::onReturn);
                                                        }
                                                    }
                                                });
                                    });
                                }
                            });
                        }
                    });
                }

                bulletImage = imageManager.loadImageFromAssets("thebarnstarRed.png", cameraWidth / 70, cameraWidth / 70);

                for (int i = 0; i < 30; ++i) {
                    bulletViews.add(
                            scene.addImageView(new ImageViewImpl("Bullet View no. " + i))
                                    .setAbsoluteX(Float.NaN)
                                    .setAbsoluteY(Float.NaN)
                                    .setZindex(4)
                                    .setImage(bulletImage)
                                    .hide()
                    );
                }

                renderer.setScene(scene.lockViews()).start();

                aimController.setOnHoover((float x, float y) -> {

                    renderer.consume(() -> {//TODO encapsulate
                       crosshairView.setAbsoluteX(x).setAbsoluteY(y);
                    });
                });

                aimController.setOnClick((float x, float y, MouseController.MouseButton mouseButton) -> {

                    if (rotationBlocker.getEnginesQueueSizes().get(0) < 2)
                        rotationBlocker.block();
                    player.rotateTowards(x, y);
                    fireBullet(player.getLastCoordinates(), Pair.create(x, y), 30);
                });

                rotationBlocker = new PlayerRotationBlockerDaemon(gameConsumer, new PlayerRotationBlocker()).start();

                player.rotateTowards(firstField.getCenterX(), firstField.getCenterY())
                        .goTo(firstField.getCenterCoords(), 12F, ret -> renderer.consume(crosshairView::show));


            } catch (IOException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
    }

    private volatile int bulletCnt = 0;

    private void fireBullet(Pair<Float, Float> sourceCoords, Pair<Float, Float> dstCoords, float velocity) {

        ProjectileDaemon bullet = new ProjectileDaemon(
                gameConsumer,
                new SimpleBullet(miniExplosionSprite, bulletImage, sourceCoords, dXY)
        ).setName("Simple Bullet " + ++bulletCnt);

        ((SimpleBullet) bullet.getPrototype()).setTargetFinder(currentCoords -> {

            Field<FieldContent> currentField = grid.getField(currentCoords);

            if (currentField != null) {

                Set<Target> targets = currentField.getObject().getTargets();

                for (Target currentTarget : targets) {
                    if (ImageTranslationMover.absDistance(currentCoords, currentTarget.getLastCoordinates()) < 50) {

                        final ZombieDaemon zombieTarget = ((ZombieDaemon) currentTarget);

                        int targetHp = currentTarget.getHp();
                        int newTargetHp = targetHp - bulletDamage;

                        if (newTargetHp < 1) {
                            zombieTarget.clearAndInterrupt().animateDirectionalSprite(zombieFallAnimation, () -> {
                                zombieTarget.stop();
                                System.err.println(DaemonUtils.timedTag() + bullet.getName() + " -  Target: " + zombieTarget.getName() + " DESTROYED!!!!!!!!!");
                            });
                        } else {
                            zombieTarget.setHp(newTargetHp);
                            System.out.println(DaemonUtils.timedTag() +  bullet.getName() + " - Target: " + zombieTarget.getName() + ", HIT Hp: " + zombieTarget.getHp() + "!!!!!!!!!");
                        }

                        return currentTarget;
                    }
                }
            }

            return null;
        })
                .setCoordinateValidator(currentCoords -> {

            Field<FieldContent> currentField = grid.getField(currentCoords);

            if (currentField == null ||  !currentField.isWalkable()
                    || (currentCoords.getFirst() < 0 || currentCoords.getFirst() > borderX || currentCoords.getSecond() < 0 || currentCoords.getSecond() > borderY))
                return false;

            return true;
        });

        ImageView bulletView = bulletViews.poll();

        renderer.consume(() -> {
            bulletView.setAbsoluteX(sourceCoords.getFirst())
                    .setAbsoluteY(sourceCoords.getSecond())
                    .show();
        });

        bullet.setAnimateProjectileSideQuest(renderer).setClosure(ret -> {
            // create view
            ImageMover.PositionedImage[] posImg = ret.runtimeCheckAndGet();
            bulletView.setAbsoluteX(posImg[0].positionX)
                    .setAbsoluteY(posImg[0].positionY);
        });

        bullet.updateTarget(new Runnable() {
            @Override
            public void run() {
                bullet.updateTarget(this::run);
            }
        });

        bullet.start().shoot(
                dstCoords.getFirst(),
                dstCoords.getSecond(),
                velocity,
                ret -> {

                    //System.err.println(DaemonUtils.timedTag() + "Bullet STOPPING: " + bullet.getLastCoordinates());
                    renderer.consume(() -> bulletView.hide().setAbsoluteX(Float.NaN).setAbsoluteY(Float.NaN));

                    bullet.stop();
                    bulletViews.add(bulletView);
                });

        Field startingField = grid.getField(sourceCoords);

        //System.out.println(DaemonUtils.timedTag() + "Bullet STARTED at: " + sourceCoords + ", Field[" + startingField.getRow() + "][" + startingField.getColumn() + "]");
    }
}

