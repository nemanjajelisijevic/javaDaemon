package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.RotatingSpriteImageMover;

import com.daemonize.daemondevapp.images.Image;

import com.daemonize.daemondevapp.renderer.DrawConsumer;
import com.daemonize.daemondevapp.renderer.Renderer2D;
import com.daemonize.daemondevapp.repo.EntityRepo;
import com.daemonize.daemondevapp.repo.QueuedEntityRepo;
import com.daemonize.daemondevapp.repo.StackedEntityRepo;
import com.daemonize.daemondevapp.scene.Scene2D;

import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;

import com.daemonize.daemondevapp.view.Button;
import com.daemonize.daemondevapp.view.CompositeImageViewImpl;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemondevapp.view.ImageViewImpl;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;

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

    //state holder
    private DaemonChainScript chain = new DaemonChainScript();

    //Scene
    private Scene2D scene;

    //BackgroundImage
    private Image backgroundImage;

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

    public Game(Renderer2D renderer, int rows, int columns, float x, float y, int fieldWidth) {
        this.renderer = renderer;
        this.scene = new Scene2D();
        this.gameConsumer = new DaemonConsumer("Game Consumer");
        this.rows = rows;
        this.columns = columns;
        this.grid = new Grid(
                rows,
                columns,
                Pair.create(0, 0),
                Pair.create(rows - 1, columns - 1),
                x,
                y,
                fieldWidth
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
                    if (towerSelect != null )Log.w("SelectTower",towerSelect.toString());
                }

                if (towerSelect == null ){
                    Log.w("Select","please select tower");
                } else {
                    setTower(x, y);
                }
            }
        });
        return this;
    }

    private ImageView backgroundView;

//    private DummyDaemon backgroundMover;
//
//    private enum Dir {
//        RIGHT,
//        DOWN,
//        LEFT,
//        UP;
//
//        private static Dir[] vals = values();
//
//        public Dir next()
//        {
//            return vals[(this.ordinal()+1) % vals.length];
//        }
//    }
//
//    private Dir currentDir = Dir.RIGHT;
//    private int pixelsMoved = 0;

    {
        //init state
        chain.addState(()-> {

            //add background to scene
            backgroundView = scene.addImageView(new ImageViewImpl("Background").setImageWithoutOffset(backgroundImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(0).show());

//            backgroundMover = DummyDaemon.create(gameConsumer, 25).setClosure(aVoid->{
//
//                int dX;
//                int dY;
//
//                if (pixelsMoved >= (currentDir.equals(Dir.RIGHT) || currentDir.equals(Dir.LEFT) ? borderX  : borderY)) {
//                    pixelsMoved = 0;
//                    currentDir = currentDir.next();
//                }
//
//                switch(currentDir) {
//                    case RIGHT:
//                        dX = -1;
//                        dY = 0;
//                        break;
//                    case DOWN:
//                        dX = 0;
//                        dY = -1;
//                        break;
//                    case LEFT:
//                        dX = 1;
//                        dY = 0;
//                        break;
//                    case UP:
//                        dX = 0;
//                        dY = 1;
//                        break;
//                    default:
//                        dX = 0;
//                        dY = 0;
//                        break;
//                }
//
//                renderer.consume(()->{
//                    backgroundView.setAbsoluteX(backgroundView.getAbsoluteX() + dX);
//                    backgroundView.setAbsoluteY(backgroundView.getAbsoluteY() + dY);
//                });
//                pixelsMoved ++;
//
//            });

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
                    Log.d(DaemonUtils.tag(), "Bullet get state: " + bullet.getState());
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
            for(int j = 0; j < rows; ++j ) {
                for (int i = 0; i < columns; ++i) {
                    gridViewMatrix[j][i].setAbsoluteX(grid.getGrid()[j][i].getCenterX());
                    gridViewMatrix[j][i].setAbsoluteY(grid.getGrid()[j][i].getCenterY());
                    gridViewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower).hide();
                }
            }

            //prepare the scene and start the renderer
            scene.lockViews();

            scene.forEach(view->{
                Log.d(DaemonUtils.tag(), view.getName());
                Log.d(DaemonUtils.tag(), "X: " + view.getAbsoluteX());
                Log.d(DaemonUtils.tag(),"Y: " + view.getAbsoluteY());
                Log.d(DaemonUtils.tag(),"Image: "  + view.getImage().toString());
                Log.d(DaemonUtils.tag(),"Image imp: " + view.getImage().getImageImp().toString());

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

                Log.d(DaemonUtils.tag(), "Enemy counter: " + enemyCounter);
                Log.d(DaemonUtils.tag(), "Enemy repo size: " + enemyRepo.size());
                Log.d(DaemonUtils.tag(), "Enemy state: " + enemyDoubleDaemon.getState());

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

            //start enemy generator
            enemyGenerator.start();

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
                renderer.consume(()->{

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
        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletRepo.size());

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
        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletRepo.size());

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

    //Game setters
    public Game setBorders(int x, int y) {
        this.borderX = x;
        this.borderY = y;
        return this;
    }

    public Game setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    public Game setSelectionImage(Image selection) {
        this.selection = selection;
        return this;
    }

    public Game setDeselectionImage(Image deselection) {
        this.deselection= deselection;
        return this;
    }


    public Game setExplodeSprite(Image[] explodeSprite) {
        this.explodeSprite = explodeSprite;
        return this;
    }

    public Game setMiniExplodeSprite(Image[] miniExplodeSprite) {
        this.miniExplodeSprite = miniExplodeSprite;
        return this;
    }

    public Game setBulletSprite(Image[] bulletSprite) {
        this.bulletSprite = bulletSprite;
        return this;
    }
    public Game setBulletSpriteRocket(Image[] bulletSpriteRocket) {
        this.bulletSpriteRocket = bulletSpriteRocket;
        return this;
    }

    public Game setEnemySprite(Image[] enemySprite) {
        this.enemySprite = enemySprite;
        return this;
    }

    public Game setFieldImage(Image fieldImage) {
        this.fieldImage = fieldImage;
        return this;
    }

    public Game setFieldImageTower(Image fieldImageTower) {
        this.fieldImageTower = fieldImageTower;
        return this;
    }

    public Game setFieldImageTowerDen(Image fieldImageTowerDen) {
        this.fieldImageTowerDen = fieldImageTowerDen;
        return this;
    }

    public Game setHealthBarSprite(Image[] healthBarSprite) {
        this.healthBarSprite = healthBarSprite;
        return this;
    }

    public Game setUpgradeButtonImage(Image upgradeButtonImage) {
        this.upgradeButtonImage = upgradeButtonImage;
        return this;
    }

    public Game setCloseButtonImage(Image closeButtonImage) {
        this.closeButtonImage = closeButtonImage;
        return this;
    }

    public Game setSaleButtonImage(Image saleButtonImage) {
        this.saleButtonImage = saleButtonImage;
        return this;
    }

    public Game setScoreBackGrImage(Image scoreBackGrImage) {
        this.scoreBackGrImage = scoreBackGrImage;
        return this;
    }

    public Game setScorenumbersImages(Image[] scorenumbersImages) {
        this.scorenumbersImages = scorenumbersImages;
        return this;
    }

    public Game setLaserSprite(Image[] laserSprite) {
        this.laserSprite = laserSprite;
        return this;
    }

    public Game setBlueTower(Image[] towerI, Image[] towerII, Image[] towerIII) {
        blueTower = new ArrayList<>(3);
        blueTower.add(towerI);
        blueTower.add(towerII);
        blueTower.add(towerIII);
        return this;
    }

    public Game setGreenTower(Image[] towerI,Image[] towerII,Image[] towerIII){
        greenTower = new ArrayList<>(3);
        greenTower.add(towerI);
        greenTower.add(towerII);
        greenTower.add(towerIII);
        return this;
    }

    public Game setRedTower(Image[] towerI,Image[] towerII,Image[] towerIII){
        redTower = new ArrayList<>(3);
        redTower.add(towerI);
        redTower.add(towerII);
        redTower.add(towerIII);
        return this;
    }

    public Game setUpgradeTowerDialogue(Image[] upgDialogTowerI,Image[] upgDialogTowerII,Image[] upgDialogTowerIII){
        redTowerUpgSprite = upgDialogTowerI;
        blueTowerUpgSprite = upgDialogTowerII;
        greenTowerUpgSprite = upgDialogTowerIII;
        dialogueImageTowerUpgrade = redTowerUpgSprite;
        return this;
    }
}