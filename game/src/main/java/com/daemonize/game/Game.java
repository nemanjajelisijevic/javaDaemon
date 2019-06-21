package com.daemonize.game;


import com.daemonize.daemonengine.DaemonEngine;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;

import com.daemonize.game.images.Image;

import com.daemonize.game.images.imageloader.ImageLoader;
import com.daemonize.game.renderer.Renderer2D;
import com.daemonize.game.repo.QueuedEntityRepo;
import com.daemonize.game.repo.StackedEntityRepo;
import com.daemonize.game.scene.Scene2D;

import com.daemonize.game.tabel.Field;
import com.daemonize.game.tabel.Grid;

import com.daemonize.game.scene.views.Button;
import com.daemonize.game.scene.views.CompositeImageViewImpl;
import com.daemonize.game.scene.views.ImageView;
import com.daemonize.game.scene.views.ImageViewImpl;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    //running flag
    private volatile boolean running;

    //pause flag
    private volatile boolean paused;

    //game consumer threads
    private Renderer2D renderer;
    private DaemonConsumer gameConsumer;

    //image loader
    private ImageLoader imageLoader;

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
    private Grid grid;
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
    private int towerHp = 100;

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

    //towers dialogue
    private TowerUpgradeDialog towerUpgradeDialogue;
    private TowerSelectDialogue selectTowerDialogue;

    private Image selection;
    private Image deselection;

    //enemies
    private Image[] enemySprite;
    private Image[] healthBarSprite;

    private DummyDaemon enemyGenerator;
    private long enemyCounter = 0;
    private float enemyVelocity = 1;
    private int enemyHp = 500;
    private long enemyGenerateinterval = 5000;
    private long waveInterval = 15000;

    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();

    private int maxEnemies = 100;
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
    private StackedEntityRepo<BulletDoubleDaemon> bulletRepo;
    private Set<BulletDoubleDaemon> activeBullets = new HashSet<>();

    private int maxRockets = 150;
    private StackedEntityRepo<BulletDoubleDaemon> rocketRepo;

    private Set<BulletDoubleDaemon> activeRockets = new HashSet<>();

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
            view.setAbsoluteX(posBmp.positionX);
            view.setAbsoluteY(posBmp.positionY);
            view.setImage(posBmp.image);
        }
    }

    private static class MultiViewAnimateClosure implements Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
        @Override
        public void onReturn(Return<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> aReturn) {
            GenericNode.forEach(aReturn.runtimeCheckAndGet(), arg -> {
                arg.getSecond().setAbsoluteX(arg.getFirst().positionX);
                arg.getSecond().setAbsoluteY(arg.getFirst().positionY);
                arg.getSecond().setImage(arg.getFirst().image);
            });
        }
    }

    //construct
    public Game(
            Renderer2D renderer,
            ImageLoader imageLoader,
            int borderX,
            int borderY,
            int rows,
            int columns,
            float gridX,
            float gridY
    ) {
        this.renderer = renderer;
        this.imageLoader = imageLoader;

        this.borderX = borderX;
        this.borderY = borderY;

        this.scene = new Scene2D();
        this.gameConsumer = new DaemonConsumer("Game Consumer");
        this.rows = rows;
        this.columns = columns;
        this.grid = new Grid(
                rows,
                columns,
                Pair.create(0, 0),
                Pair.create(rows - 1, columns - 1),
                gridX,
                gridY,
                ((borderX * 70) / 100) / columns
        );

        this.dXY = ((float) borderX) / 1000;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        gameConsumer.consume(()->{
            towerSpriteUpgrader.stop();
            fieldEraserEngine.stop();
            enemyGenerator.stop();
            for (EnemyDoubleDaemon enemy : activeEnemies)
                enemy.pause();
            for (TowerDaemon tower : towers)
                tower.pause();
            for(BulletDoubleDaemon rocket : activeRockets)
                rocket.pause();
            for(BulletDoubleDaemon bullet : activeBullets)
                bullet.pause();
            renderer.stop();
            paused = true;
        });

    }

    public void cont() { //continueAll
        gameConsumer.consume(()->{
            enemyGenerator.start();
            for (EnemyDoubleDaemon enemy : activeEnemies)
                enemy.cont();
            for (TowerDaemon tower : towers)
                tower.cont();
            for(BulletDoubleDaemon rocket : activeRockets)
                rocket.cont();
            for(BulletDoubleDaemon bullet : activeBullets)
                bullet.cont();
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
    public Game onTouch(float x, float y) {
        gameConsumer.consume(()-> {
            if (towerUpgradeDialogue.getTowerUpgrade().isShowing())
                towerUpgradeDialogue.getTowerUpgrade().checkCoordinates(x, y);
            else {

                if (selectTowerDialogue.getSelectTowerDialogue().isShowing())
                    selectTowerDialogue.getSelectTowerDialogue().checkCoordinates(x, y);

                if (towerSelect == null)
                    System.out.println("Select" + "please select tower");
                else
                    setTower(x, y);
            }
        });
        return this;
    }

    {
        //init state (loading sprites)
        stateChain.addState(()-> { //image loading State

            try {

                backgroundImage = imageLoader.loadImageFromAssets("maphi.jpg", borderX, borderY);

                //laser views init
                laserViews = new ArrayList<>(laserViewNo);

                Image[] loadingSprite = new Image[] {imageLoader.loadImageFromAssets("greenPhoton.png", borderX / 150, borderX / 150)};

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

                fieldImage = imageLoader.loadImageFromAssets("greenOctagon.png", width, height);
                fieldImageTower = imageLoader.loadImageFromAssets("blueOctagon.png", width, height);
                fieldImageTowerDen = imageLoader.loadImageFromAssets("redOctagon.png", width, height);

                fieldGreenDiagonal = imageLoader.loadImageFromAssets("greenDiagonal.png", width * 2 / 3, height * 2 / 3);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                int scoreWidth = borderX / 5;
                int selectionWidth = borderX / 10;

                int scoreHeight = borderY / 5;
                int selectionHeight = borderY / 2;

                upgradeButtonImage = imageLoader.loadImageFromAssets("ButtonUpgrade.png",borderX / 6,borderY / 10);
                upgradeButtonImagePressed = imageLoader.loadImageFromAssets("ButtonUpgradePressed.png",borderX / 6,borderY / 10);

                closeButtonImage = imageLoader.loadImageFromAssets("ButtonX.png",borderX / 20,borderY /  10);
                closeButtonImagePressed = imageLoader.loadImageFromAssets("ButtonXPressed.png",borderX / 20,borderY /  10);

                saleButtonImage = imageLoader.loadImageFromAssets("ButtonSale.png",borderX / 6,borderY / 10);
                saleButtonImagePressed = imageLoader.loadImageFromAssets("ButtonSalePressed.png",borderX / 6,borderY / 10);

                selection = imageLoader.loadImageFromAssets("greenOctagon.png", selectionWidth, selectionWidth);
                deselection = imageLoader.loadImageFromAssets("redOctagon.png", selectionWidth, selectionWidth);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                scoreBackGrImage = imageLoader.loadImageFromAssets("SmallBox.png", scoreWidth, scoreHeight);

                laserSprite = new Image[] {imageLoader.loadImageFromAssets("greenPhoton.png",  width / 10, width / 10)};

                //init enemy sprite
                enemySprite = new Image[36];

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                for (int i = 0; i < 36; i++)
                    enemySprite[i] = imageLoader.loadImageFromAssets("plane" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                //bullet sprite
                int bulletSize0 = width / 8;//20;
                bulletSprite = new Image[4];
                bulletSprite[0] = imageLoader.loadImageFromAssets("thebarnstarRed.png", bulletSize0, bulletSize0);
                bulletSprite[1] = imageLoader.loadImageFromAssets("thebarnstarRed90.png", bulletSize0, bulletSize0);
                bulletSprite[2] = imageLoader.loadImageFromAssets("thebarnstarRed180.png", bulletSize0, bulletSize0);
                bulletSprite[3] = imageLoader.loadImageFromAssets("thebarnstarRed270.png", bulletSize0, bulletSize0);

                int bulletSize = width / 3;//60;
                bulletSpriteRocket = new Image[36];

                for (int i = 0; i < 36; i++)
                    bulletSpriteRocket[i] = imageLoader.loadImageFromAssets("rocket" + i + "0.png", bulletSize, bulletSize);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                //explosion sprite
                explodeSprite = new Image[33];
                explodeSprite[0] = imageLoader.loadImageFromAssets("Explosion1.png", width, height);
                explodeSprite[1] = imageLoader.loadImageFromAssets("Explosion2.png", width, height);
                explodeSprite[2] = imageLoader.loadImageFromAssets("Explosion3.png", width, height);
                explodeSprite[3] = imageLoader.loadImageFromAssets("Explosion4.png", width, height);
                explodeSprite[4] = imageLoader.loadImageFromAssets("Explosion5.png", width, height);
                explodeSprite[5] = imageLoader.loadImageFromAssets("Explosion6.png", width, height);
                explodeSprite[6] = imageLoader.loadImageFromAssets("Explosion7.png", width, height);
                explodeSprite[7] = imageLoader.loadImageFromAssets("Explosion8.png", width, height);
                explodeSprite[8] = imageLoader.loadImageFromAssets("Explosion9.png", width, height);
                explodeSprite[9] = imageLoader.loadImageFromAssets("Explosion10.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                explodeSprite[10] = imageLoader.loadImageFromAssets("Explosion11.png", width, height);
                explodeSprite[11] = imageLoader.loadImageFromAssets("Explosion12.png", width, height);
                explodeSprite[12] = imageLoader.loadImageFromAssets("Explosion13.png", width, height);
                explodeSprite[13] = imageLoader.loadImageFromAssets("Explosion14.png", width, height);
                explodeSprite[14] = imageLoader.loadImageFromAssets("Explosion15.png", width, height);
                explodeSprite[15] = imageLoader.loadImageFromAssets("Explosion16.png", width, height);
                explodeSprite[16] = imageLoader.loadImageFromAssets("Explosion17.png", width, height);
                explodeSprite[17] = imageLoader.loadImageFromAssets("Explosion18.png", width, height);
                explodeSprite[18] = imageLoader.loadImageFromAssets("Explosion19.png", width, height);
                explodeSprite[19] = imageLoader.loadImageFromAssets("Explosion20.png", width, height);

                explodeSprite[20] = imageLoader.loadImageFromAssets("Explosion21.png", width, height);
                explodeSprite[21] = imageLoader.loadImageFromAssets("Explosion22.png", width, height);
                explodeSprite[22] = imageLoader.loadImageFromAssets("Explosion23.png", width, height);
                explodeSprite[23] = imageLoader.loadImageFromAssets("Explosion24.png", width, height);
                explodeSprite[24] = imageLoader.loadImageFromAssets("Explosion25.png", width, height);
                explodeSprite[25] = imageLoader.loadImageFromAssets("Explosion26.png", width, height);
                explodeSprite[26] = imageLoader.loadImageFromAssets("Explosion27.png", width, height);
                explodeSprite[27] = imageLoader.loadImageFromAssets("Explosion28.png", width, height);
                explodeSprite[28] = imageLoader.loadImageFromAssets("Explosion29.png", width, height);
                explodeSprite[29] = imageLoader.loadImageFromAssets("Explosion30.png", width, height);

                explodeSprite[30] = imageLoader.loadImageFromAssets("Explosion31.png", width, height);
                explodeSprite[31] = imageLoader.loadImageFromAssets("Explosion32.png", width, height);
                explodeSprite[32] = imageLoader.loadImageFromAssets("Explosion33.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                int miniWidth = width / 3;
                int miniHeight = height / 3;

                rocketExplodeSprite = new Image[33];
                rocketExplodeSprite[0] = imageLoader.loadImageFromAssets("Explosion1.png", miniWidth, miniHeight);
                rocketExplodeSprite[1] = imageLoader.loadImageFromAssets("Explosion2.png", miniWidth, miniHeight);
                rocketExplodeSprite[2] = imageLoader.loadImageFromAssets("Explosion3.png", miniWidth, miniHeight);
                rocketExplodeSprite[3] = imageLoader.loadImageFromAssets("Explosion4.png", miniWidth, miniHeight);
                rocketExplodeSprite[4] = imageLoader.loadImageFromAssets("Explosion5.png", miniWidth, miniHeight);
                rocketExplodeSprite[5] = imageLoader.loadImageFromAssets("Explosion6.png", miniWidth, miniHeight);
                rocketExplodeSprite[6] = imageLoader.loadImageFromAssets("Explosion7.png", miniWidth, miniHeight);
                rocketExplodeSprite[7] = imageLoader.loadImageFromAssets("Explosion8.png", miniWidth, miniHeight);
                rocketExplodeSprite[8] = imageLoader.loadImageFromAssets("Explosion9.png", miniWidth, miniHeight);
                rocketExplodeSprite[9] = imageLoader.loadImageFromAssets("Explosion10.png", miniWidth, miniHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                rocketExplodeSprite[10] = imageLoader.loadImageFromAssets("Explosion11.png", miniWidth, miniHeight);
                rocketExplodeSprite[11] = imageLoader.loadImageFromAssets("Explosion12.png", miniWidth, miniHeight);
                rocketExplodeSprite[12] = imageLoader.loadImageFromAssets("Explosion13.png", miniWidth, miniHeight);
                rocketExplodeSprite[13] = imageLoader.loadImageFromAssets("Explosion14.png", miniWidth, miniHeight);
                rocketExplodeSprite[14] = imageLoader.loadImageFromAssets("Explosion15.png", miniWidth, miniHeight);
                rocketExplodeSprite[15] = imageLoader.loadImageFromAssets("Explosion16.png", miniWidth, miniHeight);
                rocketExplodeSprite[16] = imageLoader.loadImageFromAssets("Explosion17.png", miniWidth, miniHeight);
                rocketExplodeSprite[17] = imageLoader.loadImageFromAssets("Explosion18.png", miniWidth, miniHeight);
                rocketExplodeSprite[18] = imageLoader.loadImageFromAssets("Explosion19.png", miniWidth, miniHeight);
                rocketExplodeSprite[19] = imageLoader.loadImageFromAssets("Explosion20.png", miniWidth, miniHeight);

                rocketExplodeSprite[20] = imageLoader.loadImageFromAssets("Explosion21.png", miniWidth, miniHeight);
                rocketExplodeSprite[21] = imageLoader.loadImageFromAssets("Explosion22.png", miniWidth, miniHeight);
                rocketExplodeSprite[22] = imageLoader.loadImageFromAssets("Explosion23.png", miniWidth, miniHeight);
                rocketExplodeSprite[23] = imageLoader.loadImageFromAssets("Explosion24.png", miniWidth, miniHeight);
                rocketExplodeSprite[24] = imageLoader.loadImageFromAssets("Explosion25.png", miniWidth, miniHeight);
                rocketExplodeSprite[25] = imageLoader.loadImageFromAssets("Explosion26.png", miniWidth, miniHeight);
                rocketExplodeSprite[26] = imageLoader.loadImageFromAssets("Explosion27.png", miniWidth, miniHeight);
                rocketExplodeSprite[27] = imageLoader.loadImageFromAssets("Explosion28.png", miniWidth, miniHeight);
                rocketExplodeSprite[28] = imageLoader.loadImageFromAssets("Explosion29.png", miniWidth, miniHeight);
                rocketExplodeSprite[29] = imageLoader.loadImageFromAssets("Explosion30.png", miniWidth, miniHeight);

                rocketExplodeSprite[30] = imageLoader.loadImageFromAssets("Explosion31.png", miniWidth, miniHeight);
                rocketExplodeSprite[31] = imageLoader.loadImageFromAssets("Explosion32.png", miniWidth, miniHeight);
                rocketExplodeSprite[32] = imageLoader.loadImageFromAssets("Explosion33.png", miniWidth, miniHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                miniExplodeSprite = new Image[20];
                miniExplodeSprite[0] = imageLoader.loadImageFromAssets("Bild-000001.png", miniWidth, miniHeight);
                miniExplodeSprite[1] = imageLoader.loadImageFromAssets("Bild-000002.png", miniWidth, miniHeight);
                miniExplodeSprite[2] = imageLoader.loadImageFromAssets("Bild-000003.png", miniWidth, miniHeight);
                miniExplodeSprite[3] = imageLoader.loadImageFromAssets("Bild-000004.png", miniWidth, miniHeight);
                miniExplodeSprite[4] = imageLoader.loadImageFromAssets("Bild-000005.png", miniWidth, miniHeight);
                miniExplodeSprite[5] = imageLoader.loadImageFromAssets("Bild-000006.png", miniWidth, miniHeight);
                miniExplodeSprite[6] = imageLoader.loadImageFromAssets("Bild-000007.png", miniWidth, miniHeight);
                miniExplodeSprite[7] = imageLoader.loadImageFromAssets("Bild-000008.png", miniWidth, miniHeight);
                miniExplodeSprite[8] = imageLoader.loadImageFromAssets("Bild-000009.png", miniWidth, miniHeight);
                miniExplodeSprite[9] = imageLoader.loadImageFromAssets("Bild-0000010.png", miniWidth, miniHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                miniExplodeSprite[10] = imageLoader.loadImageFromAssets("Bild-000011.png", miniWidth, miniHeight);
                miniExplodeSprite[11] = imageLoader.loadImageFromAssets("Bild-000012.png", miniWidth, miniHeight);
                miniExplodeSprite[12] = imageLoader.loadImageFromAssets("Bild-000013.png", miniWidth, miniHeight);
                miniExplodeSprite[13] = imageLoader.loadImageFromAssets("Bild-000014.png", miniWidth, miniHeight);
                miniExplodeSprite[14] = imageLoader.loadImageFromAssets("Bild-000015.png", miniWidth, miniHeight);
                miniExplodeSprite[15] = imageLoader.loadImageFromAssets("Bild-000016.png", miniWidth, miniHeight);
                miniExplodeSprite[16] = imageLoader.loadImageFromAssets("Bild-000017.png", miniWidth, miniHeight);
                miniExplodeSprite[17] = imageLoader.loadImageFromAssets("Bild-000018.png", miniWidth, miniHeight);
                miniExplodeSprite[18] = imageLoader.loadImageFromAssets("Bild-000019.png", miniWidth, miniHeight);
                miniExplodeSprite[19] = imageLoader.loadImageFromAssets("Bild-000020.png", miniWidth, miniHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                // blue tower
                Image[] blueTowerI = new Image[36];
                for (int i = 0; i < 36; i++)
                    blueTowerI[i] = imageLoader.loadImageFromAssets("mg" + i + "0.png", width, height);

                Image[] blueTowerII = new Image[36];
                for (int i = 0; i < 36; i++)
                    blueTowerII[i] = imageLoader.loadImageFromAssets("bgII" + i + "0.png", width, height);

                Image[] blueTowerIII = new Image[36];
                for (int i = 0; i < 36; i++)
                    blueTowerIII[i] = imageLoader.loadImageFromAssets("bgIII" + i + "0.png", width, height);

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
                for (int i = 0; i < 36; i++)
                    greenTowerI[i] = imageLoader.loadImageFromAssets("greenLS00" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                Image[] greenTowerII = new Image[36];
                for (int i = 0; i < 36; i++)
                    greenTowerII[i] = imageLoader.loadImageFromAssets("lsII" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                Image[] greenTowerIII = new Image[36];
                for (int i = 0; i < 36; i++)
                    greenTowerIII[i] = imageLoader.loadImageFromAssets("lsIII" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
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
                for (int i = 0; i < 36; i++)
                    redTowerI[i] = imageLoader.loadImageFromAssets("rmI" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                Image[] redTowerII = new Image[36];
                for (int i = 0; i < 36; i++)
                    redTowerII[i] = imageLoader.loadImageFromAssets("rmII" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                Image[] redTowerIII = new Image[36];
                for (int i = 0; i < 36; i++)
                    redTowerIII[i] = imageLoader.loadImageFromAssets("rmIII" + i + "0.png", width, height);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
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
                healthBarSprite[0] = imageLoader.loadImageFromAssets("health_bar_10.png", width_hp, height_hp);
                healthBarSprite[1] = imageLoader.loadImageFromAssets("health_bar_20.png", width_hp, height_hp);
                healthBarSprite[2] = imageLoader.loadImageFromAssets("health_bar_30.png", width_hp, height_hp);
                healthBarSprite[3] = imageLoader.loadImageFromAssets("health_bar_40.png", width_hp, height_hp);
                healthBarSprite[4] = imageLoader.loadImageFromAssets("health_bar_50.png", width_hp, height_hp);
                healthBarSprite[5] = imageLoader.loadImageFromAssets("health_bar_60.png", width_hp, height_hp);
                healthBarSprite[6] = imageLoader.loadImageFromAssets("health_bar_70.png", width_hp, height_hp);
                healthBarSprite[7] = imageLoader.loadImageFromAssets("health_bar_80.png", width_hp, height_hp);
                healthBarSprite[8] = imageLoader.loadImageFromAssets("health_bar_90.png", width_hp, height_hp);
                healthBarSprite[9] = imageLoader.loadImageFromAssets("health_bar_100.png", width_hp, height_hp);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }


                Image score = imageLoader.loadImageFromAssets("SmallBox.png", 300, 150);
                Image titleScore = imageLoader.loadImageFromAssets("HealthBar.png", 300, 70);

                int numWidth = width / 3;//50;
                int numHeight = width / 2;//70;

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                dollarSign = imageLoader.loadImageFromAssets("money.png", numWidth / 2, numHeight / 2);

                scorenumbersImages = new Image[10];

                scorenumbersImages[0] = imageLoader.loadImageFromAssets("0.png", numWidth, numHeight);
                scorenumbersImages[1] = imageLoader.loadImageFromAssets("1.png", numWidth, numHeight);
                scorenumbersImages[2] = imageLoader.loadImageFromAssets("2.png", numWidth, numHeight);
                scorenumbersImages[3] = imageLoader.loadImageFromAssets("3.png", numWidth, numHeight);
                scorenumbersImages[4] = imageLoader.loadImageFromAssets("4.png", numWidth, numHeight);
                scorenumbersImages[5] = imageLoader.loadImageFromAssets("5.png", numWidth, numHeight);
                scorenumbersImages[6] = imageLoader.loadImageFromAssets("6.png", numWidth, numHeight);
                scorenumbersImages[7] = imageLoader.loadImageFromAssets("7.png", numWidth, numHeight);
                scorenumbersImages[8] = imageLoader.loadImageFromAssets("8.png", numWidth, numHeight);
                scorenumbersImages[9] = imageLoader.loadImageFromAssets("9.png", numWidth, numHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                moneyNumbersImages = new Image[10];

                moneyNumbersImages[0] = imageLoader.loadImageFromAssets("0.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[1] = imageLoader.loadImageFromAssets("1.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[2] = imageLoader.loadImageFromAssets("2.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[3] = imageLoader.loadImageFromAssets("3.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[4] = imageLoader.loadImageFromAssets("4.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[5] = imageLoader.loadImageFromAssets("5.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[6] = imageLoader.loadImageFromAssets("6.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[7] = imageLoader.loadImageFromAssets("7.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[8] = imageLoader.loadImageFromAssets("8.png", numWidth / 2, numHeight / 2);
                moneyNumbersImages[9] = imageLoader.loadImageFromAssets("9.png", numWidth / 2, numHeight / 2);

                int upgradeDialogBackrgoundImageWidth = borderX / 3;
                int upgradeDialogBackgroundImageHeight = (borderY * 16 / 50);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                Image[] dialogUpgradeTower1 = new Image[3];

                dialogUpgradeTower1[0] = imageLoader.loadImageFromAssets("mgleve2.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower1[1] = imageLoader.loadImageFromAssets("mgleve3.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower1[2] = imageLoader.loadImageFromAssets("mgleveTOP.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);

                Image[] dialogUpgradeTower2 = new Image[3];
                dialogUpgradeTower2[0] = imageLoader.loadImageFromAssets("rcleve2.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower2[1] = imageLoader.loadImageFromAssets("rcleve3.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower2[2] = imageLoader.loadImageFromAssets("rcleveTOP.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);

                Image[] dialogUpgradeTower3 = new Image[3];
                dialogUpgradeTower3[0] = imageLoader.loadImageFromAssets("lsleve2.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower3[1] = imageLoader.loadImageFromAssets("lsleve3.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);
                dialogUpgradeTower3[2] = imageLoader.loadImageFromAssets("lsleveTOP.png", upgradeDialogBackrgoundImageWidth, upgradeDialogBackgroundImageHeight);

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

                redTowerUpgSprite = dialogUpgradeTower1;
                blueTowerUpgSprite = dialogUpgradeTower2;
                greenTowerUpgSprite = dialogUpgradeTower3;
                dialogueImageTowerUpgrade = redTowerUpgSprite;

                if (loaderBar.hasNext()) {
                    loaderBar.next().show();
                    renderer.drawScene();
                }

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

                laserSprite = new Image[] {imageLoader.loadImageFromAssets("greenPhoton.png",  width / 15, width / 15)};

                renderer.drawScene();

                gameConsumer.consume(stateChain::next);

            } catch (IOException ex) {
                System.err.println(DaemonUtils.tag() + "Could not init game!");
                ex.printStackTrace();
            }

        }).addState(()-> { //views and dialogs population

            //add background to scene
            backgroundView = scene.addImageView(new ImageViewImpl("Background").setImageWithoutOffset(backgroundImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(0).show());

            //dialogues and ui views
            scoreBackGrView = new ImageViewImpl("Score Background").setImage(scoreBackGrImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(3).show();
            scoreTitleView = new ImageViewImpl("Score Title").setAbsoluteX(0).setAbsoluteY(0).setZindex(4).show();

            viewsNum = new ImageView[5];
            viewsNum[0] = new ImageViewImpl("Score 1. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[1] = new ImageViewImpl("Score 2. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[2] = new ImageViewImpl("Score 3. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[3] = new ImageViewImpl("Score 4. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[4] = new ImageViewImpl("Score 5. digit").setImage(scorenumbersImages[0]).setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();

            //money views
            moneyView = Pair.create(
                    scene.addImageView(new ImageViewImpl("Money Amount").setZindex(10).setImage(scorenumbersImages[0]).hide()),
                    scene.addImageView(new ImageViewImpl("Dollar Sign").setZindex(10).setImage(dollarSign).hide())
            );

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

                tow.setRotationSprite(currentSprite);

                towerSpriteUpgrader.daemonize(tow.getPrototype()::updateSprite, new MultiViewAnimateClosure()::onReturn);

                renderer.consume(()->upgradeButton.disable().setImage(upgradeButtonImagePressed));
                towerSpriteUpgrader.daemonize(gameConsumer, ()->Thread.sleep(100), ()->{

                    //tow.cont();

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
                renderer.consume(()->closeButton.disable().setImage(closeButtonImagePressed));
                towerSpriteUpgrader.daemonize(gameConsumer, ()->Thread.sleep(100), ()->{
                    renderer.consume(()->closeButton.enable().setImage(closeButtonImage));
                    renderer.consume(towerUpgradeDialogue.getTowerUpgrade()::hide);
                });
            });


            Button saleButton = new Button("Sale", saleButtonImage);
            saleButton.onClick(()->{

                renderer.consume(()->saleButton.disable().setImage(saleButtonImagePressed));
                towerSpriteUpgrader.daemonize(gameConsumer, ()->Thread.sleep(100), ()->{
                    TowerDaemon tower = towerUpgradeDialogue.getTower();
                    renderer.consume(tower.getHpView()::hide);

                    Field field = grid.getField(
                            tower.getLastCoordinates().getFirst(),
                            tower.getLastCoordinates().getSecond()
                    );

                    //stop and remove tower
                    tower.stop();
                    towers.remove(tower);
                    field.setTower(null);

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
                    borderY / 2
            );

            Button tow1 = new Button("TowerType1", redTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE1;
                currentTowerSprite = redTower.get(0);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(selection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
                });
            });

            Button tow2 = new Button("TowerType2", blueTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE2;
                currentTowerSprite = blueTower.get(0);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(selection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
                });

            });

            Button tow3 = new Button("TowerType3", greenTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE3;
                currentTowerSprite = greenTower.get(0);
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

            for (int j = 0; j < rows - 1; ++j ) {
                for (int i = 0; i < columns - 1; ++i)
                    diagonalMatrix[j][i] = scene.addImageView(new ImageViewImpl("Grid [" + j + "][" + i +"]"))
                     .setAbsoluteX(grid.getGrid()[j][i].getCenterX() + gridViewMatrix[j][i].getImage().getWidth() / 2)
                     .setAbsoluteY(grid.getGrid()[j][i].getCenterY() + gridViewMatrix[j][i].getImage().getHeight() / 2)
                     .setImage(fieldGreenDiagonal)
                     .setZindex(3)
                     .hide();
            }

            //tower hp views
            towerHpViwes = new ImageView[rows][columns];

            for (int j = 0; j < rows; ++j ) {
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
            }

            //enemy repo init
            enemyRepo = new QueuedEntityRepo<EnemyDoubleDaemon>() {
                @Override
                public void onAdd(EnemyDoubleDaemon enemy) {
                    enemy.setShootable(false).clearVelocity().clearAndInterrupt();

                    renderer.consume(()->enemy.getHpView().hide().setAbsoluteX(0).setAbsoluteY(0));

                    enemy.pushSprite(explodeSprite, 0, ()->{
                        renderer.consume(()->enemy.getView().hide().setAbsoluteX(0).setAbsoluteY(0));
                        enemy.popSprite().setPreviousField(null).setCoordinates(grid.getStartingX(), grid.getStartingY()).stop();
                    });

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
            bulletRepo = new StackedEntityRepo<BulletDoubleDaemon>() {
                @Override
                public void onAdd(BulletDoubleDaemon bullet) {
                    renderer.consume(() -> {
                        for (ImageView view : bullet.getViews())
                            view.hide();
                    });
                    bullet.clearAndInterrupt().clearVelocity().popSprite().pause();
                    activeBullets.remove(bullet);
                }

                @Override
                public void onGet(BulletDoubleDaemon bullet) {
                    renderer.consume(()->{
                        for (ImageView view : bullet.getViews()) {
                            view.setAbsoluteX(bullet.getLastCoordinates().getFirst());
                            view.setAbsoluteY(bullet.getLastCoordinates().getSecond());
                            view.show();
                        }
                    });
                    activeBullets.add(bullet);
                }
            };

            //rocket repo init
            rocketRepo = new StackedEntityRepo<BulletDoubleDaemon>() {
                @Override
                public void onAdd(BulletDoubleDaemon rocket) {
                    renderer.consume(() -> {
                        for (ImageView view : rocket.getViews())
                            view.hide();
                    });
                    rocket.clearAndInterrupt().clearVelocity().popSprite().pause();
                    activeRockets.remove(rocket);
                }

                @Override
                public void onGet(BulletDoubleDaemon rocket) {
                    renderer.consume(()->{
                        for (ImageView view : rocket.getViews()) {
                            view.setAbsoluteX(rocket.getLastCoordinates().getFirst());
                            view.setAbsoluteY(rocket.getLastCoordinates().getSecond());
                            view.show();
                        }
                    });
                    activeRockets.add(rocket);
                }
            };

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
                        .setHealthBarImage(healthBarSprite)
                ).setName(enemyName);

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
                ).setName(bulletName);

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
                ).setName(rocketName);

                rocketDoubleDaemon.getPrototype().setBorders(0, borderX, 0, borderY);

                rocketDoubleDaemon.setOutOfBordersConsumer(gameConsumer)
                        .setOutOfBordersClosure(()-> rocketRepo.add(rocketDoubleDaemon.clearAndInterrupt()))
                        .setAnimateBulletSideQuest(renderer)
                        .setClosure(new MultiViewAnimateClosure()::onReturn);

                rocketRepo.getStructure().push(rocketDoubleDaemon);
            }

            rocketExplosionRange = fieldImage.getWidth();

            scene.addImageViews(laserViews);

            //laser init
            laser = new LaserBulletDaemon(
                    gameConsumer,
                    new LaserBullet(laserSprite, 40, Pair.create(0F, 0F), bulletDamage, dXY)
            ).setViews(laserViews);

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

            scene.forEach(view->{
                System.out.println(DaemonUtils.tag() + view.getName());
                System.out.println(DaemonUtils.tag() + "X: " + view.getAbsoluteX());
                System.out.println(DaemonUtils.tag() + "Y: " + view.getAbsoluteY());
                System.out.println(DaemonUtils.tag() + "Image: "  + view.getImage().toString());
                System.out.println(DaemonUtils.tag() + "Image imp: " + view.getImage().getImageImp().toString());
            });

            renderer.setScene(scene).start();

            gameConsumer.consume(stateChain::next);

        }).addState(()->{//gameState

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
                        borderY / 5,
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
            enemyGenerator = DummyDaemon.create(gameConsumer, enemyGenerateinterval).setClosure(()->{

                enemyCounter++;

                //every... enemies increase the pain!!!!
                if (enemyCounter % 3 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

                    if (enemyGenerateinterval > 1000)
                        enemyGenerateinterval -= 500;

                    if (enemyCounter % 30 == 0 && waveInterval > 6000)
                        waveInterval -= 2000;

                    enemyHp++;
                    enemyGenerator.setSleepInterval(waveInterval);

                } else
                    enemyGenerator.setSleepInterval(enemyGenerateinterval);


                if (enemyCounter % 20 == 0 && bulletDamage < 10)
                    bulletDamage += 1;

                EnemyDoubleDaemon enemyDoubleDaemon = enemyRepo.getAndConfigure(enemy->enemy.setMaxHp(enemyHp).setHp(enemyHp));

                System.err.println(DaemonUtils.timedTag() + enemyDoubleDaemon.getName() + ", STATES: " + enemyDoubleDaemon.getEnginesState().toString());

                System.out.println(DaemonUtils.tag() + "Enemy counter: " + enemyCounter);
                System.out.println(DaemonUtils.tag() + "Enemy repo size: " + enemyRepo.size());
                System.out.println(DaemonUtils.tag() + "Enemy state: " + enemyDoubleDaemon.getEnginesState().get(enemyDoubleDaemon.getEnginesState().size() - 1));

                enemyDoubleDaemon.setTarget(null).reload(new Runnable() {
                    @Override
                    public void run() {

                        TowerDaemon target = enemyDoubleDaemon.getTarget();

                        if (target != null) {

                            BulletDoubleDaemon rocket = rocketRepo.configureAndGet(bullet -> {
                                bullet.setCoordinates(enemyDoubleDaemon.getLastCoordinates().getFirst(), enemyDoubleDaemon.getLastCoordinates().getSecond())
                                        .setLevel(3)
                                        .setDamage(bulletDamage)
                                        .setSprite(bulletSpriteRocket);
                                if (bullet.getEnginesState().get(bullet.getEnginesState().size() - 1).equals(DaemonState.STOPPED))
                                    bullet.start();
                                else
                                    bullet.cont();
                            });

                            int targetAngle = (int) RotatingSpriteImageMover.getAngle(
                                    enemyDoubleDaemon.getLastCoordinates().getFirst(),
                                    enemyDoubleDaemon.getLastCoordinates().getSecond(),
                                    target.getLastCoordinates().getFirst(),
                                    target.getLastCoordinates().getSecond()
                            );

                            rocket.rotateAndGoTo(
                                    targetAngle,
                                    target.getLastCoordinates().getFirst(),
                                    target.getLastCoordinates().getSecond(),
                                    15,
                                    ret -> {

                                        if (!ret.runtimeCheckAndGet()) {
                                            bulletRepo.add(rocket);
                                            return;
                                        }

                                        int newHp = target.getHp() - rocket.getDamage();

                                        if (newHp > 0)
                                            target.setHp(newHp);
                                        else {

                                            renderer.consume(()->target.getHpView().hide().setImage(healthBarSprite[9]));
                                            enemyDoubleDaemon.setTarget(null);

                                            Field field = grid.getField(
                                                    target.getLastCoordinates().getFirst(),
                                                    target.getLastCoordinates().getSecond()
                                            );

                                            target.clearAndInterrupt().pushSprite(explodeSprite, 0, () -> {

                                                renderer.consume(target.getView()::hide);

                                                target.stop();
                                                towers.remove(target);
                                                field.setTower(null);

                                                //remove tower from grid and recalculate path
                                                if (grid.destroyTower(field.getRow(), field.getColumn()))
                                                    renderer.consume(() -> gridViewMatrix[field.getRow()][field.getColumn()].setImage(fieldImage).hide());
                                                else
                                                    throw new IllegalStateException("Could not destroy tower");
                                            });
                                        }

                                        rocket.pushSprite(rocketExplodeSprite, 0, () -> rocketRepo.add(rocket));
                                    });
                        }

                        enemyDoubleDaemon.reload(this::run);
                    }
                }).rotate(
                        (int) RotatingSpriteImageMover.getAngle(
                                enemyDoubleDaemon.getLastCoordinates().getFirst(),
                                enemyDoubleDaemon.getLastCoordinates().getSecond(),
                                firstField.getCenterX(),
                                firstField.getCenterY()
                        )
                ).goTo(
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
                                Field current = grid.getField(currentCoord.getFirst(), currentCoord.getSecond());

                                for(Field neighbour : grid.getNeighbors(current)) {
                                    if (neighbour.getTower() != null) {
                                        neighbour.getTower().addTarget(enemyDoubleDaemon);//TODO check ret val
                                        enemyDoubleDaemon.setTarget(neighbour.getTower());
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
                        });
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

            towerSpriteUpgrader = new EagerMainQuestDaemonEngine(renderer).setName("Tower Sprite Upgrader").start();
            fieldEraserEngine = new EagerMainQuestDaemonEngine(renderer).setName("Field Eraser").start();
        });
    }

    private boolean deny;

    private void setTower(float x, float y) {

        //check if correct field
        Field field = grid.getField(x, y);
        if (field == null) return;

        TowerDaemon tow = field.getTower();

        if (tow != null) {//upgrade existing tower
            if (!towerUpgradeDialogue.getTowerUpgrade().isShowing()) {//if upgrade dialog not shown
                //pause();
                Tower.TowerLevel currLvl = tow.getTowerLevel();

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

                    towerUpgradeDialogue.getTowerUpgrade()
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2);

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

        } else { //init and set new tower

            ImageView fieldView = gridViewMatrix[field.getRow()][field.getColumn()];

            //check if selected field is on the last remaining path
            if (!grid.setTower(field.getRow(), field.getColumn())){

                if (!deny) {

                    deny = true;

                    boolean isGeenFieldShown = fieldView.isShowing() && fieldView.getImage().equals(fieldImage);
                    renderer.consume(fieldView.setImage(fieldImageTowerDen)::show);

                    AtomicInteger markerCnt = new AtomicInteger(0);

                    DummyDaemon deniedMarker = new DummyDaemon(renderer, 300);
                    deniedMarker.setClosure(() -> {

                        if (fieldView.isShowing())
                            fieldView.hide();
                        else
                            fieldView.show();

                        if (markerCnt.intValue() == 6) {
                            fieldView.setImage(fieldImage);
                            if (isGeenFieldShown)
                                fieldView.show();
                            else
                                fieldView.hide();
                            deniedMarker.stop();
                            deny = false;
                        }

                        markerCnt.incrementAndGet();

                    }).setName("Denied marker").start();
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

                Tower towerPrototype = towerSelect == Tower.TowerType.TYPE3
                        ? new LaserTower (
                                renderer,
                                new ImageAnimateClosure(fieldView),
                                currentTowerSprite,
                                Pair.create(field.getCenterX(), field.getCenterY()),
                                range,
                                towerSelect,
                                dXY,
                                towerHp
                        ).setHpView(towerHpViwes[field.getRow()][field.getColumn()].setAbsoluteX(field.getCenterX()).setAbsoluteY(field.getCenterY() - 2 * healthBarSprite[9].getHeight()).show())
                        .setHealthBarImage(healthBarSprite)
                        .setTowerLevel(new Tower.TowerLevel(1,2,1500))
                :       new Tower(
                        currentTowerSprite,
                        Pair.create(field.getCenterX(), field.getCenterY()),
                        range,
                        towerSelect,
                        dXY,
                        towerHp
                )
                .setHpView(towerHpViwes[field.getRow()][field.getColumn()].setAbsoluteX(field.getCenterX()).setAbsoluteY(field.getCenterY() - 2 * healthBarSprite[9].getHeight()).show())
                .setHealthBarImage(healthBarSprite)
                .setTowerLevel(new Tower.TowerLevel(1,2,1500));

                TowerDaemon towerDaemon = new TowerDaemon(gameConsumer, towerPrototype)
                        .setName(towerName)
                        .setView(fieldView);

                towers.add(towerDaemon);
                field.setTower(towerDaemon);

                towerDaemon.setAnimateTowerSideQuest(renderer).setClosure(new MultiViewAnimateClosure()::onReturn);

                towerDaemon.start().scan(new Closure<Pair<Tower.TowerType, EnemyDoubleDaemon>>() {
                    @Override
                    public void onReturn(Return<Pair<Tower.TowerType, EnemyDoubleDaemon>> towerTypeAndEnemy) {

                        long reloadInterval = towerDaemon.getTowerLevel().reloadInterval;

                        if (towerTypeAndEnemy.runtimeCheckAndGet().getFirst() != null
                                && towerTypeAndEnemy.runtimeCheckAndGet().getSecond() != null) {

                            Tower.TowerType towerType = towerTypeAndEnemy.get().getFirst();
                            EnemyDoubleDaemon enemy = towerTypeAndEnemy.get().getSecond();

                            switch (towerType) {
                                case TYPE1:
                                    fireBullet(
                                            towerDaemon.getLastCoordinates(),
                                            enemy.getLastCoordinates(),
                                            enemy,
                                            25,
                                            towerDaemon.getTowerLevel().bulletDamage,
                                            towerDaemon.getTowerLevel().currentLevel
                                    );
                                    break;
                                case TYPE2:
                                    fireRocketBullet(
                                            towerDaemon.getLastCoordinates(),
                                            enemy,
                                            18,
                                            towerDaemon.getTowerLevel().bulletDamage,
                                            towerDaemon.getTowerLevel().currentLevel
                                    );
                                    break;
                                case TYPE3:
                                    double angle = RotatingSpriteImageMover.getAngle(
                                            towerDaemon.getLastCoordinates().getFirst(),
                                            towerDaemon.getLastCoordinates().getSecond(),
                                            enemy.getLastCoordinates().getFirst(),
                                            enemy.getLastCoordinates().getSecond()
                                    );

                                    towerDaemon.setCurrentAngle((int) angle);

                                    int lvl = towerDaemon.getTowerLevel().currentLevel;

                                    float velocity = lvl == 1 ? enemy.getVelocity().intensity / 2 : lvl == 2 ? enemy.getVelocity().intensity / 4 : 0;
                                    long duration = lvl == 1 ? 200 : lvl == 2 ? 400 : 600;

                                    fireLaser(towerDaemon.getLastCoordinates(), enemy, velocity, duration);
                                    reloadInterval = 4000;
                                    break;
                                default:
                                    throw new IllegalStateException("Tower type does not exist!");
                            }
                        }

                        towerDaemon.reload(reloadInterval, () -> towerDaemon.scan(this::onReturn));
                    }
                });
            }
        }
    }

    private void fireBullet(
            Pair<Float, Float> sourceCoord,
            Pair<Float, Float> targetCoord,
            EnemyDoubleDaemon enemy,
            float velocity,
            int bulletDamage,
            int noOfBulletsFired
    ) {
        System.out.println(DaemonUtils.tag() + "Bullet repo size: " + bulletRepo.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletRepo.configureAndGet(bullet -> {
            bullet.setCoordinates(sourceCoord.getFirst(), sourceCoord.getSecond())
                    .setLevel(noOfBulletsFired)
                    .setDamage(bulletDamage)
                    .setSprite(bulletSprite);

            if (bullet.getEnginesState().get(bullet.getEnginesState().size() - 1).equals(DaemonState.STOPPED))
                bullet.start();
            else
                bullet.cont();
        });

        bulletDoubleDaemon.goTo(targetCoord.getFirst(), targetCoord.getSecond(), velocity, ret -> {

            if (!ret.runtimeCheckAndGet() || !enemy.isShootable()) {
                bulletRepo.add(bulletDoubleDaemon);
                return;
            }

            int newHp = enemy.getHp() - bulletDoubleDaemon.getDamage();
            if (newHp > 0)
                enemy.setHp(newHp);
            else {

                enemyRepo.add(enemy);
                moneyDaemon.setAmount(3)
                        .setCoordinates(targetCoord.getFirst(), targetCoord.getSecond())
                        .goTo(scoreTitleView.getAbsoluteX(), scoreTitleView.getAbsoluteY(), 13, moneyGoToClosure::onReturn);

                renderer.consume(()->{
                    moneyView.getFirst().show();
                    moneyView.getSecond().show();
                });
            }

            bulletDoubleDaemon.pushSprite(miniExplodeSprite, 0, ()->bulletRepo.add(bulletDoubleDaemon));
        });
    }

    private void fireRocketBullet(
            Pair<Float, Float> sourceCoord,
            EnemyDoubleDaemon enemy,
            float velocity,
            int bulletDamage,
            int noOfBulletsFired
    ) {
        System.out.println(DaemonUtils.tag() + "Rocket repo size: " + rocketRepo.size());

        BulletDoubleDaemon rocketDoubleDaemon = rocketRepo.configureAndGet(rocket-> {
            rocket.setCoordinates(sourceCoord.getFirst(), sourceCoord.getSecond())
                    .setLevel(noOfBulletsFired)
                    .setDamage(bulletDamage)
                    .setSprite(bulletSpriteRocket);

            if (rocket.getEnginesState().get(rocket.getEnginesState().size() - 1).equals(DaemonState.STOPPED))
                rocket.start();
            else
                rocket.cont();

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
                rocketRepo.add(rocketDoubleDaemon);
                return;
            }

            if (!enemy.isShootable()) {
                rocketDoubleDaemon.rotateAndGoTo(
                        - angle,
                        sourceCoord.getFirst(),
                        sourceCoord.getSecond(),
                        4,
                        ret1 -> rocketRepo.add(rocketDoubleDaemon)
                );
            } else {

                Pair<Float, Float> targetCoord = enemy.getLastCoordinates();

                int targetAngle1 = (int) RotatingSpriteImageMover.getAngle(
                        rocketDoubleDaemon.getLastCoordinates().getFirst(),
                        rocketDoubleDaemon.getLastCoordinates().getSecond(),
                        enemy.getLastCoordinates().getFirst(),
                        enemy.getLastCoordinates().getSecond()
                );

                rocketDoubleDaemon.rotateAndGoTo(
                        targetAngle1,
                        enemy.getLastCoordinates().getFirst(),
                        enemy.getLastCoordinates().getSecond(),
                        velocity,
                        ret2 -> {

                            if (!ret2.runtimeCheckAndGet() || !enemy.isShootable()) {
                                rocketRepo.add(rocketDoubleDaemon);
                                return;
                            }

                            float bulletX = rocketDoubleDaemon.getLastCoordinates().getFirst();
                            float bulletY = rocketDoubleDaemon.getLastCoordinates().getSecond();

                            if (Math.abs(bulletX - enemy.getLastCoordinates().getFirst()) < rocketExplosionRange
                                    && Math.abs(bulletY - enemy.getLastCoordinates().getSecond()) < rocketExplosionRange) {

                                int newHp = enemy.getHp() - rocketDoubleDaemon.getDamage();

                                if (newHp > 0)
                                    enemy.setHp(newHp);
                                else {

                                    enemyRepo.add(enemy);
                                    moneyDaemon.setAmount(5)
                                            .setCoordinates(targetCoord.getFirst(), targetCoord.getSecond())
                                            .goTo(scoreTitleView.getAbsoluteX(), scoreTitleView.getAbsoluteY(), 13, moneyGoToClosure::onReturn);

                                    renderer.consume(() -> {
                                        moneyView.getFirst().show();
                                        moneyView.getSecond().show();
                                    });
                                }
                            }

                            rocketDoubleDaemon.pushSprite(rocketExplodeSprite, 0, () -> rocketRepo.add(rocketDoubleDaemon));
                        });
            }
        });
    }

    public void fireLaser(Pair<Float, Float> source, EnemyDoubleDaemon enemy, float velocity, long duration) {

        if (enemy.isParalyzed())
            return;

        enemy.setParalyzed(true);

        laser.desintegrateTarget(source, enemy, duration, renderer, ret -> {

            int newHp = enemy.getHp() - laser.getDamage();
            if (newHp > 0) {

                enemy.setHp(newHp).setVelocity(velocity);

                if (enemyParalyizer.queueSize() == 0) {
                    enemyParalyizer.daemonize(() -> Thread.sleep(enemyParalyzingInterval), () -> {
                        enemy.setParalyzed(false);
                        if (enemy.isShootable())
                            enemy.setVelocity(enemyVelocity);
                    });
                } else {
                    new MainQuestDaemonEngine(gameConsumer).setName("Helper Paralyzer").start().daemonize(()->{
                        System.err.println(DaemonUtils.timedTag() + "Enemy paralyzer busy. Spawning a new paralyzer engine.");
                        Thread.sleep(enemyParalyzingInterval);
                    },()->{
                        enemy.setParalyzed(false);
                        if (enemy.isShootable())
                            enemy.setVelocity(enemyVelocity);
                    });
                }

            } else {

                Pair<Float, Float> targetCoord = enemy.getLastCoordinates();
                enemyRepo.add(enemy);

                moneyDaemon.setAmount(1)
                        .setCoordinates(targetCoord.getFirst(), targetCoord.getSecond())
                        .goTo(scoreTitleView.getAbsoluteX(), scoreTitleView.getAbsoluteY(), 13, moneyGoToClosure::onReturn);

                renderer.consume(() -> {
                    moneyView.getFirst().show();
                    moneyView.getSecond().show();
                });
            }
        });
    }

    private Closure<Boolean> moneyGoToClosure = moneyGoToRet -> {

        moneyGoToRet.runtimeCheckAndGet();

        renderer.consume(()->{
            moneyView.getFirst().hide();
            moneyView.getSecond().hide();
        });

        renderer.consume(()->infoScore.setNumbers(++score));
    };
}