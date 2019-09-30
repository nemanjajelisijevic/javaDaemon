package com.daemonize.game;

import com.daemonize.daemonengine.DaemonEngine;
import com.daemonize.daemonengine.closure.BareClosure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.InterruptibleSleepSideQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.RotatingSpriteImageMover;

import com.daemonize.graphics2d.images.Image;

import com.daemonize.graphics2d.images.imageloader.ImageManager;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.game.repo.QueuedEntityRepo;
import com.daemonize.game.repo.StackedEntityRepo;
import com.daemonize.graphics2d.scene.Scene2D;

import com.daemonize.game.tabel.Field;
import com.daemonize.game.tabel.Grid;

import com.daemonize.graphics2d.scene.views.Button;
import com.daemonize.graphics2d.scene.views.CompositeImageViewImpl;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.sound.DummySoundManager;
import com.daemonize.sound.SoundClip;
import com.daemonize.sound.SoundException;
import com.daemonize.sound.SoundManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Game {

    //common repo for bullets, rockets and enemy missiles
    private static class ProjectileRepo extends StackedEntityRepo<BulletDoubleDaemon> {

        private Consumer renderer;
        private Image[] projectileSprite;
        public Set<BulletDoubleDaemon> activeProjectiles = new HashSet<>();

        public ProjectileRepo(Consumer renderer, Image[] projectileSprite) {
            this.renderer = renderer;
            this.projectileSprite = projectileSprite;
        }

        @Override
        public void onAdd(BulletDoubleDaemon projectile) {
            renderer.consume(() -> {
                for (ImageView view : projectile.getViews())
                    view.hide();
            });
            projectile.clearAndInterrupt().clearVelocity().popSprite();
            activeProjectiles.remove(projectile);
        }

        @Override
        public void onGet(BulletDoubleDaemon projectile) {
            projectile.setSprite(projectileSprite);
            renderer.consume(()->{
                for (ImageView view : projectile.getViews())
                    view.setAbsoluteX(projectile.getLastCoordinates().getFirst())
                            .setAbsoluteY(projectile.getLastCoordinates().getSecond())
                            .show();
                activeProjectiles.add(projectile);
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

    //state holder
    private DaemonChainScript stateChain = new DaemonChainScript();

    //Scene
    private Scene2D scene;

    //BackgroundImage
    private Image backgroundImage;
    private ImageView backgroundView;

    //screen borders
    private int borderX;
    private int borderY;

    //grid
    private Grid<TowerDaemon> grid;
    private int rows;
    private int columns;
    private ImageView[][] gridViewMatrix;
    private ImageView[][] diagonalMatrix;

    private Image fieldImage;
    private Image fieldImageTower;
    private Image fieldImageTowerDen;

    private Image fieldGreenDiagonal;

    private DaemonEngine fieldEraserEngine;

    //score
    private int score = 99999;
    private ImageView scoreBackGrView;
    private ImageView scoreTitleView;
    private ImageView[] viewsNum;
    private InfoTable infoScore;

    private Image dollarSign;
    private Pair<ImageView, ImageView> moneyView;
    private MoneyHandlerDaemon moneyDaemon;

    private Image[] dialogueImageTowerUpgrade;

    private Image upgradeButtonImage;
    private Image upgradeButtonImagePressed;

    private Image saleButtonImage;
    private Image saleButtonImagePressed;

    private Image closeButtonImage;
    private Image closeButtonImagePressed;

    private Image scoreBackGrImage;
    private Image[] scorenumbersImages;
    private Image[] moneyNumbersImages;

    //towers
    private int towerHp = 300;

    private Image[] redTowerUpgSprite;
    private Image[] blueTowerUpgSprite;
    private Image[] greenTowerUpgSprite;

    private List<Image[]> redTower;
    private List<Image[]> blueTower;
    private List<Image[]> greenTower;

    private ImageView[][] towerHpViwes;

    private Set<TowerDaemon> towers = new HashSet<>();
    private int range;
    private Tower.TowerType towerSelect;

    private Image[] currentTowerSprite;

    private EagerMainQuestDaemonEngine towerSpriteUpgrader;

    //tower rockets
    private int maxRockets = 150;
    private ProjectileRepo rocketRepo;

    //towers dialogue
    private TowerUpgradeDialog towerUpgradeDialogue;
    private TowerSelectDialogue selectTowerDialogue;

    private Image selection;
    private Image deselection;

    //enemies
    private long enemyCounter = 0;
    private float enemyVelocity = 1;
    private int enemyHp = 500;

    private Image[] enemySprite;
    private Image[] healthBarSprite;
    private Image[] enemyMissileSprite;

    private Image paralyzed;

    private Image target;

    private DummyDaemon enemyGenerator;
    private List<Long> enemyGenerateIntervals = new ArrayList<>();
    private Iterator<Long> enemyGenerateIntervalIt;

    {
        enemyGenerateIntervals.add(5000L);
        enemyGenerateIntervals.add(1000L);
        enemyGenerateIntervals.add(2000L);
        enemyGenerateIntervals.add(1000L);
        enemyGenerateIntervals.add(2000L);
        enemyGenerateIntervals.add(1000L);
        enemyGenerateIntervals.add(6000L);
        enemyGenerateIntervals.add(500L);
        enemyGenerateIntervals.add(2000L);
        enemyGenerateIntervals.add(500L);
        enemyGenerateIntervals.add(15000L);
        enemyGenerateIntervalIt = enemyGenerateIntervals.iterator();
    }

    private DaemonUtils.IntervalRegulator enemyGenerationRegulator = () ->
            enemyGenerateIntervalIt.hasNext()
                    ? enemyGenerateIntervalIt.next()
                    : (enemyGenerateIntervalIt = enemyGenerateIntervals.iterator()).next();

    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();

    private ProjectileRepo enemyMissileRepo;

    private int maxEnemies = 80;
    private QueuedEntityRepo<EnemyDoubleDaemon> enemyRepo;

    //explosions
    private Image[] explodeSprite;
    private Image[] rocketExplodeSprite;
    private Image[] miniExplodeSprite;

    //bullets
    private Image[] bulletSprite;
    private Image[] bulletSpriteRocket;

    private int bulletDamage = 2;
    private int rocketExplosionRange;

    private int maxBullets = 150;
    private ProjectileRepo bulletRepo;

    //laser
    private LaserBulletDaemon laser;
    private List<ImageView> laserViews;
    private Image[] laserSprite;
    private int laserViewNo = 50;

    //laser paralyzer
    private EagerMainQuestDaemonEngine enemyParalyizer;
    private int enemyParalyzingInterval = 3000;

    //random int
    private Random random = new Random();

    private int getRandomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    //resolution scaling attribute
    private float dXY;

    //animate closures
    private static class ImageAnimateClosure implements Closure<ImageMover.PositionedImage> {

        private ImageView view;

        public ImageAnimateClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage> aReturn) {
            ImageMover.PositionedImage posBmp = aReturn.runtimeCheckAndGet();
            view.setAbsoluteX(posBmp.positionX)
                    .setAbsoluteY(posBmp.positionY)
                    .setImage(posBmp.image);
        }
    }

    private static class MultiViewAnimateClosure implements Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
        @Override
        public void onReturn(Return<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> aReturn) {
            GenericNode.forEach(aReturn.runtimeCheckAndGet(), arg -> {
                ImageMover.PositionedImage image = arg.getFirst();
                arg.getSecond().setAbsoluteX(image.positionX)
                        .setAbsoluteY(image.positionY)
                        .setImage(image.image);
            });
        }
    }

    //sound manager
    private SoundManager currentSoundManager;
    private SoundManager activeSoundManager;
    private SoundManager dummySoundManager;

    private SoundClip rocketExplosionSound;
    private SoundClip bulletSound;
    private SoundClip bigExplosion;
    private SoundClip laserSound;

    private Image soundOnImage;
    private Image soundOffImage;

    private Button soundButton;

    private SoundClip soundTogglerSound;
    private SoundClip towerConstructionSound;
    private SoundClip towerSelectionSound;

    //uncaught exception handler
    public Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.err.println("Uncaught exception in: " + t.getName() + ", ID: " + t.getId());
            e.printStackTrace();
            System.exit(1);
        }
    };

    //construct
    public Game(
            Renderer2D renderer,
            ImageManager imageManager,
            SoundManager soundManager,
            int borderX,
            int borderY,
            int rows,
            int columns,
            float gridX,
            float gridY
    ) {
        this.renderer = renderer;
        this.imageManager = imageManager;
        this.activeSoundManager = soundManager;
        this.dummySoundManager = new DummySoundManager();
        this.currentSoundManager = activeSoundManager;
        this.borderX = borderX;
        this.borderY = borderY;
        this.scene = new Scene2D();
        this.gameConsumer = new DaemonConsumer("Game Consumer");
        this.rows = rows;
        this.columns = columns;
        this.grid = new Grid<TowerDaemon>(
                rows,
                columns,
                Pair.create(0, 0),
                Pair.create(rows - 1, columns - 1),
                gridX,
                gridY,
                ((borderX * 70) / 100) / columns
        );

        this.dXY = ((float) borderX) / 1000;

        //TODO following is for Windows packaging
//        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
//        try {
//            String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
//            activeSoundManager.setJarResourceLocation(jarPath, "");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    public boolean isPaused() {
        return paused;
    }

    private Runnable onPauseRunnable = () -> {};

    public void pause() {
        onPauseRunnable.run();
    }

    public void cont() { //continueAll
        gameConsumer.consume(()->{
            enemyGenerator.start();
            for (EnemyDoubleDaemon enemy : activeEnemies)
                enemy.cont();
            for (TowerDaemon tower : towers)
                tower.cont();
            for(BulletDoubleDaemon rocket : rocketRepo.activeProjectiles)
                rocket.cont();
            for(BulletDoubleDaemon bullet : bulletRepo.activeProjectiles)
                bullet.cont();
            for(BulletDoubleDaemon missile : enemyMissileRepo.activeProjectiles)
                missile.cont();
            renderer.start();
            towerSpriteUpgrader.start();
            fieldEraserEngine.start();
            paused = false;
        });
    }

    public boolean isRunning() {
        return running;
    }

    public Game run() {
        gameConsumer.start().consume(()->{
            gameConsumer.consume(stateChain::run);
            this.running = true;
            this.paused = false;
        });
        return this;
    }

    public Game stop(){
        gameConsumer.consume(()-> {
            towerSpriteUpgrader.stop();
            fieldEraserEngine.stop();
            activeSoundManager.stop();
            moneyDaemon.stop();
            enemyGenerator.stop();
            for(EnemyDoubleDaemon enemy : new ArrayList<>(activeEnemies)) enemy.stop();
            for (TowerDaemon tower : towers) tower.stop();
            laser.stop();
            scene.unlockViews();
            renderer.stop();
            this.running = false;
            gameConsumer.stop();
            System.exit(0);
        });
        return this;
    }

    //controller
    @FunctionalInterface
    private static interface TouchController {
        void onTouch(float x, float y);
    }

    private TouchController touchController = ((x, y) -> {});

    public Game onTouch(float x, float y) {
        touchController.onTouch(x, y);
        return this;
    }

    {
        //init state (loading sprites)
        stateChain.addState(()-> { //image loading State

            try {

                backgroundImage = imageManager.loadImageFromAssets("maphi.jpg", borderX, borderY);

                //laser views init
                laserViews = new ArrayList<>(laserViewNo);

                Image[] loadingSprite = new Image[] {imageManager.loadImageFromAssets("greenPhoton.png", borderX / 150, borderX / 150)};

                int startX = borderX / 5;
                int endX = borderX * 4 / 5;

                int step = (endX - startX) / laserViewNo;

                for (int i = 0; i < laserViewNo; ++i) {
                    int currX = startX + (i * step);
                    laserViews.add(new ImageViewImpl("laser View " + i).setImage(loadingSprite[0]).hide().setAbsoluteX(currX).setAbsoluteY(borderY * 3 / 4).setZindex(1));
                }

                Iterator<ImageView> loaderBar = laserViews.iterator();

                Scene2D loadingScene = new Scene2D();
                loadingScene.addImageView(
                        new ImageViewImpl("Loading background View")
                                .setAbsoluteX(borderX /2)
                                .setAbsoluteY(borderY /2)
                                .setImage(backgroundImage)
                                .setZindex(0)
                                .show()
                );

                loadingScene.addImageViews(laserViews);
                loadingScene.lockViews();
                renderer.setScene(loadingScene).drawScene();

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                int gridWidth = (borderX * 70) / 100;

                int rows = 6;
                int columns = 9;

                int width = gridWidth/columns;
                int height = width; //160

                fieldImage = imageManager.loadImageFromAssets("greenOctagon.png", width, height);
                fieldImageTower = imageManager.loadImageFromAssets("blueOctagon.png", width, height);
                fieldImageTowerDen = imageManager.loadImageFromAssets("redOctagon.png", width, height);

                fieldGreenDiagonal = imageManager.loadImageFromAssets("greenDiagonal.png", width * 2 / 3, height * 2 / 3);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                int scoreWidth = borderX / 5;
                int selectionWidth = borderX / 10;

                int scoreHeight = borderY / 5;
                int selectionHeight = borderY / 2;

                upgradeButtonImage = imageManager.loadImageFromAssets("ButtonUpgrade.png",borderX / 6,borderY / 10);
                upgradeButtonImagePressed = imageManager.loadImageFromAssets("ButtonUpgradePressed.png",borderX / 6,borderY / 10);

                closeButtonImage = imageManager.loadImageFromAssets("ButtonX.png",borderX / 20,borderY /  10);
                closeButtonImagePressed = imageManager.loadImageFromAssets("ButtonXPressed.png",borderX / 20,borderY /  10);

                saleButtonImage = imageManager.loadImageFromAssets("ButtonSale.png",borderX / 6,borderY / 10);
                saleButtonImagePressed = imageManager.loadImageFromAssets("ButtonSalePressed.png",borderX / 6,borderY / 10);

                selection = imageManager.loadImageFromAssets("greenOctagon.png", selectionWidth, selectionWidth);
                deselection = imageManager.loadImageFromAssets("redOctagon.png", selectionWidth, selectionWidth);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                scoreBackGrImage = imageManager.loadImageFromAssets("SmallBox.png", scoreWidth, scoreHeight);

                laserSprite = new Image[] {imageManager.loadImageFromAssets("greenPhoton.png",  width / 10, width / 10)};

                paralyzed = imageManager.loadImageFromAssets("paralyzed1.png", width, height);

                //init enemy projectileSprite
                enemySprite = new Image[36];

                for (int i = 0; i < 36; i++) {

                    if (i % 12 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    enemySprite[i] = imageManager.loadImageFromAssets("plane" + i + "0.png", width, height);
                }

                target = imageManager.loadImageFromAssets("target.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                //bullet projectileSprite
                int bulletSize0 = width / 8;//20;
                bulletSprite = new Image[4];
                bulletSprite[0] = imageManager.loadImageFromAssets("thebarnstarRed.png", bulletSize0, bulletSize0);
                bulletSprite[1] = imageManager.loadImageFromAssets("thebarnstarRed90.png", bulletSize0, bulletSize0);
                bulletSprite[2] = imageManager.loadImageFromAssets("thebarnstarRed180.png", bulletSize0, bulletSize0);
                bulletSprite[3] = imageManager.loadImageFromAssets("thebarnstarRed270.png", bulletSize0, bulletSize0);

                int bulletSize = width / 3;//60;
                bulletSpriteRocket = new Image[36];

                for (int i = 0; i < 36; i++) {

                    if (i % 12 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    bulletSpriteRocket[i] = imageManager.loadImageFromAssets("rocket" + i + "0.png", bulletSize, bulletSize);

                }

                //enemy missiles
                enemyMissileSprite = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 12 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    enemyMissileSprite[i] = imageManager.loadImageFromAssets("enemyMissile" + i + "0.png", bulletSize, bulletSize);
                }

                //explosion projectileSprite
                explodeSprite = new Image[33];
                for (int i = 0; i < explodeSprite.length; ++i) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    explodeSprite[i] = imageManager.loadImageFromAssets("Explosion" + (i + 1) + ".png", width, height);
                }

                int miniWidth = width / 3;
                int miniHeight = height / 3;

                rocketExplodeSprite = new Image[33];
                for (int i = 0; i < rocketExplodeSprite.length; ++i) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    //rocketExplodeSprite[i] = imageManager.rescaleImage(explodeSprite[i], miniWidth, miniHeight);
                    rocketExplodeSprite[i] = imageManager.loadImageFromAssets("Explosion" + (i + 1) + ".png", miniWidth, miniHeight);
                }


                miniExplodeSprite = new Image[20];
                for (int i = 0; i < miniExplodeSprite.length; ++i) {

                    if (i % 5 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    miniExplodeSprite[i] = imageManager.loadImageFromAssets("Bild-0000" + (i < 10 ? "0" + (i + 1) : (i + 1)) + ".png", miniWidth, miniHeight);
                }

                // blue tower
                Image[] blueTowerI = new Image[36];
                for (int i = 0; i < 36; i++)
                    blueTowerI[i] = imageManager.loadImageFromAssets("mg" + i + "0.png", width, height);

                Image[] blueTowerII = new Image[36];
                for (int i = 0; i < 36; i++)
                    blueTowerII[i] = imageManager.loadImageFromAssets("bgII" + i + "0.png", width, height);

                Image[] blueTowerIII = new Image[36];
                for (int i = 0; i < 36; i++)
                    blueTowerIII[i] = imageManager.loadImageFromAssets("bgIII" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                blueTower = new ArrayList<>(3);
                blueTower.add(blueTowerI);
                blueTower.add(blueTowerII);
                blueTower.add(blueTowerIII);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                //green tower
                Image[] greenTowerI = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    greenTowerI[i] = imageManager.loadImageFromAssets("greenLS00" + i + "0.png", width, height);
                }

                Image[] greenTowerII = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    greenTowerII[i] = imageManager.loadImageFromAssets("lsII" + i + "0.png", width, height);
                }

                Image[] greenTowerIII = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    greenTowerIII[i] = imageManager.loadImageFromAssets("lsIII" + i + "0.png", width, height);
                }

                greenTower = new ArrayList<>(3);
                greenTower.add(greenTowerI);
                greenTower.add(greenTowerII);
                greenTower.add(greenTowerIII);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                //red tower
                Image[] redTowerI = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    redTowerI[i] = imageManager.loadImageFromAssets("rmI" + i + "0.png", width, height);
                }

                Image[] redTowerII = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    redTowerII[i] = imageManager.loadImageFromAssets("rmII" + i + "0.png", width, height);
                }

                Image[] redTowerIII = new Image[36];
                for (int i = 0; i < 36; i++) {

                    if (i % 10 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    redTowerIII[i] = imageManager.loadImageFromAssets("rmIII" + i + "0.png", width, height);
                }

                redTower = new ArrayList<>(3);
                redTower.add(redTowerI);
                redTower.add(redTowerII);
                redTower.add(redTowerIII);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                int width_hp = (width * 3) / 4; //120;
                int height_hp = height / 5;//30;

                healthBarSprite = new Image[10];
                for (int i = 0; i < healthBarSprite.length; ++i) {

                    if (i % 5 == 0 && loaderBar.hasNext()) {
                        loaderBar.next().show();
                        renderer.drawScene();
                    }

                    healthBarSprite[i] = imageManager.loadImageFromAssets("health_bar_" + (i + 1) + "0.png", width_hp, height_hp);
                }

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                Image score = imageManager.loadImageFromAssets("SmallBox.png", 300, 150);
                Image titleScore = imageManager.loadImageFromAssets("HealthBar.png", 300, 70);

                int numWidth = width / 3;//50;
                int numHeight = width / 2;//70;

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                dollarSign = imageManager.loadImageFromAssets("money.png", numWidth / 2, numHeight / 2);

                scorenumbersImages = new Image[10];

                scorenumbersImages[0] = imageManager.loadImageFromAssets("0.png", numWidth, numHeight);
                scorenumbersImages[1] = imageManager.loadImageFromAssets("1.png", numWidth, numHeight);
                scorenumbersImages[2] = imageManager.loadImageFromAssets("2.png", numWidth, numHeight);
                scorenumbersImages[3] = imageManager.loadImageFromAssets("3.png", numWidth, numHeight);
                scorenumbersImages[4] = imageManager.loadImageFromAssets("4.png", numWidth, numHeight);
                scorenumbersImages[5] = imageManager.loadImageFromAssets("5.png", numWidth, numHeight);
                scorenumbersImages[6] = imageManager.loadImageFromAssets("6.png", numWidth, numHeight);
                scorenumbersImages[7] = imageManager.loadImageFromAssets("7.png", numWidth, numHeight);
                scorenumbersImages[8] = imageManager.loadImageFromAssets("8.png", numWidth, numHeight);
                scorenumbersImages[9] = imageManager.loadImageFromAssets("9.png", numWidth, numHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                moneyNumbersImages = new Image[10];

                moneyNumbersImages[0] = imageManager.loadImageFromAssets("0.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[1] = imageManager.loadImageFromAssets("1.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[2] = imageManager.loadImageFromAssets("2.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[3] = imageManager.loadImageFromAssets("3.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[4] = imageManager.loadImageFromAssets("4.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[5] = imageManager.loadImageFromAssets("5.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[6] = imageManager.loadImageFromAssets("6.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[7] = imageManager.loadImageFromAssets("7.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[8] = imageManager.loadImageFromAssets("8.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[9] = imageManager.loadImageFromAssets("9.png", numWidth / 2, numHeight / 2);

                int upgradeDialogBackrgoundImageWidth = borderX / 3;
                int upgradeDialogBackgroundImageHeight = (borderY * 16 / 50);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                soundOnImage = imageManager.loadImageFromAssets("soundOn.png", numWidth /** 4 / 5*/, numHeight /** 4 / 5*/);
                soundOffImage = imageManager.loadImageFromAssets("soundOff.png", numWidth /** 4 / 5*/, numHeight /** 4 / 5*/);

                Image[] dialogUpgradeTower1 = new Image[3];

                dialogUpgradeTower1[0] = imageManager.loadImageFromAssets("mgleve2.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower1[1] = imageManager.loadImageFromAssets("mgleve3.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower1[2] = imageManager.loadImageFromAssets("mgleveTOP.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);

                Image[] dialogUpgradeTower2 = new Image[3];
                dialogUpgradeTower2[0] = imageManager.loadImageFromAssets("rcleve2.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower2[1] = imageManager.loadImageFromAssets("rcleve3.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower2[2] = imageManager.loadImageFromAssets("rcleveTOP.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);

                Image[] dialogUpgradeTower3 = new Image[3];
                dialogUpgradeTower3[0] = imageManager.loadImageFromAssets("lsleve2.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower3[1] = imageManager.loadImageFromAssets("lsleve3.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower3[2] = imageManager.loadImageFromAssets("lsleveTOP.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                redTowerUpgSprite = dialogUpgradeTower1;
                blueTowerUpgSprite = dialogUpgradeTower2;
                greenTowerUpgSprite = dialogUpgradeTower3;
                dialogueImageTowerUpgrade = redTowerUpgSprite;

                soundTogglerSound = activeSoundManager.loadSoundClip("soundTogglerSound.wav");
                towerConstructionSound = activeSoundManager.loadSoundClip("towerConstructionSound.wav");
                towerSelectionSound = activeSoundManager.loadSoundClip("towerSelectionSound.wav");

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                //sounds init
                //soundManager.setConsumer(gameConsumer);
                bulletSound = activeSoundManager.loadSoundClip("bullet.wav");

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                rocketExplosionSound = activeSoundManager.loadSoundClip("rocketExplosion.wav");

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                bigExplosion = activeSoundManager.loadSoundClip("bigExplosion.wav");

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                laserSound = activeSoundManager.loadSoundClip("laser.wav");

                while (loaderBar.hasNext()){
                    loaderBar.next().show();
                    renderer.drawScene();

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        //
                    }
                }

                for(ImageView view: laserViews)
                    view.hide();

                laserSprite = new Image[] {imageManager.loadImageFromAssets("greenPhoton.png",  width / 15, width / 15)};

                renderer.drawScene();

                gameConsumer.consume(stateChain::next);

            } catch (IOException | SoundException ex) {
                System.err.println(DaemonUtils.tag() + "Could not init game!");
                ex.printStackTrace();
                System.exit(1);
            }

        }).addState(()-> { //views and dialogs population

            //add background to scene
            backgroundView = scene.addImageView(new ImageViewImpl("Background").setImageWithoutOffset(backgroundImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(0).show());

            //dialogues and ui views
            scoreBackGrView = new ImageViewImpl("Score Background").setImage(scoreBackGrImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(3);
            scoreTitleView = new ImageViewImpl("Score Title").setAbsoluteX(0).setAbsoluteY(0).setZindex(4);

            viewsNum = new ImageView[5];
            viewsNum[0] = new ImageViewImpl("Score 1. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5);
            viewsNum[1] = new ImageViewImpl("Score 2. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5);
            viewsNum[2] = new ImageViewImpl("Score 3. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5);
            viewsNum[3] = new ImageViewImpl("Score 4. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5);
            viewsNum[4] = new ImageViewImpl("Score 5. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5);

            //money views
            moneyView = Pair.create(
                    scene.addImageView(new ImageViewImpl("Money Amount").setZindex(10).setImage(scorenumbersImages[0]).hide()),
                    scene.addImageView(new ImageViewImpl("Dollar Sign").setZindex(10).setImage(dollarSign).hide())
            );

            soundButton =  (Button) new Button("Sound Toggler", soundOnImage).setAbsoluteX(borderX  * 85 / 100).setAbsoluteY(borderY * 30 /100).setZindex(20).show();
            soundButton.onClick(() -> {
                activeSoundManager.playSound(soundTogglerSound);
                if (soundButton.getImage().equals(soundOnImage)) {
                    currentSoundManager = dummySoundManager;
                    renderer.consume(() -> soundButton.setImage(soundOffImage));
                } else {
                    currentSoundManager = activeSoundManager;
                    renderer.consume(() -> soundButton.setImage(soundOnImage));
                }
            });

            scene.addImageView(soundButton);

            Button upgradeButton = new Button("Upgrade", upgradeButtonImage);
            upgradeButton.onClick(()->{

                TowerDaemon tow = towerUpgradeDialogue.getTower();
                tow.levelUp();

                Image[] currentSprite = null;

                switch (tow.getTowertype()) {
                    case TYPE1:
                        currentSprite = redTower.get(tow.getTowerLevel().currentLevel - 1);
                        break;
                    case TYPE2:
                        currentSprite = blueTower.get(tow.getTowerLevel().currentLevel - 1);
                        break;
                    case TYPE3:
                        currentSprite =  greenTower.get(tow.getTowerLevel().currentLevel - 1);
                        break;
                }

                Image[] rotSprite = currentSprite;

                towerSpriteUpgrader.daemonize(() -> {
                        tow.setRotationSprite(rotSprite);
                        return tow.getPrototype().updateSprite();
                    }, new MultiViewAnimateClosure()::onReturn);

                currentSoundManager.playSound(towerSelectionSound);

                renderer.consume(()->upgradeButton.disable().setImage(upgradeButtonImagePressed));

                towerSpriteUpgrader.daemonize(gameConsumer, ()->Thread.sleep(100), ()->{

                    CompositeImageViewImpl towerView = towerUpgradeDialogue.getTowerUpgrade().getViewByName("TowerView");

                    renderer.consume(()->towerView.setImage(dialogueImageTowerUpgrade[tow.getTowerLevel().currentLevel - 1]));

                    if (score > 2 && tow.getTowerLevel().currentLevel < 3)
                        renderer.consume(towerUpgradeDialogue.getTowerUpgrade().getViewByName("Upgrade")::show);
                    else
                        renderer.consume(towerUpgradeDialogue.getTowerUpgrade().getViewByName("Upgrade")::hide);

                    score -= 2;

                    renderer.consume(()->infoScore.setNumbers(score));
                    renderer.consume(()->upgradeButton.enable().setImage(upgradeButtonImage));
                });
            });

            Button closeButton = new Button("Close", closeButtonImage);
            closeButton.onClick(()->{
                currentSoundManager.playSound(soundTogglerSound);
                renderer.consume(()->closeButton.disable().setImage(closeButtonImagePressed));
                towerSpriteUpgrader.daemonize(gameConsumer, ()->Thread.sleep(100), ()->{
                    renderer.consume(()->closeButton.enable().setImage(closeButtonImage));
                    renderer.consume(towerUpgradeDialogue.getTowerUpgrade()::hide);
                });
            });

            Button saleButton = new Button("Sale", saleButtonImage);
            saleButton.onClick(()->{
                currentSoundManager.playSound(towerSelectionSound);
                renderer.consume(()->saleButton.disable().setImage(saleButtonImagePressed));
                towerSpriteUpgrader.daemonize(gameConsumer, ()->Thread.sleep(100), ()->{
                    TowerDaemon tower = towerUpgradeDialogue.getTower();
                    tower.setShootable(false);
                    renderer.consume(tower.getHpView()::hide);

                    Field<TowerDaemon> field = grid.getField(
                            tower.getLastCoordinates().getFirst(),
                            tower.getLastCoordinates().getSecond()
                    );

                    //stop and remove tower
                    tower.stop();
                    towers.remove(tower);
                    field.setObject(null);

                    renderer.consume(()->saleButton.enable().setImage(saleButtonImage));

                    //remove tower from grid and recalculate path
                    if (grid.destroyTower(field.getRow(), field.getColumn())) {
                        renderer.consume(()->{
                            gridViewMatrix[field.getRow()][field.getColumn()].setImage(fieldImage).hide();
                            towerUpgradeDialogue.getTowerUpgrade().hide();
                            infoScore.setNumbers(++score);
                        });
                    }
                });
            });

            towerUpgradeDialogue = new TowerUpgradeDialog(
                    borderX / 3,
                    borderY / 2,
                    dialogueImageTowerUpgrade[0],
                    upgradeButton,
                    closeButton,
                    saleButton,
                    borderX / 3,
                    borderY / 2,
                    20
            );

            Button tow1 = new Button("TowerType1", redTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE1;
                currentTowerSprite = redTower.get(0);
                currentSoundManager.playSound(towerSelectionSound);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(selection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
                });
            });

            Button tow2 = new Button("TowerType2", blueTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE2;
                currentTowerSprite = blueTower.get(0);
                currentSoundManager.playSound(towerSelectionSound);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(selection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
                });

            });

            Button tow3 = new Button("TowerType3", greenTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE3;
                currentTowerSprite = greenTower.get(0);
                currentSoundManager.playSound(towerSelectionSound);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(selection);
                });
            });

            selectTowerDialogue = new TowerSelectDialogue(
                    borderX * 85 / 100,
                    borderY * 65 / 100,
                    deselection.getWidth(),
                    deselection.getHeight() * 3,
                    deselection,
                    tow1,
                    tow2,
                    tow3
            );

            scene.addImageViews(towerUpgradeDialogue.getTowerUpgrade().getAllViews());
            scene.addImageViews(selectTowerDialogue.getSelectTowerDialogue().getAllViews());
            scene.addImageView(scoreBackGrView);

            renderer.consume(selectTowerDialogue.getSelectTowerDialogue()::show);

            for (ImageView view : viewsNum)
                scene.addImageView(view);

            //grid views
            gridViewMatrix = new ImageView[rows][columns];

            for (int j = 0; j < rows; ++j ) {
                for (int i = 0; i < columns; ++i)
                    gridViewMatrix[j][i] = scene.addImageView(new ImageViewImpl("Grid [" + j + "][" + i +"]"))
                            .setAbsoluteX(grid.getGrid()[j][i].getCenterX())
                            .setAbsoluteY(grid.getGrid()[j][i].getCenterY())
                            .setImage(grid.getField(j, i).isWalkable() ? fieldImage : fieldImageTower)
                            .setZindex(3)
                            .hide();
            }

            //diagonal views
            diagonalMatrix = new ImageView[rows - 1][columns - 1];

            for (int j = 0; j < rows - 1; ++j )
                for (int i = 0; i < columns - 1; ++i)
                    diagonalMatrix[j][i] = scene.addImageView(new ImageViewImpl("Grid [" + j + "][" + i +"]"))
                            .setAbsoluteX(grid.getGrid()[j][i].getCenterX() + gridViewMatrix[j][i].getImage().getWidth() / 2)
                            .setAbsoluteY(grid.getGrid()[j][i].getCenterY() + gridViewMatrix[j][i].getImage().getHeight() / 2)
                            .setImage(fieldGreenDiagonal)
                            .setZindex(3)
                            .hide();


            //tower hp views
            towerHpViwes = new ImageView[rows][columns];

            for (int j = 0; j < rows; ++j )
                for (int i = 0; i < columns; ++i) {
                    String towerName = "Tower[" + i + "][" + j + "]";
                    towerHpViwes[j][i] = scene.addImageView(
                            new ImageViewImpl(towerName + " HP View")
                                    .setImage(healthBarSprite[0])
                                    .hide()
                                    .setAbsoluteX(0)
                                    .setAbsoluteY(0)
                                    .setZindex(10)
                    );
                }

            //enemy repo init
            enemyRepo = new QueuedEntityRepo<EnemyDoubleDaemon>() {
                @Override
                public void onAdd(EnemyDoubleDaemon enemy) {
                    enemy.setShootable(false).clearVelocity().clearAndInterrupt();

                    renderer.consume(()->{
                        enemy.getHpView().hide().setAbsoluteX(0).setAbsoluteY(0);
                        enemy.getTargetView().hide().setAbsoluteX(0).setAbsoluteY(0);
                        enemy.getParalyzedView().hide().setAbsoluteX(0).setAbsoluteY(0);
                    });

                    System.err.println(DaemonUtils.tag() + enemy.getEnginesState().toString());

                    enemy.pushSprite(explodeSprite, 0, ()->{
                        renderer.consume(()->enemy.getView().hide().setAbsoluteX(0).setAbsoluteY(0));
                        enemy.popSprite().setPreviousField(null).setCoordinates(grid.getStartingX(), grid.getStartingY());
                    }).queueStop();

                    activeEnemies.remove(enemy);

                    if (activeEnemies.isEmpty()) {

                        fieldEraserEngine.daemonize(()->{

                            Field currentErasingField = grid.getField(0, 0);

                            do {
                                if (gridViewMatrix[currentErasingField.getRow()][currentErasingField.getColumn()].isShowing() && gridViewMatrix[currentErasingField.getRow()][currentErasingField.getColumn()].getImage().equals(fieldImage)) {

                                    renderer.consume(gridViewMatrix[currentErasingField.getRow()][currentErasingField.getColumn()]::hide);

                                    if ((currentErasingField.getRow() > 0) && (currentErasingField.getColumn() > 0) && diagonalMatrix[currentErasingField.getRow() - 1][currentErasingField.getColumn() - 1].isShowing())
                                        renderer.consume(diagonalMatrix[currentErasingField.getRow() - 1][currentErasingField.getColumn() - 1]::hide);

                                    if ((currentErasingField.getRow() > 0) && (currentErasingField.getColumn() < columns - 1) && diagonalMatrix[currentErasingField.getRow() - 1][currentErasingField.getColumn()].isShowing())
                                        renderer.consume(diagonalMatrix[currentErasingField.getRow() - 1][currentErasingField.getColumn()]::hide);

                                    Thread.sleep(150);
                                } else
                                    break;

                            } while ((currentErasingField = grid.getMinWeightOfNeighbors(currentErasingField)) != null);

                            if (activeEnemies.isEmpty())
                                renderer.consume(() -> {
                                    for (int j = 0; j < rows; ++j )
                                        for (int i = 0; i < columns; ++i)
                                            if (gridViewMatrix[j][i].getImage().equals(fieldImage) && gridViewMatrix[j][i].isShowing())
                                                gridViewMatrix[j][i].hide();

                                    for (int j = 0; j < rows - 1; ++j )
                                        for (int i = 0; i < columns - 1; ++i)
                                            if (diagonalMatrix[j][i].getImage().equals(fieldGreenDiagonal) && diagonalMatrix[j][i].isShowing())
                                                diagonalMatrix[j][i].hide();
                                });
                        });
                    }
                }

                @Override
                public void onGet(EnemyDoubleDaemon enemy) {
                    System.err.println(DaemonUtils.tag() + "Enemy repo size: " + this.size());

                    System.err.println(DaemonUtils.tag() + enemy.getName() + " STATES: " + enemy.getEnginesState().toString());

                    enemy.setAnimateEnemySideQuest(renderer).setClosure(new MultiViewAnimateClosure());
                    enemy.setShootable(true)
                            .setVelocity(new ImageMover.Velocity(enemyVelocity, new ImageMover.Direction(1F, 0.0F)))
                            .setCoordinates(grid.getStartingX(), grid.getStartingY())
                            .clearAndInterrupt()
                            .start();

                    activeEnemies.add(enemy);

                    renderer.consume(()->{
                        enemy.getView().show();
                        enemy.getHpView().show();
                    });
                }
            };

            //bullet repo init
            bulletRepo = new ProjectileRepo(renderer, bulletSprite);
            bulletRepo.setName("Bullet repo");

            //rocket repo init
            rocketRepo = new ProjectileRepo(renderer, bulletSpriteRocket);
            rocketRepo.setName("Rocket repo");

            //enemy missiles repo init
            enemyMissileRepo = new ProjectileRepo(renderer, enemyMissileSprite);
            enemyMissileRepo.setName("Missile repo");

            //init enemies and fill enemy repo
            for (int i = 0; i < maxEnemies; ++i) {

                String enemyName = "Enemy instance no.: " + i;

                EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                        gameConsumer,
                        new Enemy(
                                enemySprite,
                                enemyVelocity,
                                enemyHp,
                                Pair.create(grid.getStartingX(), grid.getStartingY()),
                                dXY
                        ).setView(scene.addImageView(new ImageViewImpl(enemyName + " View").setImage(enemySprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(10)))
                                .setHpView(scene.addImageView(new ImageViewImpl(enemyName + " HP View").setImage(enemySprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(10)))
                                .setTargetView(scene.addImageView(new ImageViewImpl(enemyName + " Target View").setImage(target).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(10)))
                                .setHealthBarImage(healthBarSprite)
                                .setParalyzedImage(paralyzed)
                ).setParalyzedView(scene.addImageView(new ImageViewImpl(enemyName + " Paralyzed View").setImage(paralyzed).setAbsoluteX(0).setAbsoluteY(0).setZindex(11).hide()))
                .setName(enemyName)
                .setUncaughtExceptionHandler(uncaughtExceptionHandler);

                enemy.getPrototype().setBorders(
                        0,
                        (grid.getStartingX() + grid.getGridWidth()),
                        0,
                        (grid.getStartingY() + grid.getGridHeight())
                );

                enemy.setOutOfBordersConsumer(gameConsumer)
                        .setOutOfBordersClosure(()-> enemyRepo.add(enemy.clearAndInterrupt()))
                        .setAnimateEnemySideQuest(renderer)
                        .setClosure(new MultiViewAnimateClosure()::onReturn);

                enemyRepo.getStructure().add(enemy);
            }

            //init bullets and fill bullet repo
            for (int i = 0; i < maxBullets; ++i) {

                String bulletName = "Bullet instance no. " + i;

                BulletDoubleDaemon bulletDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        new Bullet(
                                bulletSprite,
                                0,
                                Pair.create((float) 0, (float) 0),
                                bulletDamage,
                                bulletSprite[0].getWidth(),
                                dXY
                        ).setView(scene.addImageView(new ImageViewImpl(bulletName + " View 1").setImage(bulletSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(1)))
                                .setView2(scene.addImageView(new ImageViewImpl(bulletName + " View 2").setImage(bulletSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(1)))
                                .setView3(scene.addImageView(new ImageViewImpl(bulletName + " View 3").setImage(bulletSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(1)))
                ).setName(bulletName).setUncaughtExceptionHandler(uncaughtExceptionHandler);

                bulletDoubleDaemon.getPrototype().setBorders(
                        grid.getStartingX(),//TODO fix offset
                        (grid.getStartingX() + grid.getGridWidth()),
                        grid.getStartingY(),
                        (grid.getStartingY() + grid.getGridHeight())
                );

                bulletDoubleDaemon.setOutOfBordersConsumer(gameConsumer)
                        .setOutOfBordersClosure(()->bulletRepo.add(bulletDoubleDaemon.clearAndInterrupt()));

                bulletDoubleDaemon.setAnimateBulletSideQuest(renderer)
                        .setClosure(new MultiViewAnimateClosure()::onReturn);

                bulletRepo.getStructure().push(bulletDoubleDaemon);
            }

            //init rockets and fill rocket repo
            for (int i = 0; i < maxRockets; ++i) {

                String rocketName = "Rocket instance no. " + i;

                BulletDoubleDaemon rocketDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        new Bullet(
                                bulletSpriteRocket,
                                0,
                                Pair.create((float) 0, (float) 0),
                                bulletDamage,
                                bulletSpriteRocket[0].getWidth(),
                                dXY
                        ).setView(scene.addImageView(new ImageViewImpl(rocketName + " View 1").setImage(bulletSpriteRocket[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(5)))
                                .setView2(scene.addImageView(new ImageViewImpl(rocketName + " View 2").setImage(bulletSpriteRocket[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(5)))
                                .setView3(scene.addImageView(new ImageViewImpl(rocketName + " View 3").setImage(bulletSpriteRocket[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(5)))
                ).setName(rocketName).setUncaughtExceptionHandler(uncaughtExceptionHandler);

                rocketDoubleDaemon.getPrototype().setBorders(0, borderX, 0, borderY);

                rocketDoubleDaemon.setOutOfBordersConsumer(gameConsumer)
                        .setOutOfBordersClosure(()-> rocketRepo.add(rocketDoubleDaemon.clearAndInterrupt()))
                        .setAnimateBulletSideQuest(renderer)
                        .setClosure(new MultiViewAnimateClosure()::onReturn);

                rocketRepo.getStructure().push(rocketDoubleDaemon);
            }

            //init enemy missiles and fill the repo
            for (int i = 0; i < maxRockets; ++i) {

                String rocketName = "Missile instance no. " + i;

                BulletDoubleDaemon missileDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        new Bullet(
                                enemyMissileSprite,
                                0,
                                Pair.create((float) 0, (float) 0),
                                bulletDamage,
                                enemyMissileSprite[0].getWidth(),
                                dXY
                        ).setView(scene.addImageView(new ImageViewImpl(rocketName + " View 1").setImage(enemyMissileSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(5)))
                                .setView2(scene.addImageView(new ImageViewImpl(rocketName + " View 2").setImage(enemyMissileSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(5)))
                                .setView3(scene.addImageView(new ImageViewImpl(rocketName + " View 3").setImage(enemyMissileSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(5)))
                ).setName(rocketName).setUncaughtExceptionHandler(uncaughtExceptionHandler);

                missileDoubleDaemon.getPrototype().setBorders(0, borderX, 0, borderY);

                missileDoubleDaemon.setOutOfBordersConsumer(gameConsumer)
                        .setOutOfBordersClosure(()-> enemyMissileRepo.add(missileDoubleDaemon.clearAndInterrupt()))
                        .setAnimateBulletSideQuest(renderer)
                        .setClosure(new MultiViewAnimateClosure()::onReturn);

                enemyMissileRepo.getStructure().push(missileDoubleDaemon);
            }

            rocketExplosionRange = fieldImage.getWidth();

            scene.addImageViews(laserViews);

            //laser init
            laser = new LaserBulletDaemon(
                    gameConsumer,
                    new LaserBullet(laserSprite, 40, Pair.create(0F, 0F), bulletDamage, dXY)
            ).setViews(laserViews).setUncaughtExceptionHandler(uncaughtExceptionHandler);

            laser.setAnimateLaserSideQuest(renderer).setClosure(ret->{
                for (Pair<ImageView, ImageMover.PositionedImage> viewAndImage : ret.runtimeCheckAndGet())
                    viewAndImage.getFirst()
                            .setAbsoluteX(viewAndImage.getSecond().positionX)
                            .setAbsoluteY(viewAndImage.getSecond().positionY)
                            .setImage(viewAndImage.getSecond().image);
            });

            //init moneyDaemon
            moneyDaemon = new MoneyHandlerDaemon(
                    gameConsumer,
                    (MoneyHandler) new MoneyHandler(moneyNumbersImages, dollarSign, dXY).setBorders(0, borderX, 0, borderY)
            ).setName("Money handler Daemon")
                    .setUncaughtExceptionHandler(uncaughtExceptionHandler)
                    .setCoordinates(scoreTitleView.getAbsoluteX(), scoreTitleView.getAbsoluteY())
                    .setAmount(0)
                    .setOutOfBordersConsumer(gameConsumer)
                    .setOutOfBordersClosure(()->{
                        moneyDaemon.clearAndInterrupt();
                        renderer.consume(()->{
                            moneyView.getFirst().hide();
                            moneyView.getSecond().hide();
                        });
                    });

            moneyDaemon.setAnimateMoneySideQuest(renderer).setClosure(ret ->{
                Pair<ImageMover.PositionedImage, ImageMover.PositionedImage> result = ret.runtimeCheckAndGet();
                moneyView.getFirst().setImage(result.getFirst().image).setAbsoluteX(result.getFirst().positionX).setAbsoluteY(result.getFirst().positionY);
                moneyView.getSecond().setImage(result.getSecond().image).setAbsoluteX(result.getSecond().positionX).setAbsoluteY(result.getSecond().positionY);
            });

            //prepare the scene and start the renderer
            scene.lockViews();

            System.out.println(DaemonUtils.tag() + "Scene size: " + scene.getViews().size());

            scene.forEach(view -> {
                System.out.println(DaemonUtils.tag() + view.getName());
                System.out.println(DaemonUtils.tag() + "X: " + view.getAbsoluteX());
                System.out.println(DaemonUtils.tag() + "Y: " + view.getAbsoluteY());
                System.out.println(DaemonUtils.tag() + "Image: "  + view.getImage().toString());
                System.out.println(DaemonUtils.tag() + "Image imp: " + view.getImage().getImageImp().toString());
                System.out.println(DaemonUtils.tag() + "Z Index: " + view.getZindex());
            });

            activeSoundManager.start();
            renderer.setScene(scene).start();
            gameConsumer.consume(stateChain::next);

        }).addState(()->{//gameState

            //onPause
            this.onPauseRunnable = () -> {
                gameConsumer.consume(()->{
                    towerSpriteUpgrader.stop();
                    fieldEraserEngine.stop();
                    enemyGenerator.stop();
                    for (EnemyDoubleDaemon enemy : activeEnemies)
                        enemy.pause();
                    for (TowerDaemon tower : towers)
                        tower.pause();
                    for(BulletDoubleDaemon rocket : rocketRepo.activeProjectiles)
                        rocket.pause();
                    for(BulletDoubleDaemon bullet : bulletRepo.activeProjectiles)
                        bullet.pause();
                    for(BulletDoubleDaemon missile : enemyMissileRepo.activeProjectiles)
                        missile.pause();
                    renderer.stop();
                    paused = true;
                });
            };

            //init controller
            touchController = (x, y) -> {
                gameConsumer.consume(()-> {
                    if (soundButton.checkCoordinates(x, y)) {

                    } else if (towerUpgradeDialogue.getTowerUpgrade().isShowing())
                        towerUpgradeDialogue.getTowerUpgrade().checkCoordinates(x, y);
                    else {

                        if (selectTowerDialogue.getSelectTowerDialogue().isShowing())
                            selectTowerDialogue.getSelectTowerDialogue().checkCoordinates(x, y);

                        if (towerSelect == null)
                            System.out.println("Select" + "please select tower");
                        else
                            manageTower(x, y);
                    }
                });
            };

            //laser start
            for (ImageView laserView : laserViews)
                laserView.setAbsoluteX(-100).setAbsoluteY(-100);

            enemyParalyizer = new EagerMainQuestDaemonEngine(gameConsumer).setName("Enemy Paralyzer").start();

            laser.start();

            //money handler start
            moneyDaemon.start();

            //draw the score keeping dialogue
            renderer.consume(()->
                    infoScore = new InfoTable(
                            borderX * 85 / 100,
                            borderY * 15 / 100,
                            scoreBackGrView,
                            scoreTitleView,
                            viewsNum,
                            scorenumbersImages
                    ).setNumbers(80085)
            );

            //set tower range
            range = 2 * fieldImage.getWidth();

            //get grids first field
            Field firstField = grid.getField(0, 0);

            //init enemy generator
            enemyGenerator = DummyDaemon.create(gameConsumer, enemyGenerationRegulator).setClosure(()->{

                enemyCounter++;

                //every... enemies increase the pain!!!!
                if (enemyCounter % 3 == 0) {
                    if(enemyVelocity < 6)
                        enemyVelocity += 1;
                    enemyHp++;
                }

                if (enemyCounter % 20 == 0 && bulletDamage < 10)
                    bulletDamage += 1;

                EnemyDoubleDaemon enemyDoubleDaemon = enemyRepo.getAndConfigure(enemy->enemy.setMaxHp(enemyHp).setHp(enemyHp));

                System.err.println(DaemonUtils.timedTag() + enemyDoubleDaemon.getName() + ", STATES: " + enemyDoubleDaemon.getEnginesState().toString());
                System.out.println(DaemonUtils.tag() + "Enemy counter: " + enemyCounter);
                System.out.println(DaemonUtils.tag() + "Enemy repo size: " + enemyRepo.size());
                System.out.println(DaemonUtils.tag() + "Enemy state: " + enemyDoubleDaemon.getEnginesState().get(enemyDoubleDaemon.getEnginesState().size() - 1));

                int firstAngle = (int) RotatingSpriteImageMover.getAngle(
                        enemyDoubleDaemon.getLastCoordinates().getFirst(),
                        enemyDoubleDaemon.getLastCoordinates().getSecond(),
                        firstField.getCenterX(),
                        firstField.getCenterY()
                );

                enemyDoubleDaemon.setTarget(null).rotate(firstAngle).reload(new Closure<Target>() {
                    @Override
                    public void onReturn(Return<Target> ret) {

                        renderer.consume(enemyDoubleDaemon.getTargetView()::hide);

                        TowerDaemon target = (TowerDaemon) ret.runtimeCheckAndGet();

                        if (target.isShootable()
                            && Math.abs(target.getLastCoordinates().getFirst() - enemyDoubleDaemon.getLastCoordinates().getFirst()) < range
                            && Math.abs(target.getLastCoordinates().getSecond() - enemyDoubleDaemon.getLastCoordinates().getSecond()) < range) {

                            if (enemyDoubleDaemon.isShootable())
                                renderer.consume(() ->
                                        enemyDoubleDaemon.getTargetView()
                                                .setAbsoluteX(target.getLastCoordinates().getFirst())
                                                .setAbsoluteY(target.getLastCoordinates().getSecond())
                                                .show()
                                );

                            fireRocketBullet(
                                    enemyDoubleDaemon.getLastCoordinates(),
                                    target,
                                    enemyMissileRepo,
                                    15,
                                    target.getTowerLevel().bulletDamage,
                                    2,
                                    ()->{

                                        if(!target.isShootable())
                                            return;

                                        renderer.consume(target.getHpView()::hide);

                                        enemyDoubleDaemon.setTarget(null);

                                        Field<TowerDaemon> field = grid.getField(
                                                target.getLastCoordinates().getFirst(),
                                                target.getLastCoordinates().getSecond()
                                        );

                                        currentSoundManager.playSound(bigExplosion);

                                        target.clearAndInterrupt().pushSprite(explodeSprite, 0, () -> {

                                            target.setShootable(false);
                                            renderer.consume(target.getView()::hide);
                                            towers.remove(target);
                                            field.setObject(null);

                                            grid.destroyTower(field.getRow(), field.getColumn());
                                            renderer.consume(() -> gridViewMatrix[field.getRow()][field.getColumn()].setImage(fieldImage).hide());
                                        }).queueStop();
                                    }
                            );
                        }

                        enemyDoubleDaemon.reload(this::onReturn);
                    }
                }).goTo(
                        firstField.getCenterX(),
                        firstField.getCenterY(),
                        enemyVelocity,
                        new Closure<Boolean>() {
                            @Override
                            public void onReturn(Return<Boolean> ret) {

                                if (!ret.runtimeCheckAndGet()){
                                    enemyRepo.add(enemyDoubleDaemon);
                                    return;
                                }

                                Pair<Float, Float> currentCoord = enemyDoubleDaemon.getPrototype().getLastCoordinates();
                                Field<TowerDaemon> current = grid.getField(currentCoord.getFirst(), currentCoord.getSecond());

                                for(Field<TowerDaemon> neighbour : grid.getNeighbors(current)) {
                                    TowerDaemon neighbourTower = neighbour.getObject();
                                    if (neighbourTower != null) {
                                        neighbourTower.addTarget(enemyDoubleDaemon);//TODO check ret val
                                        enemyDoubleDaemon.setTarget(neighbourTower.isShootable() ? neighbourTower : null);
                                    }
                                }

                                ImageView currentFieldView = gridViewMatrix[current.getRow()][current.getColumn()];

                                //show enemy progress on grid
                                Pair<Integer, Integer> previousFieldRowColumn = enemyDoubleDaemon.getPreviousField();

                                if (previousFieldRowColumn != null) {

                                    int previousRow = previousFieldRowColumn.getFirst();
                                    int previousColumn = previousFieldRowColumn.getSecond();

                                    int currentRow = current.getRow();
                                    int currentColumn = current.getColumn();

                                    if ((currentRow != previousRow) && (currentColumn != previousColumn))
                                        renderer.consume(diagonalMatrix[Math.min(currentRow, previousRow)][Math.min(currentColumn, previousColumn)]::show);

                                }

                                renderer.consume(currentFieldView::show);

                                enemyDoubleDaemon.setPreviousField(Pair.create(current.getRow(), current.getColumn()));

                                //if enemy reaches last field
                                if (current.getColumn() == columns - 1 && current.getRow() == rows - 1) {
                                    if (score > 0)
                                        renderer.consume(()-> infoScore.setNumbers(--score));
                                    currentSoundManager.playSound(bigExplosion);
                                    renderer.consume(currentFieldView.setImage(fieldImageTowerDen)::show);
                                    enemyRepo.add(enemyDoubleDaemon);
                                    return;
                                }

                                //go to next fields center
                                Field next = grid.getMinWeightOfNeighbors(current);
                                int angle = (int) RotatingSpriteImageMover.getAngle(
                                        current.getCenterX(),
                                        current.getCenterY(),
                                        next.getCenterX(),
                                        next.getCenterY()
                                );

                                enemyDoubleDaemon.rotate(angle).goTo(
                                        next.getCenterX(),
                                        next.getCenterY(),
                                        enemyDoubleDaemon.getVelocity().intensity,
                                        this::onReturn
                                );
                            }
                        }
                );
            });

            //start enemy generator
            enemyGenerator.setName("Enemy Generator").start();

            //marking start and end field
            ImageView firstFieldView = gridViewMatrix[0][0];
            ImageView lastFieldView = gridViewMatrix[rows - 1][columns - 1];

            new MainQuestDaemonEngine(renderer).daemonize(() -> {

                int cnt = 0;

                while (cnt < 6) {
                    if (firstFieldView.isShowing())
                        renderer.consume(firstFieldView::hide);
                    else
                        renderer.consume(firstFieldView::show);

                    cnt++;

                    Thread.sleep(300);
                }

                while (cnt < 12) {
                    if (lastFieldView.isShowing())
                        renderer.consume(lastFieldView::hide);
                    else
                        renderer.consume(lastFieldView.setImage(fieldImageTowerDen)::show);

                    cnt++;

                    Thread.sleep(300);
                }

            }).setName("Start End field marker").start();

            System.out.println(DaemonUtils.tag() + "DXY: " + dXY);

            towerSpriteUpgrader = new EagerMainQuestDaemonEngine(renderer).setName("Tower Sprite Upgrader").setUncaughtExceptionHandler(uncaughtExceptionHandler).start();
            fieldEraserEngine = new EagerMainQuestDaemonEngine(renderer).setName("Field Eraser").setUncaughtExceptionHandler(uncaughtExceptionHandler).start();

            renderer.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            gameConsumer.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        });
    }

    private void upgradeTower(TowerDaemon tow) {

        Tower.TowerLevel currLvl = tow.getTowerLevel();

        if (!towerUpgradeDialogue.getTowerUpgrade().isShowing() && currLvl.currentLevel > 0) {//if upgrade dialog not shown

            towerUpgradeDialogue.setTower(tow);

            boolean hasSkillsToPayTheBills = score > 3;

            switch (tow.getTowertype()) {
                case TYPE1:
                    dialogueImageTowerUpgrade = redTowerUpgSprite;
                    break;
                case TYPE2:
                    dialogueImageTowerUpgrade = blueTowerUpgSprite;
                    break;
                case TYPE3:
                    dialogueImageTowerUpgrade = greenTowerUpgSprite;
                    break;
            }

            //show upgrade dialog
            renderer.consume(()->{

                towerUpgradeDialogue.getTowerUpgrade().setAbsoluteX(borderX / 2).setAbsoluteY(borderY / 2);

                towerUpgradeDialogue.getTowerUpgrade()
                        .getViewByName("TowerView")
                        .setImage(dialogueImageTowerUpgrade[currLvl.currentLevel - 1]);

                towerUpgradeDialogue.getTowerUpgrade().show();

                if (hasSkillsToPayTheBills && tow.getTowerLevel().currentLevel < 3)
                    towerUpgradeDialogue.getTowerUpgrade().getViewByName("Upgrade").show();
                else
                    towerUpgradeDialogue.getTowerUpgrade().getViewByName("Upgrade").hide();
            });
        }
    }

    private boolean deny;

    private void setNewTower(Field<TowerDaemon> field) {

        ImageView fieldView = gridViewMatrix[field.getRow()][field.getColumn()];

        //check if selected field is on the last remaining path
        if (!grid.setTower(field.getRow(), field.getColumn())){

            if (!deny) {

                deny = true;

                boolean isGeenFieldShown = fieldView.isShowing() && fieldView.getImage().equals(fieldImage);
                renderer.consume(fieldView.setImage(fieldImageTowerDen)::show);

                SideQuestDaemonEngine denyMarker = new SideQuestDaemonEngine().setName("Denied Marker");
                denyMarker.start().setSideQuest(renderer, new InterruptibleSleepSideQuest<Boolean>() {

                    private int cnt = 0;

                    @Override
                    public Boolean pursue() throws Exception {
                        if (cnt++ <= 6) {
                            if (cnt % 2 == 0)
                                return false;
                            else
                                return true;
                        } else
                            throw new InterruptedException();
                    }

                }).setSleepInterval(300).setClosure(showDeniedMarker -> {
                    if (showDeniedMarker.runtimeCheckAndGet())
                        fieldView.show();
                    else
                        fieldView.hide();
                }).onInterrupt(renderer, () -> {
                    denyMarker.stop();

                    deny = false;
                    fieldView.setImage(fieldImage);

                    if (isGeenFieldShown)
                        fieldView.show();
                    else
                        fieldView.hide();
                });
            }
        } else {

            {
                renderer.consume(fieldView.setImage(currentTowerSprite[0])::show);

                //hide diagonal views
                int fieldRow = field.getRow();
                int fieldColumn = field.getColumn();

                System.out.println(DaemonUtils.tag() + "FIELD ROW: " + fieldRow + ", FIELD COLUMN: " + fieldColumn);

                //upper left diagonal
                if (fieldRow - 1 >= 0 && fieldColumn - 1 >= 0)
                    renderer.consume(diagonalMatrix[fieldRow - 1][fieldColumn - 1]::hide);

                //upper right diagonal
                if (fieldRow - 1 >= 0 && fieldColumn < columns - 1)
                    renderer.consume(diagonalMatrix[fieldRow - 1][fieldColumn]::hide);

                //down left diagonal
                if (fieldRow < rows - 1 && fieldColumn - 1 >= 0)
                    renderer.consume(diagonalMatrix[fieldRow][fieldColumn - 1]::hide);

                //down right diagonal
                if (fieldRow < rows - 1 && fieldColumn < columns - 1)
                    renderer.consume(diagonalMatrix[fieldRow][fieldColumn]::hide);
            }

            String towerName = "Tower[" + field.getColumn() + "][" + field.getRow() + "]";

            towerHpViwes[field.getRow()][field.getColumn()].setImage(healthBarSprite[9]);

            currentSoundManager.playSound(towerConstructionSound);

            Tower towerPrototype = towerSelect == Tower.TowerType.TYPE3

                    ?

                    new LaserTower (
                            renderer,
                            new MultiViewAnimateClosure(),
                            currentTowerSprite,
                            healthBarSprite,
                            Pair.create(field.getCenterX(), field.getCenterY()),
                            range,
                            towerSelect,
                            dXY,
                            towerHp
                    ).setHpView(towerHpViwes[field.getRow()][field.getColumn()].setAbsoluteX(field.getCenterX()).setAbsoluteY(field.getCenterY() - 2 * healthBarSprite[9].getHeight()).show())
                            .setHealthBarImage(healthBarSprite)

                    :

                    new Tower(
                            currentTowerSprite,
                            healthBarSprite,
                            Pair.create(field.getCenterX(), field.getCenterY()),
                            range,
                            towerSelect,
                            dXY,
                            towerHp
                    ).setHpView(towerHpViwes[field.getRow()][field.getColumn()].setAbsoluteX(field.getCenterX()).setAbsoluteY(field.getCenterY() - 2 * healthBarSprite[9].getHeight()).show());

            TowerDaemon towerDaemon = new TowerDaemon(gameConsumer, towerPrototype)
                    .setName(towerName)
                    .setView(fieldView)
                    .setShootable(true)
                    .setUncaughtExceptionHandler(uncaughtExceptionHandler);

            towers.add(towerDaemon);
            field.setObject(towerDaemon);

            towerDaemon.start().setInitTowerSideQuest(renderer).setClosure(new MultiViewAnimateClosure()::onReturn).onInterrupt(gameConsumer, () -> {

                towerDaemon.setAnimateTowerSideQuest(renderer).setClosure(new MultiViewAnimateClosure()::onReturn);

                towerDaemon.scan(new Closure<Pair<Tower.TowerType, Target>>() {
                    @Override
                    public void onReturn(Return<Pair<Tower.TowerType, Target>> towerTypeAndEnemy) {

                        long reloadInterval = towerDaemon.getTowerLevel().reloadInterval;

                        if (towerTypeAndEnemy.runtimeCheckAndGet().getFirst() != null
                                && towerTypeAndEnemy.runtimeCheckAndGet().getSecond() != null) {

                            Tower.TowerType towerType = towerTypeAndEnemy.get().getFirst();
                            EnemyDoubleDaemon enemy = (EnemyDoubleDaemon) towerTypeAndEnemy.get().getSecond();

                            BareClosure<Integer> bulletClosure = amount -> {
                                currentSoundManager.playSound(bigExplosion);
                                enemyRepo.add(enemy);
                                moneyDaemon.clearAndInterrupt().setAmount(amount)
                                        .setCoordinates(enemy.getLastCoordinates().getFirst(), enemy.getLastCoordinates().getSecond())
                                        .goTo(scoreTitleView.getAbsoluteX(), scoreTitleView.getAbsoluteY(), 13, moneyGoToClosure::onReturn);

                                renderer.consume(()->infoScore.setNumbers(score += amount));

                                renderer.consume(()->{
                                    moneyView.getFirst().show();
                                    moneyView.getSecond().show();
                                });
                            };

                            switch (towerType) {
                                case TYPE1:
                                    fireBullet(
                                            towerDaemon.getLastCoordinates(),
                                            enemy,
                                            25,
                                            towerDaemon.getTowerLevel().bulletDamage,
                                            towerDaemon.getTowerLevel().currentLevel,
                                            false,
                                            0,
                                            bulletSprite,
                                            miniExplodeSprite,
                                            ()-> bulletClosure.onReturn(3)
                                    );
                                    break;
                                case TYPE2:
                                    fireRocketBullet(
                                            towerDaemon.getLastCoordinates(),
                                            enemy,
                                            rocketRepo,
                                            18,
                                            towerDaemon.getTowerLevel().bulletDamage,
                                            towerDaemon.getTowerLevel().currentLevel,
                                            () -> bulletClosure.onReturn(5)
                                    );
                                    break;
                                case TYPE3:
                                    double angle = RotatingSpriteImageMover.getAngle(
                                            towerDaemon.getLastCoordinates().getFirst(),
                                            towerDaemon.getLastCoordinates().getSecond(),
                                            enemy.getLastCoordinates().getFirst(),
                                            enemy.getLastCoordinates().getSecond()
                                    );

                                    int lvl = towerDaemon.getTowerLevel().currentLevel;

                                    float velocity = lvl == 1 ? enemy.getVelocity().intensity / 2 : lvl == 2 ? enemy.getVelocity().intensity / 4 : 0;
                                    long duration = lvl == 1 ? 200 : lvl == 2 ? 400 : 600;

                                    fireLaser(towerDaemon.getLastCoordinates(), enemy, velocity, duration, () -> bulletClosure.onReturn(1));

                                    reloadInterval = 4000;
                                    break;
                                default:
                                    throw new IllegalStateException("Tower type does not exist!");
                            }
                        }

                        towerDaemon.reload(reloadInterval, () -> towerDaemon.scan(this::onReturn));
                    }
                });
            });
        }
    }

    private void manageTower(float x, float y) {

        //check if correct field
        Field<TowerDaemon> field = grid.getField(x, y);
        if (field == null) return;

        TowerDaemon tow = field.getObject();

        if (tow != null ) //upgrade existing tower
            upgradeTower(tow);
        else  //init and set new tower
            setNewTower(field);
    }

    private void fireBullet(
            Pair<Float, Float> sourceCoord,
            Target target,
            float velocity,
            int bulletDamage,
            int noOfBulletsFired,
            boolean rotate,
            int bulletRotateAngle,
            Image[] bulletSprite,
            Image[] bulletExplodeSprite,
            Runnable destructionClosure
    ) {
        System.out.println(DaemonUtils.tag() +  bulletRepo.getName() + " size: " + bulletRepo.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletRepo.configureAndGet(bullet -> {
            bullet.setCoordinates(sourceCoord.getFirst(), sourceCoord.getSecond())
                    .setLevel(noOfBulletsFired)
                    .setDamage(bulletDamage)
                    .start();
        });

        float targetX = target.getLastCoordinates().getFirst();
        float targetY = target.getLastCoordinates().getSecond();

        if (rotate)
            bulletDoubleDaemon.rotate(bulletRotateAngle, ()->{});

        bulletDoubleDaemon.goTo(targetX, targetY, velocity, ret -> {

            if (!ret.runtimeCheckAndGet() || !target.isShootable()) {
                bulletRepo.add(bulletDoubleDaemon);
                return;
            }

            int newHp = target.getHp() - bulletDoubleDaemon.getDamage();
            if (newHp > 0)
                target.setHp(newHp);
            else
                destructionClosure.run();

            currentSoundManager.playSound(bulletSound);
            bulletDoubleDaemon.pushSprite(bulletExplodeSprite, 0, ()->bulletRepo.add(bulletDoubleDaemon));
        });
    }

    private void fireRocketBullet(
            Pair<Float, Float> sourceCoord,
            Target target,
            StackedEntityRepo<BulletDoubleDaemon> repo,
            float velocity,
            int bulletDamage,
            int noOfBulletsFired,
            Runnable destructionClosure
    ) {
        System.out.println(DaemonUtils.tag() + repo.getName() + " size: " + repo.size());

        BulletDoubleDaemon rocketDoubleDaemon = repo.configureAndGet(rocket -> {
            rocket.setCoordinates(sourceCoord.getFirst(), sourceCoord.getSecond())
                    .setLevel(noOfBulletsFired)
                    .setDamage(bulletDamage)
                    .start();
        });

        int launchX = getRandomInt(
                (int)(sourceCoord.getFirst() - fieldImage.getWidth() / 2),
                (int)(sourceCoord.getFirst() + fieldImage.getWidth() / 2)
        );

        int launchY = getRandomInt(
                (int)(sourceCoord.getSecond() - fieldImage.getWidth() / 2),
                (int)(sourceCoord.getSecond() + fieldImage.getWidth() / 2)
        );

        int angle = (int) RotatingSpriteImageMover.getAngle(
                sourceCoord.getFirst(),
                sourceCoord.getSecond(),
                launchX,
                launchY
        );

        rocketDoubleDaemon.rotateAndGoTo(angle, launchX, launchY, 4, ret -> {

            if (!ret.runtimeCheckAndGet()) {
                repo.add(rocketDoubleDaemon);
                return;
            }

            if (!target.isShootable()) {
                rocketDoubleDaemon.rotateAndGoTo(
                        - angle,
                        sourceCoord.getFirst(),
                        sourceCoord.getSecond(),
                        4,
                        ret1 -> repo.add(rocketDoubleDaemon)
                );
            } else {

                Pair<Float, Float> targetCoord = target.getLastCoordinates();

                int targetAngle1 = (int) RotatingSpriteImageMover.getAngle(
                        rocketDoubleDaemon.getLastCoordinates().getFirst(),
                        rocketDoubleDaemon.getLastCoordinates().getSecond(),
                        target.getLastCoordinates().getFirst(),
                        target.getLastCoordinates().getSecond()
                );

                rocketDoubleDaemon.rotateAndGoTo(
                        targetAngle1,
                        target.getLastCoordinates().getFirst(),
                        target.getLastCoordinates().getSecond(),
                        velocity,
                        ret2 -> {

                            if (!ret2.runtimeCheckAndGet() || !target.isShootable()) {
                                repo.add(rocketDoubleDaemon);
                                return;
                            }

                            float bulletX = rocketDoubleDaemon.getLastCoordinates().getFirst();
                            float bulletY = rocketDoubleDaemon.getLastCoordinates().getSecond();

                            if (Math.abs(bulletX - target.getLastCoordinates().getFirst()) < rocketExplosionRange
                                    && Math.abs(bulletY - target.getLastCoordinates().getSecond()) < rocketExplosionRange) {

                                int newHp = target.getHp() - rocketDoubleDaemon.getDamage();

                                if (newHp > 0)
                                    target.setHp(newHp);
                                else
                                    destructionClosure.run();
                            }

                            currentSoundManager.playSound(rocketExplosionSound);
                            rocketDoubleDaemon.pushSprite(rocketExplodeSprite, 0, () -> repo.add(rocketDoubleDaemon));
                        }
                );
            }
        });
    }

    public void fireLaser(
            Pair<Float, Float> source,
            Target target,
            float velocity,
            long duration,
            Runnable destructionClosure
    ) {

        if (!target.isShootable() || target.isParalyzed())
            return;

        target.setParalyzed(true);
        EnemyDoubleDaemon enemyTarget = ((EnemyDoubleDaemon) target);

        currentSoundManager.playSound(laserSound);

        Runnable paralyzerClosure = () -> {

            enemyTarget.setParalyzed(false);

            renderer.consume(() -> enemyTarget.getParalyzedView().hide());

            if (target.isShootable()) {

                target.setVelocity(enemyVelocity);

                Pair<Integer, Integer> prevFieldCoord = enemyTarget.getPreviousField();

                Field<TowerDaemon> prevField = (prevFieldCoord == null) ?  grid.getField(0, 0) : grid.getField(prevFieldCoord.getFirst(), prevFieldCoord.getSecond());

                //go to next fields center
                Field next = grid.getMinWeightOfNeighbors(prevField);

                int angle = (int) RotatingSpriteImageMover.getAngle(
                        target.getLastCoordinates().getFirst(),
                        target.getLastCoordinates().getSecond(),
                        next.getCenterX(),
                        next.getCenterY()
                );

                enemyTarget.rotate(angle).redirect(next.getCenterX(), next.getCenterY());
            }
        };

        laser.desintegrateTarget(source, target, duration, renderer, ret -> {

            int newHp = enemyTarget.getHp() - laser.getDamage();
            if (newHp > 0) {

                enemyTarget.setHp(newHp);
                enemyTarget.setVelocity(velocity);

                if (enemyParalyizer.queueSize() == 0) {
                    renderer.consume(() -> enemyTarget.getParalyzedView().show());
                    enemyParalyizer.daemonize(() -> Thread.sleep(enemyParalyzingInterval), paralyzerClosure);
                } else {
                    renderer.consume(() -> enemyTarget.getParalyzedView().show());
                    new MainQuestDaemonEngine(gameConsumer).setName("Helper Paralyzer").start().daemonize(()->{
                        System.err.println(DaemonUtils.timedTag() + "Enemy paralyzer busy. Spawning a new paralyzer engine.");
                        Thread.sleep(enemyParalyzingInterval);
                    }, paralyzerClosure);
                }

            } else
                destructionClosure.run();
        });
    }

    private Closure<Boolean> moneyGoToClosure = moneyGoToRet -> {

        moneyGoToRet.runtimeCheckAndGet();

        renderer.consume(()->{
            moneyView.getFirst().hide();
            moneyView.getSecond().hide();
        });

        //renderer.consume(()->infoScore.setNumbers(++score));
    };
}