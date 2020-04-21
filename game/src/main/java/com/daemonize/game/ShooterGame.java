package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.controller.MovementController;
import com.daemonize.game.controller.MovementControllerDaemon;
import com.daemonize.game.app.DaemonApp;
import com.daemonize.game.grid.Field;
import com.daemonize.game.grid.Grid;
import com.daemonize.game.interactables.Interactable;
import com.daemonize.game.interactables.health.HealthPack;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.FixedView;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;
import com.daemonize.imagemovers.AngleToSpriteArray;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.ImageTranslationMover;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import daemon.com.commandparser.CommandParser;
import daemon.com.commandparser.CommandParserDaemon;

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

    //grid
    private Grid<Interactable> grid;
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

    private Image[] explosionSprite;

    //controllable zombie
    //private ZombieDaemon zombie;

    private Image[] playerSprite;
    private Image[] healthBarSprite;
    private Image searchlight;

    private Map<String, ImageView> playerViewMap = new TreeMap<>();
    private PlayerCameraClosure playerAnimateClosure;

    private Image healthPackImage;
    private ImageView healthPackView;

    private List<Field<Interactable>> healthPackFields;

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

    private ImageView[] zombieViews;
    //private List<SpriteAnimatorDaemon<ConstantSpriteAnimator>> zombieAnimators;

    //controller
    private MovementControllerDaemon controller;

    public MovementControllerDaemon getMovementController() {
        return controller;
    }

    //test
    //private UnholyTrinity<SpriteAnimatorDaemon<ConstantSpriteAnimator>> testTrinity = new UnholyTrinity<>();
    private UnholyTrinity<DummyDaemon> streetLamp = new UnholyTrinity<>();

    //construct
    public ShooterGame(
            Renderer2D renderer,
            ImageManager imageManager,
            MovementController controller,
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

//
//        this.grid = new Grid<Interactable<PlayerDaemon>>(
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
//
//        String path = getClass().getResource("/" + "test.json").getPath();
//        File jsonGrid = new File(path);

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

                //zombie sprite
                zombieSprite = imageManager.loadSheet("zombieSheet.png", 8, 36, cameraWidth / 4, cameraHeight / 4);

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

                Image streetLampImage = imageManager.loadImageFromAssets("streetLamp.png", playerWidth, playerWidth );
                Image lampLightImage = imageManager.loadImageFromAssets("searchlight.png", playerWidth * 2, playerWidth); //* 4 / 3);

                Field lampField = grid.getField(rows - 10, columns - 18);
                Field lampField2 = grid.getField(6, 45);
                Field lampField3 = grid.getField(35, 61);

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
                                800,
                                800
                        )
                ).setName("Player").setPlayerCoordinateExporter((x, y) -> {

                    Field current = grid.getField(x, y);
                    grid.setStartAndRecalculate(current.getRow(), current.getColumn());

                }).exportCoordinates(15000, new Runnable() {
                    @Override
                    public void run() {
                        player.exportCoordinates(500, this::run);
                    }
                });

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

                //picakbles
                healthPackImage = imageManager.loadImageFromAssets("healthPack.png", playerWidth / 2, playerWidth /2 );

                healthPackFields.add(grid.getField(21, 25));
                healthPackFields.add(grid.getField(11, 15));
                healthPackFields.add(grid.getField(11, 35));
                healthPackFields.add(grid.getField(2, 15));
                healthPackFields.add(grid.getField(getRandomInt(0, rows - 1), getRandomInt(0, columns- 1)));
                healthPackFields.add(grid.getField(getRandomInt(0, rows - 1), getRandomInt(0, columns- 1)));

                for(Field<Interactable> current : healthPackFields) {
                    current.setObject(
                            HealthPack.generateHealthPack(
                                    player,
                                    20,
                                    ((int) current.getCenterX()),
                                    ((int) current.getCenterY()),
                                    healthPackImage, scene
                            )
                    );
                }

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

                        switch (dir) {
                            case UP:
                                if(neighbors.get(1).isWalkable())
                                    ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                                else if (neighbors.get(0).isWalkable())
                                    ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                                else if (neighbors.get(2).isWalkable())
                                    ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                                break;
                            case DOWN:
                                if(neighbors.get(6).isWalkable())
                                    ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
                                else if (neighbors.get(5).isWalkable())
                                    ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                                else if (neighbors.get(7).isWalkable())
                                    ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                                break;
                            case RIGHT:
                                if(neighbors.get(4).isWalkable())
                                    ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                                else if (neighbors.get(2).isWalkable())
                                    ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                                else if (neighbors.get(7).isWalkable())
                                    ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                                break;
                            case LEFT:
                                if(neighbors.get(3).isWalkable())
                                    ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                                else if (neighbors.get(0).isWalkable())
                                    ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                                else if (neighbors.get(5).isWalkable())
                                    ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                                break;
                            case UP_RIGHT:
                                if(neighbors.get(2).isWalkable())
                                    ret = Pair.create(neighbors.get(2).getCenterX(), neighbors.get(2).getCenterY());
                                else if (neighbors.get(1).isWalkable())
                                    ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                                else if (neighbors.get(4).isWalkable())
                                    ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                                break;
                            case UP_LEFT:
                                if(neighbors.get(0).isWalkable())
                                    ret = Pair.create(neighbors.get(0).getCenterX(), neighbors.get(0).getCenterY());
                                else if (neighbors.get(3).isWalkable())
                                    ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                                else if (neighbors.get(1).isWalkable())
                                    ret = Pair.create(neighbors.get(1).getCenterX(), neighbors.get(1).getCenterY());
                                break;
                            case DOWN_RIGHT:
                                if(neighbors.get(7).isWalkable())
                                    ret = Pair.create(neighbors.get(7).getCenterX(), neighbors.get(7).getCenterY());
                                else if (neighbors.get(4).isWalkable())
                                    ret = Pair.create(neighbors.get(4).getCenterX(), neighbors.get(4).getCenterY());
                                else if (neighbors.get(6).isWalkable())
                                    ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
                                break;
                            case DOWN_LEFT:
                                if(neighbors.get(5).isWalkable())
                                    ret = Pair.create(neighbors.get(5).getCenterX(), neighbors.get(5).getCenterY());
                                else if (neighbors.get(3).isWalkable())
                                    ret = Pair.create(neighbors.get(3).getCenterX(), neighbors.get(3).getCenterY());
                                else if (neighbors.get(6).isWalkable())
                                    ret = Pair.create(neighbors.get(6).getCenterX(), neighbors.get(6).getCenterY());
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
                        parameterPack.translationClosure.run();
                    } else {
                        player.rotateTowards(parameterPack.nextCoords, parameterPack.rotationClosure)
                                .goTo(parameterPack.nextCoords, parameterPack.velocity, parameterPack.translationClosure);
                    }

                });

                controllerPrototype.setMovementCallback(player -> {

                    Field<Interactable> field = grid.getField(
                            player.getLastCoordinates().getFirst(),
                            player.getLastCoordinates().getSecond()
                    );

                    Interactable item = field.getObject();

                    if (item != null) {
                        item.interact();
                        renderer.consume(item.getView()::hide);
                        field.setObject(null);
                    }
                });

                controller.setControlSideQuest();
                controller.start();

                player.rotateTowards(firstField.getCenterX(), firstField.getCenterY())
                        .go(firstField.getCenterX(), firstField.getCenterY(), 12F);

//
//                Field currentField = grid.getField(player.getLastCoordinates().getFirst(), player.getLastCoordinates().getSecond());
//
//                grid.setCoordsAndRecalculate(currentField.getRow(), currentField.getColumn());

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

                int noOfZombies = 16;

                zombieViews = new ImageView[noOfZombies];

                for(int i = 0; i < noOfZombies; ++i) {

                    //int currentZombieX = getRandomInt(0, borderX);
                    //int currentZombieY = getRandomInt(0, borderY);

                    int currentZombieX = ((int) firstField.getCenterX() + getRandomInt(-40, 10));
                    int currentZombieY = ((int) firstField.getCenterY() + getRandomInt(-40, 10));

                    zombieViews[i] = scene.addImageView(new ImageViewImpl("Zombie View No. " + i))
                            .setAbsoluteX(currentZombieX)
                            .setAbsoluteY(currentZombieY)
                            .setZindex(6)
                            .setImage(zombieMove270[0])
                            .show();

                    float zombieVelocity = 6.4F;

                    if (i % 2 == 0)
                        zombieVelocity = 9.4F;


                    float zombieInstanceVelocity = zombieVelocity + getRandomInt(-3 , 3);

                    ZombieDaemon zombie = new ZombieDaemon(
                            gameConsumer,
                            new Zombie(
                                    zombieMove270[0],
                                    zombieMoveAnimation.clone(),
                                    zombieAttackAnimation.clone(),
                                    zombieInstanceVelocity,
                                    Pair.create(zombieViews[i].getAbsoluteX(), zombieViews[i].getAbsoluteY()),
                                    dXY
                            )
                    ).setName( i % 50 ==0 ? "DebugZombie no: " + i : "Zombie");

                    zombie.setAnimateZombieSideQuest(renderer)
                            .setSleepInterval(zombieInstanceVelocity > 10 ? 50 : zombieInstanceVelocity > 8 ? 70 : zombieInstanceVelocity > 5 ? 80 : zombieInstanceVelocity > 3 ? 90 : zombieInstanceVelocity > 0 ? 100 : 0)
                            .setClosure(new ZombieSpriteAnimateClosure(zombieViews[i]));

                    Field zeroField = grid.getField(zombie.getLastCoordinates().getFirst(), zombie.getLastCoordinates().getSecond());

                    Field firstF = grid.getMinWeightOfNeighbors(zeroField);

                    zombie.start().rotateTowards(firstF.getCenterX(), firstF.getCenterY()).goTo(firstF.getCenterX(), firstF.getCenterY(), zombie.getPrototype().recommendedVelocity, new Closure<Boolean>() {

                        @Override
                        public void onReturn(Return<Boolean> ret) {

                            ret.runtimeCheckAndGet();

                            Field curr = grid.getField(
                                    zombie.getLastCoordinates().getFirst(),
                                    zombie.getLastCoordinates().getSecond()
                            );

                            Field next = grid.getMinWeightOfNeighbors(curr);

                            if (zombie.getName().contains("DebugZombie"))
                                System.err.println(DaemonUtils.timedTag() + zombie.getName() + " at " + curr);

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
                                        zombie.rotateTowards(next.getCenterX(), next.getCenterY())
                                                .goTo(next.getCenterX(), next.getCenterY(), zombie.getPrototype().recommendedVelocity, this::onReturn);
                                    });

                                } else

                                    //zombie.attack(() ->
                                            zombie.sleep(1000).rotateTowards(next.getCenterX(), next.getCenterY())
                                                    .goTo(next.getCenterX(), next.getCenterY(), zombie.getPrototype().recommendedVelocity, this::onReturn);
                                    //);

                            } else
                                zombie.rotateTowards(next.getCenterX(), next.getCenterY()).goTo(next.getCenterX(), next.getCenterY(), zombie.getPrototype().recommendedVelocity, this::onReturn);
                        }
                    });
                }

                renderer.setScene(scene.lockViews()).start();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
    }
}
