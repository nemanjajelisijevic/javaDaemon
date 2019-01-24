package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;

import com.daemonize.game.images.Image;

import com.daemonize.game.images.imageloader.ImageLoader;
import com.daemonize.game.renderer.DrawConsumer;
import com.daemonize.game.renderer.Renderer2D;
import com.daemonize.game.repo.EntityRepo;
import com.daemonize.game.repo.QueuedEntityRepo;
import com.daemonize.game.repo.StackedEntityRepo;
import com.daemonize.game.scene.Scene2D;

import com.daemonize.game.tabel.Field;
import com.daemonize.game.tabel.Grid;

import com.daemonize.game.view.Button;
import com.daemonize.game.view.CompositeImageViewImpl;
import com.daemonize.game.view.ImageView;
import com.daemonize.game.view.ImageViewImpl;

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
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Game {

    //running flag
    private volatile boolean running;

    //pause flag
    private volatile boolean paused;

    //game threads
    private Renderer2D renderer;
    private DaemonConsumer gameConsumer;

    //image loader
    private ImageLoader imageLoader;

    //state holder
    private DaemonChainScript chain = new DaemonChainScript();

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

    //score
    private int score = 0;
    private ImageView scoreBackGrView;
    private ImageView scoreTitleView;
    private ImageView[] viewsNum;
    private InfoTable infoScore;

    private Image fieldImage;
    private Image fieldImageTower;
    private Image fieldImageTowerDen;
    private Image[] dialogueImageTowerUpgrade;
    private Image upgradeButtonImage;
    private Image saleButtonImage;
    private Image closeButtonImage;
    private Image scoreBackGrImage;
    private Image[] scorenumbersImages;

    //towers
    private Image[] redTowerUpgSprite;
    private Image[] blueTowerUpgSprite;
    private Image[] greenTowerUpgSprite;

    private List<Image[]> redTower;
    private List<Image[]> blueTower;
    private List<Image[]> greenTower;

    private Set<TowerDaemon> towers = new HashSet<>();
    private int range = 250;
    private Tower.TowerType towerSelect;

    private Image[] currentTowerSprite;

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
    private int enemyHp = 10;
    private long enemyGenerateinterval = 5000;
    private long waveInterval = 20000;

    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();

    private int maxEnemies = 40;
    private EntityRepo<Queue<EnemyDoubleDaemon>, EnemyDoubleDaemon> enemyRepo;

    //explosions
    private Image[] explodeSprite;
    private Image[] miniExplodeSprite;

    //bullets
    private Image[] bulletSprite;
    private Image[] bulletSpriteRocket;

    private int bulletDamage = 2;
    private int rocketExplosionRange = 200;

    private int maxBullets = 100;
    private EntityRepo<Stack<BulletDoubleDaemon>, BulletDoubleDaemon> bulletRepo;

    //laser
    private LaserBulletDaemon laser;
    private List<ImageView> laserViews;
    private Image[] laserSprite;
    private int laserViewNo = 50;

    //random int
    private Random random = new Random();

    private int getRandomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    //closures
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
            GenericNode.forEach(aReturn.runtimeCheckAndGet(), actionret -> {
                Pair<ImageMover.PositionedImage, ImageView> imageAndView = actionret.runtimeCheckAndGet();
                imageAndView.getSecond().setAbsoluteX(imageAndView.getFirst().positionX);
                imageAndView.getSecond().setAbsoluteY(imageAndView.getFirst().positionY);
                imageAndView.getSecond().setImage(imageAndView.getFirst().image);
            });
        }
    }

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
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        gameConsumer.consume(()->{
            enemyGenerator.stop();
            for (EnemyDoubleDaemon enemy : activeEnemies)
                enemy.pause();
            for (TowerDaemon tower : towers)
                tower.pause();
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
            renderer.start();
            paused = false;
        });
    }

    public boolean isRunning() {
        return running;
    }

    public Game run() {
        gameConsumer.consume(()->{
            gameConsumer.consume(()->chain.run());
            this.running = true;
            this.paused = false;
        });
        gameConsumer.start();
        return this;
    }

    public Game stop(){
        gameConsumer.consume(()-> {
            enemyGenerator.stop();
            for(EnemyDoubleDaemon enemy : new ArrayList<>(activeEnemies)) enemy.stop();
            for (TowerDaemon tower : towers) tower.stop();
            laser.stop();
            scene.unlockViews();
            renderer.stop();
            this.running = false;
            gameConsumer.stop();
        });
        return this;
    }

    public Game onTouch(float x, float y) {
        gameConsumer.consume(()->{
            if (towerUpgradeDialogue.getTowerUpgrade().isShowing()){
                towerUpgradeDialogue.getTowerUpgrade().checkCoordinates(x, y);
            } else {

                if (selectTowerDialogue.getSelectTowerDialogue().isShowing()){
                   selectTowerDialogue.getSelectTowerDialogue().checkCoordinates(x,y);
                }

                if (towerSelect == null ){
                    System.out.println("Select" + "please select tower");
                } else {
                    setTower(x, y);
                }
            }
        });
        return this;
    }

    {
        //init state
        chain.addState(()-> { //image loading State

            try {

                int gridWidth = (borderX * 70) / 100;

                int rows = 6;
                int columns = 9;

                int width = gridWidth/columns;
                int height = width; //160

                backgroundImage = imageLoader.loadImageFromAssets("maphi.jpg", borderX, borderY);

                fieldImage = imageLoader.loadImageFromAssets("green.png", width, height);
                fieldImageTower = imageLoader.loadImageFromAssets("Exceptione.png", width, height);
                fieldImageTowerDen = imageLoader.loadImageFromAssets("red.png", width, height);

                upgradeButtonImage = imageLoader.loadImageFromAssets("ButtonUpgrade.png",350,90);
                closeButtonImage = imageLoader.loadImageFromAssets("ButtonX.png",110,120);
                saleButtonImage = imageLoader.loadImageFromAssets("ButtonSale.png",250,90);

                selection = imageLoader.loadImageFromAssets("green.png", 200, 200);
                deselection = imageLoader.loadImageFromAssets("red.png", 200, 200);

                scoreBackGrImage = imageLoader.loadImageFromAssets("SmallBox.png", 300, 150);



                laserSprite = new Image[] {imageLoader.loadImageFromAssets("greenPhoton.png", 10, 10)};

                //init enemy sprite
                enemySprite = new Image[36];

                for (int i = 0; i < 36; i++)
                    enemySprite[i] = imageLoader.loadImageFromAssets("plane" + Integer.toString(i) + "0.png", width, height);

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
                    bulletSpriteRocket[i] = imageLoader.loadImageFromAssets("rocket" + Integer.toString(i) + "0.png", bulletSize, bulletSize);

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

                int miniWidth = width / 3;
                int miniHeight = height / 3;

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

                blueTower = new ArrayList<>(3);
                blueTower.add(blueTowerI);
                blueTower.add(blueTowerII);
                blueTower.add(blueTowerIII);

                //green tower
                Image[] greenTowerI = new Image[36];
                for (int i = 0; i < 36; i++)
                    greenTowerI[i] = imageLoader.loadImageFromAssets("greenLS00" + i + "0.png", width, height);

                Image[] greenTowerII = new Image[36];
                for (int i = 0; i < 36; i++)
                    greenTowerII[i] = imageLoader.loadImageFromAssets("lsII" + i + "0.png", width, height);

                Image[] greenTowerIII = new Image[36];
                for (int i = 0; i < 36; i++)
                    greenTowerIII[i] = imageLoader.loadImageFromAssets("lsIII" + i + "0.png", width, height);

                greenTower = new ArrayList<>(3);
                greenTower.add(greenTowerI);
                greenTower.add(greenTowerII);
                greenTower.add(greenTowerIII);

                //red tower
                Image[] redTowerI = new Image[36];
                for (int i = 0; i < 36; i++)
                    redTowerI[i] = imageLoader.loadImageFromAssets("rmI" + i + "0.png", width, height);

                Image[] redTowerII = new Image[36];
                for (int i = 0; i < 36; i++)
                    redTowerII[i] = imageLoader.loadImageFromAssets("rmII" + i + "0.png", width, height);

                Image[] redTowerIII = new Image[36];
                for (int i = 0; i < 36; i++)
                    redTowerIII[i] = imageLoader.loadImageFromAssets("rmIII" + i + "0.png", width, height);

                redTower = new ArrayList<>(3);
                redTower.add(redTowerI);
                redTower.add(redTowerII);
                redTower.add(redTowerIII);


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

                Image score = imageLoader.loadImageFromAssets("SmallBox.png", 300, 150);
                Image titleScore = imageLoader.loadImageFromAssets("HealthBar.png", 300, 70);

                int numWidth = width / 3;//50;
                int numHeight = width / 2;//70;

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

                Image[] dialogUpgradeTower1 = new Image[3];

                dialogUpgradeTower1[0] = imageLoader.loadImageFromAssets("mgleve2.png", 800, 530);
                dialogUpgradeTower1[1] = imageLoader.loadImageFromAssets("mgleve3.png", 800, 530);
                dialogUpgradeTower1[2] = imageLoader.loadImageFromAssets("mgleveTOP.png", 800, 530);

                Image[] dialogUpgradeTower2 = new Image[3];
                dialogUpgradeTower2[0] = imageLoader.loadImageFromAssets("rcleve2.png", 800, 530);
                dialogUpgradeTower2[1] = imageLoader.loadImageFromAssets("rcleve3.png", 800, 530);
                dialogUpgradeTower2[2] = imageLoader.loadImageFromAssets("rcleveTOP.png", 800, 530);

                Image[] dialogUpgradeTower3 = new Image[3];
                dialogUpgradeTower3[0] = imageLoader.loadImageFromAssets("lsleve2.png", 800, 530);
                dialogUpgradeTower3[1] = imageLoader.loadImageFromAssets("lsleve3.png", 800, 530);
                dialogUpgradeTower3[2] = imageLoader.loadImageFromAssets("lsleveTOP.png", 800, 530);


                redTowerUpgSprite = dialogUpgradeTower1;
                blueTowerUpgSprite = dialogUpgradeTower2;
                greenTowerUpgSprite = dialogUpgradeTower3;
                dialogueImageTowerUpgrade = redTowerUpgSprite;

                chain.next();

            } catch (IOException ex) {
                System.err.println(DaemonUtils.tag() + "Could not init game!");
                ex.printStackTrace();
            }

        }).addState(()-> { //view populating

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

            Button upgradeButton = new Button("Upgrade", 0, 0, upgradeButtonImage).onClick(()->{

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

                //renderer.consume(()->new ImageAnimateClosure(tow.getView()).onReturn(tow.updateSprite()));

                tow.updateSprite(update-> renderer.consume(()->{
                    ImageMover.PositionedImage posBmp = update.runtimeCheckAndGet();
                    tow.getView().setAbsoluteX(posBmp.positionX);
                    tow.getView().setAbsoluteY(posBmp.positionY);
                    tow.getView().setImage(posBmp.image);
                }));

                tow.cont();

                CompositeImageViewImpl towerView = towerUpgradeDialogue.getTowerUpgrade().getViewByName("TowerView");

                renderer.consume(()->towerView.setImage(dialogueImageTowerUpgrade[tow.getTowerLevel().currentLevel - 1]));

                if (score > 2 && tow.getTowerLevel().currentLevel < 3)
                    renderer.consume(()-> towerUpgradeDialogue.getTowerUpgrade().getViewByName("Upgrade").show());
                else
                    renderer.consume(()-> towerUpgradeDialogue.getTowerUpgrade().getViewByName("Upgrade").hide());

                score -= 2;
                renderer.consume(()->infoScore.setNumbers(score));
            });


            Button closeButton = new Button("Close", 0, 0, closeButtonImage).onClick(()->
                    renderer.consume(()-> towerUpgradeDialogue.getTowerUpgrade().hide()));


            Button saleButton = new Button("Sale", 0, 0, saleButtonImage).onClick(()->{
                //cont();

                TowerDaemon tower = towerUpgradeDialogue.getTower();

                Field field = grid.getField(
                        tower.getLastCoordinates().getFirst(),
                        tower.getLastCoordinates().getSecond()
                );

                //stop and remove tower
                tower.stop();
                towers.remove(tower);
                field.setTower(null);

                //remove tower from grid and recalculate path
                if (grid.destroyTower(field.getRow(), field.getColumn())) {
                    renderer.consume(() -> {
                        gridViewMatrix[field.getRow()][field.getColumn()].setImage(fieldImage).show();
                        towerUpgradeDialogue.getTowerUpgrade().hide();
                        infoScore.setNumbers(++score);
                    });
                }
            });

            towerUpgradeDialogue = new TowerUpgradeDialog(
                    700,
                    500,
                    dialogueImageTowerUpgrade[0],
                    upgradeButton,
                    closeButton,
                    saleButton,
                    810,
                    750
            );

            Button tow1 = new Button("TowerType1",0,0,redTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE1;
                currentTowerSprite = redTower.get(0);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(selection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
                });
            });

            Button tow2 = new Button("TowerType2",0,0,blueTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE2;
                currentTowerSprite = blueTower.get(0);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(selection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
                });

            });

            Button tow3 = new Button("TowerType3",0,0,greenTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE3;
                currentTowerSprite = greenTower.get(0);
                renderer.consume(()->{
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                    selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(selection);
                });
            });

            selectTowerDialogue = new TowerSelectDialogue(
                    borderX - 300,
                    700,
                    200, 600,
                    deselection,
                    tow1,
                    tow2,
                    tow3
            );

            scene.addImageViews(towerUpgradeDialogue.getTowerUpgrade().getAllViews());
            scene.addImageViews(selectTowerDialogue.getSelectTowerDialogue().getAllViews());
            scene.addImageView(scoreBackGrView);

            renderer.consume(()->selectTowerDialogue.getSelectTowerDialogue().show());

            for (ImageView view : viewsNum)
                scene.addImageView(view);

            //grid views
            gridViewMatrix = new ImageView[rows][columns];

            for (int j = 0; j < rows; ++j ) {
                for (int i = 0; i < columns; ++i)
                    gridViewMatrix[j][i] = scene.addImageView(new ImageViewImpl("Gird [" + j + "][" + i +"]").hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3));
            }

            //enemy repo init
            enemyRepo = new QueuedEntityRepo<EnemyDoubleDaemon>() {
                @Override
                public void onAdd(EnemyDoubleDaemon enemy) {
                    enemy.setShootable(false);
                    renderer.consume(enemy.getHpView()::hide);
                    enemy.setVelocity(0);
                    activeEnemies.remove(enemy);
                    enemy.pushSprite(explodeSprite, 0, () -> {
                        renderer.consume(enemy.getView()::hide);
                        enemy.stop();
                        enemy.setCoordinates(grid.getStartingX(), grid.getStartingY());
                    });
                }

                @Override
                public void onGet(EnemyDoubleDaemon enemy) {
                    enemy.setShootable(true);
                    enemy.setCoordinates(grid.getStartingX(), grid.getStartingY());
                    enemy.setVelocity(
                            new ImageMover.Velocity(
                                    enemyVelocity,
                                    new ImageMover.Direction(1, 0)
                            )
                    );

                    renderer.consume(()->{
                        enemy.getView().show();
                        enemy.getHpView().show();
                    });

                    activeEnemies.add(enemy);
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
                    bullet.setVelocity(0);
                    bullet.pause();
                }

                @Override
                public void onGet(BulletDoubleDaemon bullet) {
                    System.out.println(DaemonUtils.tag() + "Bullet get state: " + bullet.getState());
                    renderer.consume(()->{
                        for (ImageView view : bullet.getViews())
                            view.show();
                    });
                }
            };

            //init enemies and fill enemy repo
            for (int i = 0; i < maxEnemies; ++i) {

                String enemyName = "Enemy instance no.: " + i;

                EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                        gameConsumer,
                        renderer,
                        new Enemy(
                                enemySprite,
                                enemyVelocity,
                                enemyHp,
                                Pair.create(grid.getStartingX(), grid.getStartingY())
                        ).setView(scene.addImageView(new ImageViewImpl(enemyName + " View").setImage(enemySprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3)))
                        .setHpView(scene.addImageView(new ImageViewImpl(enemyName + " HP View").setImage(enemySprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3)))
                        .setHealthBarImage(healthBarSprite)
                ).setName(enemyName);

                enemy.getPrototype().setBorders(
                        grid.getStartingX(),
                        (grid.getStartingX() + grid.getGridWidth()),
                        grid.getStartingY(),
                        (grid.getStartingY() + grid.getGridHeight())
                );

                enemy.setAnimateEnemySideQuest().setClosure(new MultiViewAnimateClosure()::onReturn);

                enemyRepo.getStructure().add(enemy);
            }

            //init bullets and fill bullet repo
            for (int i = 0; i < maxBullets; ++i) {

                String bulletName = "Bullet instance no. " + i;

                BulletDoubleDaemon bulletDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        renderer,
                        new Bullet(
                                /*bulletSprite,*/bulletSpriteRocket,
                                0,
                                Pair.create((float) 0, (float) 0),
                                bulletDamage
                        ).setView(scene.addImageView(new ImageViewImpl(bulletName + " View 1").setImage(bulletSpriteRocket[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(0)))
                        .setView2(scene.addImageView(new ImageViewImpl(bulletName + " View 2").setImage(bulletSpriteRocket[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(0)))
                        .setView3(scene.addImageView(new ImageViewImpl(bulletName + " View 3").setImage(bulletSpriteRocket[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(0)))
                ).setName(bulletName);

                bulletDoubleDaemon.getPrototype().setBorders(
                        - 50,//grid.getStartingX(),//TODO fix offset
                        (grid.getStartingX() + grid.getGridWidth()),
                        - 50,  //grid.getStartingY(),
                        (grid.getStartingY() + grid.getGridHeight())
                );

                bulletDoubleDaemon.setOutOfBordersConsumer(gameConsumer).setOutOfBordersClosure(()-> bulletRepo.add(bulletDoubleDaemon));
                bulletDoubleDaemon.setAnimateBulletSideQuest().setClosure(new MultiViewAnimateClosure()::onReturn);

                bulletRepo.getStructure().push(bulletDoubleDaemon);
            }

            //laser views init
            laserViews = new ArrayList<>(laserViewNo);

            for (int i = 0; i < laserViewNo; ++i)
                laserViews.add(scene.addImageView(new ImageViewImpl("laser View " + i).setImage(laserSprite[0]).hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(1)));

            //laser init
            laser = new LaserBulletDaemon(
                    gameConsumer,
                    renderer,
                    new LaserBullet(
                            laserSprite,
                            40,
                            Pair.create(0F, 0F),
                            bulletDamage
                    )
            );

            laser.setViews(laserViews);
            laser.setAnimateLaserSideQuest().setClosure(ret->{
                for (Pair<ImageView, ImageMover.PositionedImage> viewAndImage : ret.runtimeCheckAndGet()) {
                    viewAndImage.getFirst().setAbsoluteX(viewAndImage.getSecond().positionX);
                    viewAndImage.getFirst().setAbsoluteY(viewAndImage.getSecond().positionY);
                    viewAndImage.getFirst().setImage(viewAndImage.getSecond().image);
                }
            });

            //draw grid
            for(int j = 0; j < rows; ++j )
                for (int i = 0; i < columns; ++i) {
                    gridViewMatrix[j][i].setAbsoluteX(grid.getGrid()[j][i].getCenterX());
                    gridViewMatrix[j][i].setAbsoluteY(grid.getGrid()[j][i].getCenterY());
                    gridViewMatrix[j][i].setImage(grid.getField(j, i).isWalkable() ? fieldImage : fieldImageTower).hide();
                }

            //prepare the scene and start the renderer
            scene.lockViews();

            scene.forEach(view->{
                System.out.println(DaemonUtils.tag() +  view.getName());
                System.out.println(DaemonUtils.tag() +  "X: " + view.getAbsoluteX());
                System.out.println(DaemonUtils.tag() + "Y: " + view.getAbsoluteY());
                System.out.println(DaemonUtils.tag() + "Image: "  + view.getImage().toString());
                System.out.println(DaemonUtils.tag() + "Image imp: " + view.getImage().getImageImp().toString());

            });

            renderer.setScene(scene).start();

            chain.next();

        }).addState(()->{//gameState

            //laser start
            laser.start();

            //hide the grid at start and draw the score keeping dialogue
            renderer.consume(()->{
                infoScore = new InfoTable(
                        borderX - scoreBackGrImage.getWidth(),
                        250,
                        scoreBackGrView,
                        scoreTitleView,
                        viewsNum,
                        scorenumbersImages
                ).setNumbers(0);
            });

            //get grids first field
            Field firstField = grid.getField(0, 0);

            //init enemy generator
            enemyGenerator = DummyDaemon.create(gameConsumer, enemyGenerateinterval).setClosure(ret->{

                enemyCounter++;

                //every ... enemies increase the pain!!!!
                if (enemyCounter % 3 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

                    if (enemyGenerateinterval > 1000)
                        enemyGenerateinterval -= 500;

                    if (enemyCounter % 15 == 0 && waveInterval > 2000) //TODO fix this!
                        waveInterval -= 2000;

                    enemyHp++;
                    enemyGenerator.setSleepInterval(waveInterval);//TODO set long as param in DaemonGenerators

                } else {
                    enemyGenerator.setSleepInterval(enemyGenerateinterval);
                }

                if (enemyCounter % 20 == 0 && bulletDamage < 10)
                    bulletDamage += 1;

                EnemyDoubleDaemon enemyDoubleDaemon = enemyRepo.getAndConfigure(enemy->{
                    enemy.setMaxHp(enemyHp);
                    enemy.setHp(enemyHp);
                });

                System.out.println(DaemonUtils.tag() + "Enemy counter: " + enemyCounter);
                System.out.println(DaemonUtils.tag() + "Enemy repo size: " + enemyRepo.size());
                System.out.println(DaemonUtils.tag() + "Enemy state: " + enemyDoubleDaemon.getState());

                int angle = (int) RotatingSpriteImageMover.getAngle(
                        enemyDoubleDaemon.getLastCoordinates().getFirst(),
                        enemyDoubleDaemon.getLastCoordinates().getSecond(),
                        firstField.getCenterX(),
                        firstField.getCenterY()
                );

                enemyDoubleDaemon.start();

                enemyDoubleDaemon.rotate(angle);

                enemyDoubleDaemon.goTo(firstField.getCenterX(), firstField.getCenterY(), enemyVelocity,
                        new Runnable() {// gameConsumer
                            @Override
                            public void run() {

                                Pair<Float, Float> currentCoord = enemyDoubleDaemon.getPrototype().getLastCoordinates();
                                Field current = grid.getField(currentCoord.getFirst(), currentCoord.getSecond());

                                for(Field neighbour : grid.getNeighbors(current)) {
                                    if (neighbour.getTower() != null)
                                        neighbour.getTower().addTarget(enemyDoubleDaemon);
                                }

                                //show enemy progress on grid
                                renderer.consume(()->gridViewMatrix[current.getRow()][current.getColumn()].show());

                                //if enemy reaches last field
                                if (current.getColumn() == columns - 1 && current.getRow() == rows - 1) {
                                    if (score > 0)
                                        renderer.consume(()-> infoScore.setNumbers(--score));
                                    enemyRepo.add(enemyDoubleDaemon);
                                    return;
                                }

                                //go to next fields center
                                Field next = grid.getMinWeightOfNeighbors(current);
                                enemyDoubleDaemon.rotate(
                                        (int) RotatingSpriteImageMover.getAngle(
                                                current.getCenterX(),
                                                current.getCenterY(),
                                                next.getCenterX(),
                                                next.getCenterY()
                                        )
                                );

                                enemyDoubleDaemon.goTo(next.getCenterX(), next.getCenterY(), enemyVelocity, this::run);
                            }
                        }
                );
            });

            //start enemy generatorh
            enemyGenerator.setName("Enemy Generator").start();

//            backgroundMover.start();
        });
    }

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
                boolean consumed = renderer.consume(()->{

                    towerUpgradeDialogue.getTowerUpgrade()
                            .setAbsoluteX(borderX / 2)
                            .setAbsoluteY(borderY / 2);

                    towerUpgradeDialogue.getTowerUpgrade().getViewByName("TowerView")
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
                renderer.consume(()->fieldView.setImage(fieldImageTowerDen).show());
            } else {

                renderer.consume(()->fieldView.setImage(currentTowerSprite[0]).show());

                TowerDaemon towerDaemon = new TowerDaemon(
                        gameConsumer,
                        //drawConsumer,
                        renderer,
                        new Tower(
                                currentTowerSprite,
                                Pair.create(field.getCenterX(), field.getCenterY()),
                                range,
                                towerSelect
                        )
                ).setName("Tower[" + field.getColumn() + "][" + field.getRow() + "]");

                towerDaemon.setView(fieldView);

                towers.add(towerDaemon);
                field.setTower(towerDaemon);

                towerDaemon.setAnimateSideQuest().setClosure(new ImageAnimateClosure(fieldView)::onReturn);
                towerDaemon.start();

                towerDaemon.scan(new Closure<Pair<Tower.TowerType, EnemyDoubleDaemon>>() {
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

                                    fireLaser(towerDaemon.getLastCoordinates(), enemy, 300);
                                    reloadInterval = 1000;
                                    break;
                                default:
                                    throw new IllegalStateException("Tower type does not exist!");
                            }
                        }

                        towerDaemon.reload(reloadInterval, ()->towerDaemon.scan(this::onReturn));
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
        System.out.println(DaemonUtils.tag() + "Bullet queue size: " + bulletRepo.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletRepo.configureAndGet(bullet -> {
            bullet.setCoordinates(sourceCoord.getFirst(), sourceCoord.getSecond());
            bullet.setLevel(noOfBulletsFired);
            bullet.setDamage(bulletDamage);
            bullet.setSprite(bulletSprite);
        });

        if (bulletDoubleDaemon.getState().equals(DaemonState.STOPPED))
            bulletDoubleDaemon.start();
        else
            bulletDoubleDaemon.cont();

        bulletDoubleDaemon.goTo(targetCoord.getFirst(), targetCoord.getSecond(), velocity, () -> {

            if (!enemy.isShootable()) {
                bulletRepo.add(bulletDoubleDaemon);
                return;
            }

            int newHp = enemy.getHp() - bulletDoubleDaemon.getPrototype().getDamage();
            if (newHp > 0) {
                enemy.setHp(newHp);
            } else {
                renderer.consume(()->infoScore.setNumbers(++score));
                enemyRepo.add(enemy);
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
        System.out.println(DaemonUtils.tag() + "Bullet stack size: " + bulletRepo.size());

        BulletDoubleDaemon rocketDoubleDaemon = bulletRepo.configureAndGet(rocket->{
            rocket.setCoordinates(sourceCoord.getFirst(), sourceCoord.getSecond());
            rocket.setLevel(noOfBulletsFired);
            rocket.setDamage(bulletDamage);
            rocket.setSprite(bulletSpriteRocket);
        });

        int launchX = getRandomInt((int)(sourceCoord.getFirst() - 50), (int)(sourceCoord.getFirst() + 50));
        int launchY = getRandomInt((int)(sourceCoord.getSecond() - 50), (int)(sourceCoord.getSecond() + 50));

        int angle = (int) RotatingSpriteImageMover.getAngle(
                sourceCoord.getFirst(),
                sourceCoord.getSecond(),
                launchX,
                launchY
        );

        if (rocketDoubleDaemon.getState().equals(DaemonState.STOPPED))
            rocketDoubleDaemon.start();
        else
            rocketDoubleDaemon.cont();

        rocketDoubleDaemon.rotateAndGoTo(angle, launchX, launchY, 4, () -> {

            if (!enemy.isShootable()) {
                bulletRepo.add(rocketDoubleDaemon);
                return;
            }

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
                    ()->{

                        if (!enemy.isShootable()){
                            bulletRepo.add(rocketDoubleDaemon);
                            return;
                        }

                        float bulletX = rocketDoubleDaemon.getLastCoordinates().getFirst();
                        float bulletY = rocketDoubleDaemon.getLastCoordinates().getSecond();

                        if (Math.abs(bulletX - enemy.getLastCoordinates().getFirst()) > rocketExplosionRange
                                && Math.abs(bulletY - enemy.getLastCoordinates().getSecond()) > rocketExplosionRange) {
                            return;
                        }

                        int newHp = enemy.getHp() - rocketDoubleDaemon.getDamage();

                        if (newHp > 0) {
                            enemy.setHp(newHp);
                        } else {
                            renderer.consume(()->infoScore.setNumbers(++score));
                            enemyRepo.add(enemy);
                        }

                        rocketDoubleDaemon.pushSprite(miniExplodeSprite, 0, ()->bulletRepo.add(rocketDoubleDaemon));
                    });
        });
    }

    public void fireLaser(Pair<Float, Float> source, EnemyDoubleDaemon enemy, long duration) {
        laser.desintegrateTarget(source, enemy, duration, renderer/*drawConsumer*/, ret->{
            int newHp = enemy.getHp() - laser.getDamage();
            if (newHp > 0) {
                enemy.setHp(newHp);
            } else {
                renderer.consume(()->infoScore.setNumbers(++score));
                enemyRepo.add(enemy);
            }
        });
    }
}