package com.daemonize.daemondevapp;

//import android.util.Log;

import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.RotatingSpriteImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.renderer.Renderer2D;
import com.daemonize.daemondevapp.scene.Scene2D;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.view.Button;
import com.daemonize.daemondevapp.view.CompositeImageViewImpl;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemondevapp.view.ImageViewImpl;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;


public class Game {

    //game threads
    private Renderer2D renderer;
    private DaemonConsumer gameConsumer = new DaemonConsumer("Game Consumer");
    private DaemonConsumer drawConsumer = new DaemonConsumer("Draw Consumer");

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
    private GenericNode<ImageView> dialogue;

    //score
    private int score = 0;
    private ImageView scoreBackGrView;
    private ImageView scoreTitleView;
    private ImageView[] viewsNum;
    private InfoTable infoScore;

    private Image fieldImage;
    private Image fieldImageTower;
    private Image fieldImageTowerDen;
    private Image dialogueImage;
    private Image [] dialogueImageTowerUpgradeLevel;
    private Image upgradeButtonImage;
    private Image closeButtonImage;
    private Image nestedRedDialogueImage;
//    private Image greenDialogueImage;
    private Image scoreBackGrImage;
    private Image scoreTitle;
    private Image[] scorenumbersImages;

    private boolean pause;

    //towers
    private List<TowerDaemon> towers = new ArrayList<>();
    private Queue<EnemyDoubleDaemon> enemyQueue = new LinkedList<>();
    private int towerShootInterval = 1500;
    private int range = 250;
    private Tower.TowerType towerSelect;

    private Image[] currentTowerSprite;

    private List<Image[]> redTower;
    private List<Image[]> blueTower;
    private List<Image[]> greenTower;


    //towers dialogue
    private TowerScanClosure towerScanClosure;
    private TowerUpgradeDialog towerUpgradeDialog;
    private TowerSelectDialogue selectTowerDialogue;
//    private Image selectTowerBackgroudnImage;
    private Image selection;
    private Image deselection;

    //enemies
    //private ActiveEntitySet<EnemyDoubleDaemon> activeEnemies = new ActiveEntitySet();
    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();
    private Image[] enemySprite;
    private Image[] healthBarSprite;

    //explosions
    private Image[] explodeSprite;
    private Image[] miniExplodeSprite;

    private DummyDaemon enemyGenerator;
    private DummyDaemon levelGenerator;
    private long enemyCounter = 0;
    private float enemyVelocity = 1;
    private int enemyHp = 10;
    private long enemyGenerateinterval = 5000;
    private long waveInterval = 20000;
    private long levelGenerateinterval = 5000;

    //bullets
    private Queue<BulletDoubleDaemon> bulletQueue = new LinkedList<>();
    private Image[] bulletSprite;
    private Image[] bulletSpriteLaser;
    private int bulletDamage = 2;

    //laser
    private LaserBulletDaemon laser;
    private List<ImageView> laserViews;
    private Image[] laserSprite;
    private int laserViewNo = 50;

    //closures
    private class ImageAnimateClosure implements Closure<ImageMover.PositionedImage> {

        private ImageView view;

        public ImageAnimateClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage> aReturn) {
            ImageMover.PositionedImage posBmp = aReturn.uncheckAndGet();
            view.setAbsoluteX(posBmp.positionX);
            view.setAbsoluteY(posBmp.positionY);
            view.setImage(posBmp.image);
        }
    }

    private class MultiViewAnimateClosure implements Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
        @Override
        public void onReturn(Return<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> aReturn) {
            GenericNode.forEach(aReturn.uncheckAndGet(), actionret -> {
                Pair<ImageMover.PositionedImage, ImageView> imageAndView = actionret.uncheckAndGet();
                imageAndView.getSecond().setAbsoluteX(imageAndView.getFirst().positionX);
                imageAndView.getSecond().setAbsoluteY(imageAndView.getFirst().positionY);
                imageAndView.getSecond().setImage(imageAndView.getFirst().image);
            });
        }
    }

    public class TowerScanClosure implements Closure<Pair<Tower.TowerType, EnemyDoubleDaemon>> {

        private TowerDaemon tower;

        public TowerScanClosure(TowerDaemon tower) {
            this.tower = tower;
        }

        @Override
        public void onReturn(Return<Pair<Tower.TowerType, EnemyDoubleDaemon>> aReturn) {

            long reloadInterval = tower.getTowerLevel().reloadInterval;

            if (aReturn.uncheckAndGet() != null
                    && aReturn.uncheckAndGet().getFirst() != null
                    && aReturn.uncheckAndGet().getSecond() != null) {

                switch (aReturn.get().getFirst()) {
                    case TYPE1:
                        fireBullet(
                                tower.getLastCoordinates(),
                                aReturn.uncheckAndGet().getSecond().getLastCoordinates(),
                                aReturn.uncheckAndGet().getSecond(),
                                25,
                                1, //tower.getTowerLevel().bulletDamage,
                                tower.getTowerLevel().currentLevel
                        );
                        break;
                    case TYPE2:
                        fireRocketBullet(
                                tower.getLastCoordinates(),
                                aReturn.uncheckAndGet().getSecond(),
                                18,
                                tower.getTowerLevel().bulletDamage,
                                tower.getTowerLevel().currentLevel
                        );
                        break;
                    case TYPE3:
                        fireLaser(tower.getLastCoordinates(), aReturn.get().getSecond(), 300);
                        reloadInterval = 1000;
                        break;
                    default:
                        throw new IllegalStateException("Tower type does not exist!");
                }
            }

            //tower.scan(this::onReturn);

            tower.reload(reloadInterval, aReturn1 -> { // this method should name reload, after reloading we get the current list of active enemies, and scan over this list
                //tower.scan(new ArrayList<>(activeEnemies), this);
                tower.scan(this::onReturn);
            });
        }
    }

    private static int getRandomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public Game(Renderer2D renderer, int rows, int columns, float x, float y, int fieldWidth) {
        this.renderer = renderer;
        this.scene = new Scene2D();
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

    public void pauseAll() {
        pause = true;
        enemyGenerator.stop();
        for (EnemyDoubleDaemon enemy : new ArrayList<>(activeEnemies)) {
            enemy.pause();
        }
        for (TowerDaemon tower : towers) {
            tower.pause();
        }
    }

    public void contAll() { //continueAll
        pause = false;
        enemyGenerator.start();
        for (EnemyDoubleDaemon enemy : new ArrayList<>(activeEnemies)) {
            enemy.cont();
        }
        for (TowerDaemon tower : towers) {
            tower.cont();
        }
    }

    public Game run() {
        drawConsumer.start();
        gameConsumer.start();
        gameConsumer.consume(()->chain.run());
        return this;
    }

    public Game stop(){
        enemyGenerator.stop();
        for(EnemyDoubleDaemon enemy : new ArrayList<>(activeEnemies)) enemy.stop();
        for (TowerDaemon tower : towers) tower.stop();
        laser.stop();
        drawConsumer.stop();
        gameConsumer.stop();
        scene.unlockViews();
        renderer.stop();
        return this;
    }

    public Game onTouch(float x, float y) {//TODO use root dialogs only!!!!
        gameConsumer.consume(()->{
            if (towerUpgradeDialog.getTowerUpgrade().isShowing()){
                towerUpgradeDialog.getTowerUpgrade().checkCoordinates(x, y);
            } else {
                if (selectTowerDialogue.getSelectTowerDialogue().isShowing()){
                   selectTowerDialogue.getSelectTowerDialogue().checkCoordinates(x,y);
                    if (towerSelect != null )Log.w("SelectTower",towerSelect.toString());
                }
                if (towerSelect == null ){
                    Log.w("Select","please select tower");
                    //show dialoge for selecting tower
                } else {
                    setTower(x, y);
                }
            }
        });
        return this;
    }

    {
        //init state
        chain.addState(()-> {

            scene.addImageView(new ImageViewImpl().setImageWithoutOffset(backgroundImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(0).show());

            dialogue = new GenericNode<>(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3)), "TEST DIALOGUE");
            dialogue.addChild(new GenericNode<>(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(4)), "KILL DIALOGUE BUTTON"));

            scoreBackGrView = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(3).show();

            scoreTitleView = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(4).show();

            viewsNum = new ImageView[5];
            viewsNum[0] = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[1] = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[2] = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[3] = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();
            viewsNum[4] = new ImageViewImpl().setAbsoluteX(0).setAbsoluteY(0).setZindex(5).show();

            Button upgradeButton = new Button("Upgrade", 0, 0, upgradeButtonImage).onClick(()->{

                Tower tow = towerUpgradeDialog.getTower();
                tow.levelUp();
                Image[] currentSprite = null;
                switch (tow.getTowertype()){
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

                CompositeImageViewImpl towerView = towerUpgradeDialog.getTowerUpgrade().getViewByName("TowerView");

                if (towerView == null)
                    throw new IllegalStateException("towerView == null");

                drawConsumer.consume(()->towerView.setImage(dialogueImageTowerUpgradeLevel[tow.getTowerLevel().currentLevel - 1]));
                if (score > 2 && tow.getTowerLevel().currentLevel < 3)
                    towerUpgradeDialog.getTowerUpgrade().getViewByName("Upgrade").show();
                else
                    towerUpgradeDialog.getTowerUpgrade().getViewByName("Upgrade").hide();
//                drawConsumer.consume(()->towerUpgradeDialog.getTowerUpgrade().getViewByName("Upgrade").hide());
                score -= 2;
                drawConsumer.consume(()->infoScore.setNumbers(score));

            });

            Button closeButton = new Button("Close", 0, 0, closeButtonImage).onClick(()->{
                //contAll();
                drawConsumer.consume(()->towerUpgradeDialog.getTowerUpgrade().hide());
            });

            towerUpgradeDialog =  new TowerUpgradeDialog(700,500,
                   dialogueImageTowerUpgradeLevel[0], upgradeButton, closeButton, 810, 600 );//.setOnUpgrade(()->{

            Button tow1 = new Button("TowerType1",0,0,redTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE1;
                currentTowerSprite = redTower.get(0);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(selection);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
            });

            Button tow2 = new Button("TowerType2",0,0,blueTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE2;
                currentTowerSprite = blueTower.get(0);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(selection);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(deselection);
            });

            Button tow3 = new Button("TowerType3",0,0,greenTower.get(0)[0]).onClick(()->{
                towerSelect = Tower.TowerType.TYPE3;
                currentTowerSprite = greenTower.get(0);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower1").setImage(deselection);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower2").setImage(deselection);
                selectTowerDialogue.getSelectTowerDialogue().getViewByName("Tower3").setImage(selection);
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

            scene.addImageViews(towerUpgradeDialog.getTowerUpgrade().getAllViews());
            scene.addImageViews(selectTowerDialogue.getSelectTowerDialogue().getAllViews());
            scene.addImageView(scoreBackGrView);
            scene.addImageView(scoreTitleView);

            selectTowerDialogue.getSelectTowerDialogue().show();

            for (ImageView view : viewsNum) {
                scene.addImageView(view);
            }

            gridViewMatrix = new ImageView[rows][columns];

            for(int j = 0; j < rows; ++j ) {
                for (int i = 0; i < columns; ++i) {
                    gridViewMatrix[j][i] = scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3));
                }
            }

            Field firstField = grid.getField(0, 0);

            for (int i = 0; i < 200; ++i) {

                EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                        gameConsumer,
                        drawConsumer,
                        new Enemy(
                                enemySprite,
                                enemyVelocity,
                                enemyHp,
                                Pair.create((float) 0, (float) 0),
                                Pair.create(firstField.getCenterX(), firstField.getCenterY())
                        ).setView(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3)))
                                .setHpView(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(3)))
                                .setHealthBarImage(healthBarSprite)
                ).setName("Enemy no. " + i);

                enemy.getPrototype().setBorders(
                        grid.getStartingX(),
                        (grid.getStartingX() + grid.getGridWidth()),
                        grid.getStartingY(),
                        (grid.getStartingY() + grid.getGridHeight())
                );

                enemy.setAnimateEnemySideQuest().setClosure(new MultiViewAnimateClosure());//gui consumer

                enemyQueue.add(enemy);

                BulletDoubleDaemon bulletDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        drawConsumer,
                        new Bullet(
                                /*bulletSprite,*/bulletSpriteLaser,
                                0,
                                Pair.create((float) 0, (float) 0),
                                Pair.create((float) 0, (float) 0),
                                bulletDamage
                        ).setView(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(0)))
                        .setView2(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(0)))
                        .setView3(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(0)))
                ).setName("Bullet no. " + i);

                bulletDoubleDaemon.getPrototype().setBorders(
                        grid.getStartingX(),
                        (grid.getStartingX() + grid.getGridWidth()),
                        grid.getStartingY(),
                        (grid.getStartingY() + grid.getGridHeight())
                );

                bulletDoubleDaemon.setAnimateBulletSideQuest().setClosure(new MultiViewAnimateClosure()::onReturn);

                bulletQueue.add(bulletDoubleDaemon);

            }

            laserViews = new ArrayList<>(laserViewNo);

            for (int i = 0; i < laserViewNo; ++i) {
                laserViews.add(scene.addImageView(new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(1)));
            }

            laser = new LaserBulletDaemon(
                    gameConsumer,
                    drawConsumer,
                    new LaserBullet(
                            laserSprite,
                            40,
                            Pair.create(0F, 0F),
                            Pair.create(0F, 0F),
                            bulletDamage
                    )
            );

            laser.setViews(laserViews);
            laser.setAnimateLaserSideQuest().setClosure(ret->{
                for (Pair<ImageView, ImageMover.PositionedImage> viewAndImage : ret.uncheckAndGet()) {
                    viewAndImage.getFirst().setAbsoluteX(viewAndImage.getSecond().positionX);
                    viewAndImage.getFirst().setAbsoluteY(viewAndImage.getSecond().positionY);
                    viewAndImage.getFirst().setImage(viewAndImage.getSecond().image);
                }
            });

            scene.lockViews();
            renderer.setScene(scene).start();

            chain.next();

        }).addState(()->{//gameState

            laser.start();

            drawConsumer.consume(()->{
                for(int j = 0; j < rows; ++j ) {
                    for (int i = 0; i < columns; ++i) {
                        gridViewMatrix[j][i].setAbsoluteX(grid.getGrid()[j][i].getCenterX());
                        gridViewMatrix[j][i].setAbsoluteY(grid.getGrid()[j][i].getCenterY());
                        gridViewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower).hide();
                    }
                }

                scoreTitleView.setImage(scoreTitle);
                scoreBackGrView.setImage(scoreBackGrImage);
                infoScore = new InfoTable(
                        borderX - scoreBackGrImage.getWidth(),
                        250,
                        scoreBackGrView,
                        scoreTitleView,
                        viewsNum,
                        scorenumbersImages
                ).setNumbers(00000);

            });
            //scoreBackGrView.setAbsoluteX(borderX - scoreBackGrImage.getWidth());
            Field firstField = grid.getField(0, 0);

            enemyGenerator = new DummyDaemon(gameConsumer, enemyGenerateinterval).setClosure(ret->{

                enemyCounter++;

                //Log.d(DaemonUtils.tag(), "Enemy queue size: " + enemyQueue.size());

                //every 15 enemies increase the pain!!!!
                if (enemyCounter % 3 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

                    if (enemyGenerateinterval > 1000)
                        enemyGenerateinterval -= 500;

                    if (enemyCounter % 15 == 0 && waveInterval > 2000){
                        waveInterval -= 2000;
                    }

                    enemyHp++;
                    enemyGenerator.setSleepInterval((int)waveInterval);//TODO set long as param in DaemonGenerators

                } else {
                    enemyGenerator.setSleepInterval((int)enemyGenerateinterval);
                }

                if (enemyCounter % 20 == 0) {
                    if (bulletDamage < 10)
                        bulletDamage += 1;
                }

                EnemyDoubleDaemon enemy = enemyQueue.poll();
                enemy.setName("Enemy no." + enemyCounter);
                enemy.setMaxHp(enemyHp);
                enemy.setHp(enemyHp);
                //enemy.getPrototype().setVelocity(enemyVelocity);
                enemy.setVelocity(new ImageMover.Velocity(enemyVelocity, new ImageMover.Direction(0, 0)));// todo maybe coeficient should be grid.first fild center
                enemy.rotate(0, ret1->{});

                drawConsumer.consume(()->enemy.getView().show());
                drawConsumer.consume(()->enemy.getHpView().show());
                activeEnemies.add(enemy); // todo why add enemy here and same 4 lines below
                enemy.setShootable(true);
                enemy.start();

                //activeEnemies.add(enemy); //

                int angle = (int) RotatingSpriteImageMover.getAngle(enemy.getLastCoordinates().getFirst(), enemy.getLastCoordinates().getSecond(), firstField.getCenterX(), firstField.getCenterY());
                enemy.rotate(angle, ret1->{});

                //enemy.rotateTowards(firstField.getCenterX(), firstField.getCenterY(), ret1->{});

                enemy.goTo(firstField.getCenterX(), firstField.getCenterY(), enemyVelocity,
                        new Closure<Boolean>() {// gameConsumer
                            @Override
                            public void onReturn(Return<Boolean> aReturn) {

                                Pair<Float, Float> currentCoord = enemy.getPrototype().getLastCoordinates();
                                Field current = grid.getField(currentCoord.getFirst(), currentCoord.getSecond());

                                if (current == null) {
                                    Log.e("MARKO ","coord x: "+currentCoord.getFirst()+", coord y: "+currentCoord.getSecond());
                                }

                                List<Field> neighbours = grid.getNeighbors(current);
                                for(Field neighbour : neighbours) {
                                    if (neighbour.getTower() != null) {
                                        neighbour.getTower().addTarget(enemy);
                                    }
                                }

                                if (current == null) return;//throw new IllegalStateException("Field can not be null");
                                else if (current.getColumn() == columns - 1 && current.getRow() == rows - 1) {
                                    //explode in  end .
                                    enemy.setVelocity(new ImageMover.Velocity(0, enemy.getVelocity().direction));
                                    enemy.setShootable(false);
                                    drawConsumer.consume(()-> enemy.getHpView().hide());

                                    if (score > 0)
                                        drawConsumer.consume(()-> infoScore.setNumbers(--score));

                                    enemy.pushSprite(explodeSprite, 0,  aReturn2-> {
                                        enemy.stop();
                                        drawConsumer.consume(() -> enemy.getView().hide());
                                        activeEnemies.remove(enemy);
                                        enemy.setLastCoordinates(grid.getStartingX(),grid.getStartingY()); // ToDO maybe this causes current = null !!!!!
                                        if (!enemyQueue.contains(enemy))
                                            enemyQueue.add(enemy);
                                    });
                                }

                                drawConsumer.consume(()->gridViewMatrix[current.getRow()][current.getColumn()].show());

                                Field next = grid.getMinWeightOfNeighbors(current);
                                int angle = (int) RotatingSpriteImageMover.getAngle(current.getCenterX(), current.getCenterY(), next.getCenterX(), next.getCenterY());
                                enemy.setVelocity(new ImageMover.Velocity(3, enemy.getVelocity().direction));
                                enemy.rotate(angle, ret-> {});

                                enemy.goTo(next.getCenterX(), next.getCenterY(), enemyVelocity, this::onReturn);
                            }
                        }
                );

            });

            enemyGenerator.start();

//            activeEnemies.setOnDepleted(()->{
//                for (TowerDaemon tower: towers)
//                    tower.pauseScan();
//            });
//
//            activeEnemies.setOnFirstAdded(()->{
//                for (TowerDaemon tower: towers)
//                    tower.contScan();
//            });

            //try to garbage collect
            new DummyDaemon(gameConsumer, 3000).setClosure(aReturn -> System.gc()).start();

        });

    }

    public void setTower(float x, float y) {

        Field field = grid.getField(x, y);
        if (field == null) return;

        //upgrade existing tower
        TowerDaemon tow = field.getTower();
        if (tow != null) {

            if (!towerUpgradeDialog.getTowerUpgrade().isShowing()) {

                //pauseAll();
                Tower.TowerLevel currLvl = tow.getTowerLevel();
                towerUpgradeDialog.setTower(tow.getPrototype());
                boolean hasSkillsToPayTheBills = score > 3;

                drawConsumer.consume(()->{
                    //towerUpgradeDialog.getTowerUpgrade().setAbsoluteX(tow.getPrototype().getLastCoordinates().getFirst());
                    //towerUpgradeDialog.getTowerUpgrade().setAbsoluteY(tow.getPrototype().getLastCoordinates().getSecond());

                    towerUpgradeDialog.getTowerUpgrade().setAbsoluteX(borderX / 2);
                    towerUpgradeDialog.getTowerUpgrade().setAbsoluteY(borderY / 2);

                    towerUpgradeDialog.getTowerUpgrade().getViewByName("TowerView")
                            .setImage(dialogueImageTowerUpgradeLevel[currLvl.currentLevel - 1]);

                    towerUpgradeDialog.getTowerUpgrade().show();
                    if (hasSkillsToPayTheBills && tow.getTowerLevel().currentLevel < 3)
                        towerUpgradeDialog.getTowerUpgrade().getViewByName("Upgrade").show();
                    else
                        towerUpgradeDialog.getTowerUpgrade().getViewByName("Upgrade").hide();
                });
            }

            return;

        } else if (pause) {
            return;
        }

        boolean b = grid.setTower(field.getRow(), field.getColumn());

        Image image = grid.getField(
                field.getRow(),
                field.getColumn()
        ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : currentTowerSprite[0];

        drawConsumer.consume(()-> gridViewMatrix[field.getRow()][field.getColumn()].setImage(image).show());

        if (b) {

            TowerDaemon towerDaemon = new TowerDaemon(
                    gameConsumer,
                    drawConsumer,
                    new Tower(
                            currentTowerSprite,
                            Pair.create(field.getCenterX(), field.getCenterY()),
                            range,
                            towerSelect
                    )
            ).setName("Tower[" + field.getColumn() + "][" + field.getRow() + "]");

            towerDaemon.setView(gridViewMatrix[field.getRow()][field.getColumn()]);

            towers.add(towerDaemon);

            field.setTower(towerDaemon);

            towerDaemon.setAnimateSideQuest().setClosure(new ImageAnimateClosure(gridViewMatrix[field.getRow()][field.getColumn()]));

            towerDaemon.start();
            //towerDaemon.scan(new ArrayList<>(activeEnemies), new TowerScanClosure(towerDaemon)::onReturn);
            towerDaemon.scan(new TowerScanClosure(towerDaemon)::onReturn);
        }
    }

    private Pair<Float,Float> getPointOnCircle(Pair<Float, Float> centerCord, float degress, float radius) {

       double rads = Math.toRadians(degress); // 0 becomes the top

        // Calculate the outter point of the line
        float xPosy = (float) (centerCord.getFirst() + Math.cos(rads) * radius);
        float yPosy = (float) (centerCord.getSecond() - Math.sin(rads) * radius);

        return Pair.create(xPosy, yPosy);
    }

    private List<Pair<Float,Float>> getListOfPoint(Pair<Float, Float> sourceCoord, Pair<Float, Float> targetCoord) {
        //rotation bullet before fire


        int targetAngle = (int) RotatingSpriteImageMover.getAngle(
                sourceCoord.getFirst(),
                sourceCoord.getSecond(),
                targetCoord.getFirst(),
                targetCoord.getSecond()
        );
        Log.w("targetAngle"," pocetni ugao je : "+targetAngle);
        List<Pair<Float,Float>> lst = new LinkedList<>();

        float beginX = sourceCoord.getFirst();
        float beginY = sourceCoord.getSecond();

        float endX =  targetCoord.getFirst();
        float endY =  targetCoord.getSecond();

        float diffX = beginX - endX;
        int signX = 1;
        if (diffX > 0) signX = -1;
        float stepX = Math.abs(diffX) / 2;

        float diffY = beginY - endY;
        int signY = 1;
        if (diffY > 0) signY = -1;
        float stepY = Math.abs(diffY) / 2;

        float radius = (float) (Math.sqrt(diffX*diffX + diffY*diffY)/2);

        Pair<Float,Float> centerCoord = Pair.create(
                sourceCoord.getFirst() + signX * stepX,
                sourceCoord.getSecond() + signY * stepY
        );

        float angle1 =  (targetAngle + 180) >= 360 ? targetAngle + 180 - 360 : targetAngle + 180  ;
        float startAngle;
        float endAngle;

        startAngle = angle1;
        endAngle =  targetAngle;

        float currentAngle = startAngle;

        Log.w("Angle"," pocetni ugao je : "+startAngle+" startAngle coord:"+sourceCoord);
        Log.w("Angle"," zavrsi ugao je : "+endAngle+" endAngle coord:"+targetCoord);
        Log.w("coord"," center coord : "+centerCoord);

        while (currentAngle != endAngle) {

            float tempAngle = currentAngle + signX * (-1) * 20; // step
            currentAngle = tempAngle;

            if (tempAngle >= 360)
                currentAngle = tempAngle - 360;

            if (tempAngle < 0 )
                currentAngle = 360 + tempAngle;

            Pair<Float,Float> co = getPointOnCircle(centerCoord,currentAngle,radius);
            Log.w("Angle"," current angle is : " + currentAngle+"tren coord : "+co);

            lst.add(co);
        }
        return lst;
    }

    private void fireKrugBullet(Pair<Float, Float> sourceCoord, Pair<Float, Float> targetCoord, EnemyDoubleDaemon enemy, float velocity, int bulletDamage, int noOfBulletsFired) {//velocity = 13

        if (!enemy.isShootable())//TODO This is what causes bullets to go out of the screen!!!!!
            return;

        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletQueue.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletQueue.poll();
        bulletDoubleDaemon.setLevel(noOfBulletsFired);
        bulletDoubleDaemon.setDamage(bulletDamage);

        drawConsumer.consume(()->{
            for (ImageView view : bulletDoubleDaemon.getViews())
                view.show();
        });

        bulletDoubleDaemon.setStartingCoords(sourceCoord);
        bulletDoubleDaemon.setOutOfBordersConsumer(gameConsumer).setOutOfBordersClosure(()->{
            drawConsumer.consume(()->{
                for (ImageView view : bulletDoubleDaemon.getViews())
                    view.hide();
            });

            bulletDoubleDaemon.stop();
            bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

            if(!bulletQueue.contains(bulletDoubleDaemon))
                bulletQueue.add(bulletDoubleDaemon);
        });

        bulletDoubleDaemon.start();

        Iterator<Pair<Float,Float>> iterator = getListOfPoint(sourceCoord,targetCoord).iterator();

        if(!iterator.hasNext())
            throw new IllegalStateException("Path list has no elemnets!");

        Pair<Float, Float> pathPtr = iterator.next();

        bulletDoubleDaemon.goTo(pathPtr.getFirst(), pathPtr.getSecond(), velocity*2, /*aReturn -> {*/
            new Closure<Boolean>() {
                @Override
                public void onReturn(Return<Boolean> aReturn) {
                    if (iterator.hasNext()) {

                        Pair<Float, Float> pathPtr = iterator.next();

                        if (!iterator.hasNext()) {
                            //in case that enemi was moved in maintime
                            pathPtr = enemy.getLastCoordinates();
                        }

                        bulletDoubleDaemon.goTo(
                                pathPtr.getFirst(),
                                pathPtr.getSecond(),
                                velocity * 2,
                                this
                        );

                    } else {
                        // reached the enemy
                        if (!enemy.isShootable())
                            return;

                        int newHp = enemy.getHp() - bulletDoubleDaemon.getPrototype().getDamage();

                        if (newHp > 0) {
                            enemy.setHp(newHp);
                        } else {
                            enemy.setShootable(false);
                            drawConsumer.consume(() -> infoScore.setNumbers(++score));
                            drawConsumer.consume(() -> enemy.getHpView().hide());
                            enemy.pushSprite(explodeSprite, 0, aReturn2 -> {
                                drawConsumer.consume(() -> enemy.getView().hide());
                                enemy.stop();
                                activeEnemies.remove(enemy);
                                enemy.setLastCoordinates(grid.getStartingX(),grid.getStartingY());
                                if (!enemyQueue.contains(enemy))
                                    enemyQueue.add(enemy);
                            });
                        }

                        bulletDoubleDaemon.pushSprite(miniExplodeSprite, 0, ret2 -> {

                            drawConsumer.consume(() -> {
                                for (ImageView view : bulletDoubleDaemon.getViews()) view.hide();
                            });

                            bulletDoubleDaemon.stop();
                            bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

                            if (!bulletQueue.contains(bulletDoubleDaemon))
                                bulletQueue.add(bulletDoubleDaemon);

                        });
                    }
                }
            });

    }

    private void fireBullet(Pair<Float, Float> sourceCoord,Pair<Float, Float> targetCoord, EnemyDoubleDaemon enemy, float velocity, int bulletDamage, int noOfBulletsFired) {//velocity = 13

        if (!enemy.isShootable())//TODO This is what causes bullets to go out of the screen!!!!!
            return;

        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletQueue.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletQueue.poll();
        bulletDoubleDaemon.setLevel(noOfBulletsFired);
        bulletDoubleDaemon.setDamage(bulletDamage);
        bulletDoubleDaemon.setSprite(bulletSprite);
        drawConsumer.consume(()->{
            for (ImageView view : bulletDoubleDaemon.getViews()){
                view.show();
            }
        });

        bulletDoubleDaemon.setStartingCoords(sourceCoord);
        bulletDoubleDaemon.setOutOfBordersConsumer(gameConsumer).setOutOfBordersClosure(()->{

            drawConsumer.consume(()->{
                for (ImageView view : bulletDoubleDaemon.getViews()){
                    view.hide();
                }
            });

            bulletDoubleDaemon.stop();
            bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

            if(!bulletQueue.contains(bulletDoubleDaemon)) {
                bulletQueue.add(bulletDoubleDaemon);
            }
        });

        //rotation bullet before fire
        int targetAngle = (int) RotatingSpriteImageMover.getAngle(
                sourceCoord.getFirst(),
                sourceCoord.getSecond(),
                targetCoord.getFirst(),
                targetCoord.getSecond()
        );

        bulletDoubleDaemon.start();

        bulletDoubleDaemon.rotate(targetAngle, ret-> {

            bulletDoubleDaemon.goTo(targetCoord.getFirst(), targetCoord.getSecond(), velocity, aReturn -> {

                if (!enemy.isShootable())
                    return;

                int newHp = enemy.getHp() - bulletDoubleDaemon.getPrototype().getDamage();

                if (newHp > 0) {
                    enemy.setHp(newHp);
                } else {
                    enemy.setShootable(false);
                    drawConsumer.consume(() -> infoScore.setNumbers(++score));
                    drawConsumer.consume(() -> enemy.getHpView().hide());
                    enemy.pushSprite(explodeSprite, 0, aReturn2 -> {
                        drawConsumer.consume(() -> enemy.getView().hide());
                        enemy.stop();
                        activeEnemies.remove(enemy);
                        enemy.setLastCoordinates(grid.getStartingX(),grid.getStartingY());
                        if (!enemyQueue.contains(enemy))
                            enemyQueue.add(enemy);
                    });
                }

                bulletDoubleDaemon.pushSprite(miniExplodeSprite, 0, ret2 -> {

                    drawConsumer.consume(() -> {
                        for (ImageView view : bulletDoubleDaemon.getViews()) {
                            view.hide();
                        }
                    });

                    bulletDoubleDaemon.stop();
                    bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

                    if (!bulletQueue.contains(bulletDoubleDaemon))
                        bulletQueue.add(bulletDoubleDaemon);

                });

            });
        });

    }

    private void fireRocketBullet(Pair<Float, Float> sourceCoord, EnemyDoubleDaemon enemy, float velocity, int bulletDamage, int noOfBulletsFired) {//velocity = 13

        if (!enemy.isShootable())//TODO This is what causes bullets to go out of the screen!!!!!
            return;

        Pair<Float, Float> enemyCoord = enemy.getPrototype().getLastCoordinates();

        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletQueue.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletQueue.poll();
        bulletDoubleDaemon.setStartingCoords(sourceCoord);
        bulletDoubleDaemon.setLevel(noOfBulletsFired);
        bulletDoubleDaemon.setDamage(bulletDamage);
        bulletDoubleDaemon.setSprite(bulletSpriteLaser);

        bulletDoubleDaemon.setOutOfBordersConsumer(gameConsumer).setOutOfBordersClosure(()->{

            drawConsumer.consume(()->{
                for (ImageView view : bulletDoubleDaemon.getViews()){
                    view.hide();
                }
            });

            bulletDoubleDaemon.stop();
            bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

            if(!bulletQueue.contains(bulletDoubleDaemon)) {
                bulletQueue.add(bulletDoubleDaemon);
            }
        });

        int launchX = getRandomInt((int)(sourceCoord.getFirst() - 50), (int)(sourceCoord.getFirst() + 50));
        int launchY = getRandomInt((int)(sourceCoord.getSecond() - 50), (int)(sourceCoord.getSecond() + 50));

        if (Math.abs(sourceCoord.getFirst() - launchX) > 50) {
            throw  new IllegalStateException("getrandomInt() X not allowed");
        }

        if (Math.abs(sourceCoord.getSecond() - launchY) > 50) {
            throw  new IllegalStateException("getrandomInt() Y not allowed");
        }

        int angle = (int) RotatingSpriteImageMover.getAngle(
                sourceCoord.getFirst(),
                sourceCoord.getSecond(),
                launchX,
                launchY
        );

        bulletDoubleDaemon.start();

        bulletDoubleDaemon.rotate(angle, ret1->{

            drawConsumer.consume(()->{
                for (ImageView view : bulletDoubleDaemon.getViews()){
                    view.show();
                }
            });

            bulletDoubleDaemon.goTo(launchX,launchY, 4, aReturn1 -> {

                if (!enemy.isShootable()) {
                    bulletDoubleDaemon.pushSprite(miniExplodeSprite, 0, ret -> {

                        drawConsumer.consume(() -> {
                            for (ImageView view : bulletDoubleDaemon.getViews())
                                view.hide();
                        });

                        bulletDoubleDaemon.stop();
                        bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

                        if (!bulletQueue.contains(bulletDoubleDaemon))
                            bulletQueue.add(bulletDoubleDaemon);

                    });
                }


                int targetAngle1 = (int) RotatingSpriteImageMover.getAngle(
                        bulletDoubleDaemon.getLastCoordinates().getFirst(),
                        bulletDoubleDaemon.getLastCoordinates().getSecond(),
                        enemy.getLastCoordinates().getFirst(),
                        enemy.getLastCoordinates().getSecond()
                );

                bulletDoubleDaemon.rotate(targetAngle1, aReturnR -> {
                    bulletDoubleDaemon.goTo(
                        enemy.getLastCoordinates().getFirst(),
                        enemy.getLastCoordinates().getSecond(),
                        velocity,
                        aReturn2->{
                            if (!enemy.isShootable()) return;

                            float bulletX = bulletDoubleDaemon.getLastCoordinates().getFirst();
                            float bulletY = bulletDoubleDaemon.getLastCoordinates().getSecond();

                            if (Math.abs(bulletX - enemy.getLastCoordinates().getFirst()) > 200
                                    && Math.abs(bulletY - enemy.getLastCoordinates().getSecond()) > 200)
                                return;

                            int newHp = enemy.getHp() - bulletDoubleDaemon.getDamage();

                            if (newHp > 0) {
                                enemy.setHp(newHp);
                            } else {
                                enemy.setShootable(false);
                                drawConsumer.consume(() -> infoScore.setNumbers(++score));
                                drawConsumer.consume(() -> enemy.getHpView().hide());
                                enemy.pushSprite(explodeSprite, 0, aReturn3 -> {
                                    drawConsumer.consume(() -> enemy.getView().hide());
                                    enemy.stop();
                                    activeEnemies.remove(enemy);
                                    enemy.setLastCoordinates(grid.getStartingX(),grid.getStartingY());
                                    if (!enemyQueue.contains(enemy)) enemyQueue.add(enemy);
                                });
                            }

                            bulletDoubleDaemon.pushSprite(miniExplodeSprite, 0, ret -> {

                                drawConsumer.consume(() -> {
                                    for (ImageView view : bulletDoubleDaemon.getViews()) {
                                        view.hide();
                                    }
                                });

                                bulletDoubleDaemon.stop();
                                bulletDoubleDaemon.setStartingCoords(Pair.create(0F, 0F));

                                if (!bulletQueue.contains(bulletDoubleDaemon))
                                    bulletQueue.add(bulletDoubleDaemon);

                            });
                        }
                    );
                });
            });
        });

    }

    public void fireLaser(Pair<Float, Float> source, EnemyDoubleDaemon enemy, long duration) {

        laser.desintegrateTarget(source, enemy, duration, drawConsumer, ret->{

            int newHp = enemy.getHp() - laser.getDamage();

            if (newHp > 0) {
                enemy.setHp(newHp);
            } else {
                enemy.setShootable(false);
                drawConsumer.consume(() -> infoScore.setNumbers(++score));
                drawConsumer.consume(() -> enemy.getHpView().hide());
                enemy.pushSprite(explodeSprite, 0, aReturn3 -> {
                    drawConsumer.consume(() -> enemy.getView().hide());
                    enemy.stop();
                    activeEnemies.remove(enemy);
                    enemy.setLastCoordinates(grid.getStartingX(),grid.getStartingY());
                    if (!enemyQueue.contains(enemy)) enemyQueue.add(enemy);
                });
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
    public Game setBulletSpriteLaser(Image[] bulletSpriteLaser) {
        this.bulletSpriteLaser = bulletSpriteLaser;
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

    public Game setDialogue(Image dialogueImage) {
        this.dialogueImage = dialogueImage;
        return this;
    }

    public Game setDialogueImageTowerUpgradeLevel(Image[] dialogueImageTowerUpgradeLevel) {
        this.dialogueImageTowerUpgradeLevel = dialogueImageTowerUpgradeLevel;
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

    public Game setRedNestedDialogue(Image nestedRedDialogueImage) {
        this.nestedRedDialogueImage = nestedRedDialogueImage;
        return this;
    }

    public Game setScoreBackGrImage(Image scoreBackGrImage) {
        this.scoreBackGrImage = scoreBackGrImage;
        return this;
    }

    public Game setScoreTitle(Image scoreTitle) {
        this.scoreTitle = scoreTitle;
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

}
