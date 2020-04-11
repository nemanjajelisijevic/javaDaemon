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
import com.daemonize.game.game.DaemonApp;
import com.daemonize.game.grid.Field;
import com.daemonize.game.grid.Grid;
import com.daemonize.game.interactables.Interactable;
import com.daemonize.game.interactables.health.HealthPack;
import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.camera.FixedCamera;
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

import java.io.IOException;
import java.util.ArrayList;
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
    private Grid<Interactable<PlayerDaemon>> grid;
    private int rows;
    private int columns;
    //private ImageView[][] gridViewMatrix;

    private int fieldWidth;

    private Image accessibleField;
    private Image inaccessibleField;

    //following camera
    private FollowingCamera followingCamera;
    //fixed camera
    private FixedCamera fixedCamera;

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

    private List<Field<Interactable<PlayerDaemon>>> healthPackFields;

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

        this.borderX = width * cameraToMapRatio;
        this.borderY = height * cameraToMapRatio;

        this.followingCamera = new FollowingCamera(width, height);
        this.fixedCamera = new FixedCamera(borderX / 2, borderY / 2 , width, height);

        this.scene = new Scene2D();
        this.dXY = ((float) cameraWidth) / 1000;

        this.controller = new MovementControllerDaemon(gameConsumer, controller).setName("Player controller");

        this.fieldWidth = 50;
        this.rows = borderY / fieldWidth;
        this.columns = borderX / fieldWidth;

        this.grid = new Grid<Interactable<PlayerDaemon>>(
                rows,
                columns,
                Pair.create(0, 0),
                Pair.create(rows - 1, columns - 1),
                0,
                0,
                borderX / columns
        );

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
                        "emptyMap.png", //"map_1.png",
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

                Field lampField = grid.getField(25, 34);
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

                //picakbles
                healthPackImage = imageManager.loadImageFromAssets("healthPack.png", playerWidth / 2, playerWidth /2 );

                healthPackFields.add(grid.getField(21, 25));
                healthPackFields.add(grid.getField(11, 15));
                healthPackFields.add(grid.getField(11, 35));
                healthPackFields.add(grid.getField(2, 15));
                healthPackFields.add(grid.getField(getRandomInt(0, rows - 1), getRandomInt(0, columns- 1)));
                healthPackFields.add(grid.getField(getRandomInt(0, rows - 1), getRandomInt(0, columns- 1)));

                for(Field<Interactable<PlayerDaemon>> current : healthPackFields) {
                    current.setObject(
                            HealthPack.generateHealthPack(
                                    20,
                                    ((int) current.getCenterX()),
                                    ((int) current.getCenterY()),
                                    healthPackImage, scene
                            )
                    );
                }

                //init player
                player = new PlayerDaemon(
                        gameConsumer,
                        new Player(
                                playerSprite,
                                healthBarSprite,
                                searchlight,
                                Pair.create((float)(borderX / 2), (float) (borderY / 2)),
                                dXY,
                                cameraWidth / 2,
                                cameraHeight / 2,
                                500,
                                500
                        )
                ).setName("Player");

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


                int noOfZombies = 50;

                zombieViews = new ImageView[noOfZombies];

                for(int i = 0; i < noOfZombies; ++i) {

                    int currentZombieX = getRandomInt(0, borderX);
                    int currentZombieY = getRandomInt(0, borderY);

                    zombieViews[i] = scene.addImageView(new ImageViewImpl("Zombie View No. " + i))
                            .setAbsoluteX(currentZombieX)
                            .setAbsoluteY(currentZombieY)
                            .setZindex(6)
                            .setImage(zombieMove270[0])
                            .show();
                }

                renderer.setScene(scene.lockViews()).start();

                followingCamera.setTarget(player);

                renderer.setCamera(followingCamera);

                controller.getPrototype().setControllable(player.start());

                KeyBoardMovementControllerImpl controllerPrototype = ((KeyBoardMovementControllerImpl) controller.getPrototype());

                controllerPrototype.setConsumer(gameConsumer);

                controllerPrototype.setDirMapper(new MovementController.DirectionToCoordinateMapper() {
                    @Override
                    public Pair<Float, Float> map(MovementController.Direction dir) {

                        Field currentField = grid.getField(
                                player.getLastCoordinates().getFirst(),
                                player.getLastCoordinates().getSecond()
                        );

                        List<Field> neighbors = grid.getNeighbors(currentField);

                        Pair<Float, Float> ret = null;

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
                    }
                });

                controllerPrototype.setMovementCallback(player -> {

                    Field<Interactable<PlayerDaemon>> field = grid.getField(player.getLastCoordinates().getFirst(), player.getLastCoordinates().getSecond());

//                    System.out.println(DaemonUtils.tag() + "Actual player coordinates: " + player.getLastCoordinates().toString());
//                    System.out.println(DaemonUtils.tag() + "Player at field: " + field.toString());

                    Interactable<PlayerDaemon> item = field.getObject();

                    if (item != null) {
                        item.interact(player);
                        renderer.consume(item.getView()::hide);
                        field.setObject(null);
                    }
                });

                controller.setControlSideQuest();
                controller.start();

                Field firstField = grid.getField(rows / 2, columns / 2);

                player.rotateTowards(firstField.getCenterX(), firstField.getCenterY())
                        .go(firstField.getCenterX(), firstField.getCenterY(), 2F);
//
//                dummyPlayer = new DummyPlayerDaemon(gameConsumer, new DummyPlayer(playerSprite[0], player.getLastCoordinates(), dXY));
//                dummyPlayer.setAnimateDummyPlayerSideQuest(renderer).setClosure(ret -> {});
//                dummyPlayer.start();

                //camera switcher init
//                cameraSwitcher = DummyDaemon.create(gameConsumer, 5000L).setClosure(new Runnable() {
//
//                    private Camera2D currentCamera = followingCamera;
//
//                    private void setCamera(Camera2D camera) {
//                        this.currentCamera = camera;
//                        renderer.setCamera(camera);
//                    }
//
//                    @Override
//                    public void run() {
//
//                        if (currentCamera.equals(followingCamera)) {
//
//                            playerAnimateClosure.setMainView(playerViewMap.get("mainFC"))
//                                    .setHpView(playerViewMap.get("hpFC"))
//                                    .setSearchlightView(playerViewMap.get("searchlightFC"));
//
//                            fixedCamera.setX(followingCamera.getRenderingX())
//                                    .setY(followingCamera.getRenderingY());
//
//                            playerViewMap.get("mainFC")
//                                    .setAbsoluteX(playerViewMap.get("main").getAbsoluteX())
//                                    .setAbsoluteY(playerViewMap.get("main").getAbsoluteY())
//                                    .setImage(playerViewMap.get("main").getImage());
//
//                            playerViewMap.get("hpFC").setAbsoluteX(playerViewMap.get("hp").getAbsoluteX())
//                                    .setAbsoluteY(playerViewMap.get("hp").getAbsoluteY())
//                                    .setImage(playerViewMap.get("hp").getImage());
//
//                            playerViewMap.get("searchlightFC").setAbsoluteX(playerViewMap.get("searchlightView").getAbsoluteX())
//                                    .setAbsoluteY(playerViewMap.get("searchlightView").getAbsoluteY())
//                                    .setImage(playerViewMap.get("searchlightView").getImage());
//
//                            renderer.consume(() -> {
//                                playerViewMap.get("main").hide();
//                                playerViewMap.get("hp").hide();
//                                playerViewMap.get("searchlightView").hide();
//
//                                playerViewMap.get("mainFC").show();
//                                playerViewMap.get("hpFC").show();
//                                playerViewMap.get("searchlightFC").show();
//                            });
//
//                            setCamera(fixedCamera);
//
//                        } else {
//
//                            dummyPlayer.setCoordinates(fixedCamera.getCenterX(), fixedCamera.getCenterY());
//                            setCamera(followingCamera.setTarget(dummyPlayer));
//
//                            dummyPlayer.goTo(
//                                    player.getLastCoordinates(),
//                                    35,
//                                    new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            if (Math.abs(dummyPlayer.getLastCoordinates().getFirst() - player.getLastCoordinates().getFirst()) > 30
//                                                    && Math.abs(dummyPlayer.getLastCoordinates().getSecond() - player.getLastCoordinates().getSecond()) > 25) {
//                                                dummyPlayer.goTo(player.getLastCoordinates(), 20, this::run);
//                                                return;
//                                            }
//
//                                            playerViewMap.get("main")
//                                                    .setAbsoluteX(playerViewMap.get("mainFC").getAbsoluteX())
//                                                    .setAbsoluteY(playerViewMap.get("mainFC").getAbsoluteY())
//                                                    .setImage(playerViewMap.get("mainFC").getImage());
//
//                                            playerViewMap.get("hp")
//                                                    .setAbsoluteX(playerViewMap.get("hpFC").getAbsoluteX())
//                                                    .setAbsoluteY(playerViewMap.get("hpFC").getAbsoluteY())
//                                                    .setImage(playerViewMap.get("hpFC").getImage());
//
//                                            playerViewMap.get("searchlightView")
//                                                    .setAbsoluteX(playerViewMap.get("searchlightFC").getAbsoluteX())
//                                                    .setAbsoluteY(playerViewMap.get("searchlightFC").getAbsoluteY())
//                                                    .setImage(playerViewMap.get("searchlightFC").getImage());
//
//                                            playerAnimateClosure.setMainView(playerViewMap.get("main"))
//                                                        .setHpView(playerViewMap.get("hp"))
//                                                        .setSearchlightView(playerViewMap.get("searchlightView"));
//
//                                            followingCamera.setTarget(player);
//
//                                            renderer.consume(() -> {
//
//                                                playerViewMap.get("mainFC").hide();
//                                                playerViewMap.get("hpFC").hide();
//                                                playerViewMap.get("searchlightFC").hide();
//
//                                                playerViewMap.get("main").show();
//                                                playerViewMap.get("hp").show();
//                                                playerViewMap.get("searchlightView").show();
//
//                                            });
//                                        }
//                                    });
//                        }
//                    }
//                });


                Field currentField = grid.getField(player.getLastCoordinates().getFirst(), player.getLastCoordinates().getSecond());

                grid.setCoordsAndRecalculate(currentField.getRow(), currentField.getColumn());
//
//                player.interact(1000, new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Field currentField = grid.getField(player.getLastCoordinates().getFirst(), player.getLastCoordinates().getSecond());
//
//                        grid.setCoordsAndRecalculate(currentField.getRow(), currentField.getColumn());
//
//                        System.err.println(DaemonUtils.tag() + "MATRICA \n" +  grid.gridToString().toString());
//                        player.interact(1000, this::run);
//                    }
//                });

//                cameraSwitcher.start();

                for(int i = 0; i < noOfZombies; ++i) {

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


                    ZombieDaemon zombie = new ZombieDaemon(
                            gameConsumer,
                            new Zombie(
                                    zombieMove270[0],
                                    zombieMoveAnimation,
                                    Pair.create(zombieViews[i].getAbsoluteX(), zombieViews[i].getAbsoluteY()),
                                    dXY
                            )
                    ).setName( i % 50 ==0 ? "DebugZombie no: " + i : "Zombie");

                    zombie.setAnimateZombieSideQuest(renderer)
                            .setSleepInterval(80)
                            .setClosure(new ZombieSpriteAnimateClosure(zombieViews[i]));


                    Field zeroField = grid.getField(zombie.getLastCoordinates().getFirst(), zombie.getLastCoordinates().getSecond());

                    Field firstF = grid.getMinWeightOfNeighbors(zeroField);

                    zombie.start().rotateTowards(firstF.getCenterX(), firstF.getCenterY()).goTo(firstF.getCenterX(), firstF.getCenterY(), 6.4F, new Closure<Boolean>() {

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

                            if (ImageTranslationMover.absDistance(player.getLastCoordinates(), zombie.getLastCoordinates()) < 10) {

                                if (player.getHp() - 100 < 1)
                                    player.pushSprite(explosionSprite);
                                else
                                    player.setHp(player.getHp() - 100);

                                zombie.attack(1000,
                                        () -> zombie.rotateTowards(next.getCenterX(), next.getCenterY())
                                                //() ->
                                                        .goTo(next.getCenterX(), next.getCenterY(), 6, this::onReturn)
                                        //)
                                );
                            } else
                                zombie.rotateTowards(next.getCenterX(), next.getCenterY()).goTo(next.getCenterX(), next.getCenterY(), 6, this::onReturn);
                                //);
                        }
                    });
                }




//
//                AngleToSpriteArray angleToSpriteArray = new AngleToSpriteArray(36);
//
//                angleToSpriteArray.mapAllAngles(angle -> {
//                    System.out.println(DaemonUtils.tag() + "ANGLE: " + angle);
//                    return new Image[]{playerSprite[angle / 10]};
//                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
