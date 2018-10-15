package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.renderer.Renderer2D;
import com.daemonize.daemondevapp.scene.Scene2D;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemondevapp.view.ImageViewImpl;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Game {

    //game threads
    private Renderer2D renderer;
    private DaemonConsumer gameConsumer = new DaemonConsumer("Game Consumer");
    private DaemonConsumer guiConsumer = new DaemonConsumer("Gui Consumer");

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

    private int score = 0;

    private ImageView scoreBackGrView;
    private ImageView scoreTitleView;

    ImageView [] viewsNum;
    private InfoTable infoScore;

    private Image fieldImage;
    private Image fieldImageTower;
    private Image fieldImageTowerDen;
    private Image dialogueImage;
    private Image scoreBackGrImage;
    private Image scoreTitle;
    private Image [] scorenumbersImages;

    private boolean pause;

    //towers
    private List<TowerDaemon> towers = new ArrayList<>();
    private Queue<EnemyDoubleDaemon> enemyQueue = new LinkedList<>();
    private int towerShootInterval = 1500;
    private int range = 320;

    private TowerScanClosure towerScanClosure;

    //enemies
    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();
    private Image[] enemySprite;
    private Image [] explodeSprite;
    private Image [] healthBarSprite;

    private DummyDaemon enemyGenerator;
    private DummyDaemon levelGenerator;
    private long enemyCounter = 0;
    private float enemyVelocity = 1;
    private int enemyHp = 10;
    private long enemyGenerateinterval = 5000;
    private long levelGenerateinterval = 5000;

    //bullets
    private Queue<BulletDoubleDaemon> bulletQueue = new LinkedList<>();
    private Image[] bulletSprite;

    //closures
    private class ImageAnimateClosure implements Closure<ImageMover.PositionedImage> {

        private ImageView view;

        public ImageAnimateClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedImage> aReturn) {
            ImageMover.PositionedImage posBmp = aReturn.uncheckAndGet();
            view.setX(posBmp.positionX);
            view.setY(posBmp.positionY);
            view.setImage(posBmp.image);
        }
    }

    private class EnemyAnimateClosure implements Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
        @Override
        public void onReturn(Return<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> aReturn) {
            GenericNode.forEach(aReturn.get(), actionret -> {
                Pair<ImageMover.PositionedImage, ImageView> imageAndView = actionret.get();
                imageAndView.getSecond().setX(imageAndView.getFirst().positionX);
                imageAndView.getSecond().setY(imageAndView.getFirst().positionY);
                imageAndView.getSecond().setImage(imageAndView.getFirst().image);
            });
        }
    }

    public class TowerScanClosure implements Closure<Pair<Boolean, EnemyDoubleDaemon>> {

        private TowerDaemon tower;
        private int sleepInterval;

        public TowerScanClosure setSleepInterval(int sleepInterval) {
            this.sleepInterval = sleepInterval;
            return this;
        }

        public TowerScanClosure(TowerDaemon tower, int sleepInterval) {
            this.tower = tower;
            this.sleepInterval = sleepInterval;
        }

        @Override
        public void onReturn(Return<Pair<Boolean, EnemyDoubleDaemon>> aReturn) {

            if (aReturn.get() != null && aReturn.get().getFirst()) {
                fireBullet(tower.getPrototype().getLastCoordinates(), aReturn.get().getSecond(),15);
                tower.sleep(sleepInterval, aReturn1 -> {
                    List<EnemyDoubleDaemon> clone = new ArrayList<>(activeEnemies.size());
                    clone.addAll(activeEnemies);
                    tower.scan(clone, this);
                });
            } else {
                List<EnemyDoubleDaemon> clone = new ArrayList<>(activeEnemies.size());
                clone.addAll(activeEnemies);
                tower.scan(clone, this);
            }
        }
    }

    public Game setBorders(int x, int y) {
        this.borderX = x;
        this.borderY = y;
        return this;
    }

    public Game setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    public Game setTowerSprite(Image [] towerSprite) {
        this.towerSprite = towerSprite;
        return this;
    }

    private Image[] towerSprite;

    public Game setExplodeSprite(Image[] explodeSprite) {
        this.explodeSprite = explodeSprite;
        return this;
    }

    public Game setBulletSprite(Image[] bulletSprite) {
        this.bulletSprite = bulletSprite;
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

    private void pauseAll() {
        pause = true;
        enemyGenerator.stop();
        for (EnemyDoubleDaemon enemy : activeEnemies) {
            enemy.pause();
        }
        for (TowerDaemon tower : towers) {
            tower.pause();
        }
    }

    private void contAll() {
        pause = false;
        enemyGenerator.start();
        for (EnemyDoubleDaemon enemy : activeEnemies) {
            enemy.cont();
        }
        for (TowerDaemon tower : towers) {
            tower.cont();
        }
    }

    public Game run() {
        guiConsumer.start();
        gameConsumer.start();
        chain.run();
        return this;
    }

    public Game stop(){
        enemyGenerator.stop();
        for(EnemyDoubleDaemon enemy : activeEnemies) enemy.stop();
        for (TowerDaemon tower : towers) tower.stop();
        guiConsumer.stop();
        gameConsumer.stop();
        renderer.stop();
        return this;
    }

    public Game onTouch(float x, float y) {
        gameConsumer.consume(()->{
            if (dialogue.getValue().isShowing()) {
                if (dialogue.getChildren().get(0).getValue().checkCoordinates(x,y)) {
                    dialogue.getValue().hide();
                    dialogue.getChildren().get(0).getValue().hide();
                    contAll();
                }
            } else {
                setTower(x, y);
            }
        });
        return this;
    }

    {
        //init spell (state)
        chain.addState(()-> {

            scene.addImageView(new ImageViewImpl().setImageWithoutOffset(backgroundImage).setX(0).setY(0).setZindex(0).show());

            dialogue = new GenericNode<>(scene.addImageView(new ImageViewImpl().hide().setX(0).setY(0).setZindex(3)), "TEST DIALOGUE");
            dialogue.addChild(new GenericNode<>(scene.addImageView(new ImageViewImpl().hide().setX(0).setY(0).setZindex(4)), "KILL DIALOGUE BUTTON"));

            scoreBackGrView = new ImageViewImpl().setX(0).setY(0).setZindex(3).show();

            scoreTitleView = new ImageViewImpl().setX(0).setY(0).setZindex(4).show();

            viewsNum = new ImageView[5];
            viewsNum[0] = new ImageViewImpl().setX(0).setY(0).setZindex(5).show();
            viewsNum[1] = new ImageViewImpl().setX(0).setY(0).setZindex(5).show();
            viewsNum[2] = new ImageViewImpl().setX(0).setY(0).setZindex(5).show();
            viewsNum[3] = new ImageViewImpl().setX(0).setY(0).setZindex(5).show();
            viewsNum[4] = new ImageViewImpl().setX(0).setY(0).setZindex(5).show();

            scene.addImageView(scoreBackGrView);
            scene.addImageView(scoreTitleView);

            for (ImageView view : viewsNum) {
                scene.addImageView(view);
            }



            gridViewMatrix = new ImageView[rows][columns];

            for(int j = 0; j < rows; ++j ) {
                for (int i = 0; i < columns; ++i) {
                    gridViewMatrix[j][i] = scene.addImageView(new ImageViewImpl().hide().setX(0).setY(0).setZindex(1));
                }
            }

            Field firstField = grid.getField(0, 0);

            for (int i = 0; i < 200; ++i) {
                EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                        gameConsumer,
                        guiConsumer,
                        new Enemy(
                                enemySprite,
                                enemyVelocity,
                                enemyHp,
                                Pair.create((float) 0, (float) 0),
                                Pair.create(firstField.getCenterX(), firstField.getCenterY())
                        ).setView(scene.addImageView(new ImageViewImpl().hide().setX(0).setY(0).setZindex(2)))
                                .setHpView(scene.addImageView(new ImageViewImpl().hide().setX(0).setY(0).setZindex(2)))
                                .setHealthBarImage(healthBarSprite)
                ).setName("Enemy no. " + i);

                enemy.getPrototype().setBorders(borderX, borderY);

                enemy.setAnimateEnemySideQuest().setClosure(new EnemyAnimateClosure());//gui consumer

                enemyQueue.add(enemy);

            }

            for (int i = 0; i < 200; ++i) {
                BulletDoubleDaemon bulletDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        guiConsumer,
                        new Bullet(
                                bulletSprite,
                                0,
                                Pair.create((float) 0, (float) 0),
                                Pair.create((float) 0, (float) 0),
                                2
                        ).setView(scene.addImageView(new ImageViewImpl().hide().setX(0).setY(0).setZindex(0)))
                ).setName("Bullet no. " + i);

                bulletDoubleDaemon.getPrototype().setBorders(borderX, borderY);
                bulletDoubleDaemon.setAnimateSideQuest().setClosure(aReturn -> { //by default working on gui consumer,because of that we use  ** some part of code which we want to be executed in game  consumer, not in gui

                    ImageMover.PositionedImage posBmp = aReturn.get();

                    gameConsumer.consume(()-> { //**
                                if (Math.abs(posBmp.positionX) < 20
                                        || Math.abs(posBmp.positionX - borderX) < 20
                                        || Math.abs(posBmp.positionY) < 20
                                        || Math.abs(posBmp.positionY - borderY) < 20) {
                                    bulletDoubleDaemon.stop();
                                    guiConsumer.consume(() -> bulletDoubleDaemon.getView().hide());
                                    if (!bulletQueue.contains(bulletDoubleDaemon))
                                        bulletQueue.add(bulletDoubleDaemon);
                                }
                    });

                    bulletDoubleDaemon.getView().setX(posBmp.positionX);
                    bulletDoubleDaemon.getView().setY(posBmp.positionY);
                    bulletDoubleDaemon.getView().setImage(posBmp.image);

                });

                bulletQueue.add(bulletDoubleDaemon);
            }

            scene.lockViews();
            renderer.setScene(scene).start();

            chain.next();

        }).addState(()->{//gameState

            guiConsumer.consume(()->{
                for(int j = 0; j < rows; ++j ) {
                    for (int i = 0; i < columns; ++i) {
                        gridViewMatrix[j][i].setX(grid.getGrid()[j][i].getCenterX());
                        gridViewMatrix[j][i].setY(grid.getGrid()[j][i].getCenterY());
                        gridViewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower).show();
                    }
                }

                scoreTitleView.setImage(scoreTitle);
                scoreBackGrView.setImage(scoreBackGrImage);
                infoScore = new InfoTable(borderX - scoreBackGrImage.getWidth(),150,scoreBackGrView,scoreTitleView,viewsNum,scorenumbersImages).setNumbers(97513);

            });

            Field firstField = grid.getField(0, 0);

            enemyGenerator = new DummyDaemon(gameConsumer, enemyGenerateinterval).setClosure(ret->{

                enemyCounter++;

                Log.d(DaemonUtils.tag(), "Enemy queue size: " + enemyQueue.size());

                //every 15 enemies increase the pain!!!!
                if (enemyCounter % 3 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

                    if (enemyHp < 80)
                        enemyHp += 5;

                    if (enemyGenerateinterval > 1000)
                        enemyGenerateinterval -= 500;

                    enemyGenerator.setSleepInterval(20000);
                } else {
                    enemyGenerator.setSleepInterval((int)enemyGenerateinterval);
                }

                EnemyDoubleDaemon enemy = enemyQueue.poll();
                enemy.setName("Enemy no." + enemyCounter);
                enemy.setMaxHp(enemyHp);
                enemy.setHp(enemyHp);
                enemy.getPrototype().setVelocity(enemyVelocity);

                guiConsumer.consume(()->enemy.getView().show());
                guiConsumer.consume(()->enemy.getHpView().show());
                activeEnemies.add(enemy);
                enemy.setShootable(true);
                enemy.start();

                activeEnemies.add(enemy);

                enemy.goTo(firstField.getCenterX(), firstField.getCenterY(), enemyVelocity,
                        new Closure<Boolean>() {// gameConsumer
                            @Override
                            public void onReturn(Return<Boolean> aReturn) {
                                Pair<Float, Float> currentCoord = enemy.getPrototype().getLastCoordinates();
                                Field current = grid.getField(currentCoord.getFirst(), currentCoord.getSecond());

                                if (current == null) return;
                                else if (current.getColumn() == columns - 1 && current.getRow() == rows - 1) {
                                    enemy.setShootable(false);
                                    guiConsumer.consume(()-> enemy.getHpView().hide());
                                    guiConsumer.consume(()-> infoScore.setNumbers(++score));
                                    enemy.pushSprite(explodeSprite, 0,  aReturn2-> {
                                        enemy.stop();
                                        guiConsumer.consume(() -> enemy.getView().hide());
                                        activeEnemies.remove(enemy);
                                        if (!enemyQueue.contains(enemy))
                                            enemyQueue.add(enemy);
                                    });
                                }

                                Field next = grid.getMinWeightOfNeighbors(current);
                                enemy.goTo(next.getCenterX(), next.getCenterY(), enemyVelocity, this);
                            }
                        }
                );

            });

            enemyGenerator.start();

            //try to garbage collect
            new DummyDaemon(gameConsumer, 3000).setClosure(aReturn -> System.gc()).start();

        });

    }

    public Game setTower(float x, float y) { //TODO to be called from Activity.onTouch()

        gameConsumer.consume(()-> {

            Field field = grid.getField(x, y);
            if (field == null) return;

            //upgrade existing tower
            TowerDaemon tow = field.getTower();
            if (tow != null) {

                if (!dialogue.getValue().isShowing()) {
                    pauseAll();
                    guiConsumer.consume(() -> {
                        dialogue.getValue().setImage(dialogueImage)
                                .setX(grid.getStartingX() + grid.getGridHeight() + dialogueImage.getWidth() / 2)//TODO fix grid.getGridHeight/Width!!!!!!!!!!!!
                                .setY(grid.getStartingY() + dialogueImage.getHeight() / 2)
                                .show();
                        dialogue.getChildren().get(0).getValue()
                                .setImage(fieldImageTowerDen)
                                .setX(grid.getStartingX() + grid.getGridHeight() + dialogueImage.getWidth() * 7/8)
                                .setY(grid.getStartingY())
                                .show();

                    });
                } else {
                    contAll();
                    guiConsumer.consume(() -> GenericNode.forEach(dialogue, ret->{
                        ret.get().hide();
                    }));
                }

                if(towerShootInterval > 200)
                    towerShootInterval -= 50;

                towerScanClosure.setSleepInterval(towerShootInterval);
                return;
            } else if (pause) {
                return;
            }

            boolean b = grid.setTower(field.getRow(), field.getColumn());

            Image image = grid.getField(
                    field.getRow(),
                    field.getColumn()
            ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : towerSprite[0];

            guiConsumer.consume(()-> gridViewMatrix[field.getRow()][field.getColumn()].setImage(image).show());

            if (b) {

                Image[] initTowerSprite = new Image[1];
                initTowerSprite[0] = towerSprite[0];

                TowerDaemon towerDaemon = new TowerDaemon(
                        gameConsumer,
                        guiConsumer,
                        new Tower(
                                initTowerSprite,
                                towerSprite,
                                Pair.create(field.getCenterX(), field.getCenterY()),
                                range,
                                towerShootInterval
                        )
                ).setName("Tower[" + field.getColumn() + "][" + field.getRow() + "]");

                towerDaemon.setView(gridViewMatrix[field.getRow()][field.getColumn()]);

                towers.add(towerDaemon);

                field.setTower(towerDaemon);

                towerDaemon.setAnimateSideQuest().setClosure(new ImageAnimateClosure(gridViewMatrix[field.getRow()][field.getColumn()]));

                towerDaemon.start();

                towerScanClosure = new TowerScanClosure(towerDaemon, towerShootInterval);
                List<EnemyDoubleDaemon> clone = new ArrayList<EnemyDoubleDaemon>(activeEnemies.size());
                clone.addAll(activeEnemies);
                towerDaemon.scan(clone, towerScanClosure);
            }
        });

        return this;
    }

    private void fireBullet(Pair<Float, Float> sourceCoord, EnemyDoubleDaemon enemy, float velocity) {//velocity = 13

        if (!enemy.isShootable())
            return;

        Pair<Float, Float> enemyCoord = enemy.getPrototype().getLastCoordinates();

        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletQueue.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletQueue.poll();
        guiConsumer.consume(()->bulletDoubleDaemon.getView().show());
        bulletDoubleDaemon.setStartingCoords(sourceCoord);
        bulletDoubleDaemon.start();

        bulletDoubleDaemon.goTo(enemyCoord.getFirst(), enemyCoord.getSecond(), velocity, aReturn-> {

            int enemyHp = enemy.getHp();
            if (enemyHp > 0) {
                enemy.setHp(enemyHp - bulletDoubleDaemon.getPrototype().getDamage());
            } else {
                enemy.setShootable(false);
                guiConsumer.consume(()->enemy.getHpView().hide());
                enemy.pushSprite(explodeSprite, 0,  aReturn2-> {
                    guiConsumer.consume(() -> enemy.getView().hide());
                    enemy.stop();
                    activeEnemies.remove(enemy);
                    if (!enemyQueue.contains(enemy))
                        enemyQueue.add(enemy);
                });
            }

            bulletDoubleDaemon.stop();
            guiConsumer.consume(()->bulletDoubleDaemon.getView().hide());

            if(!bulletQueue.contains(bulletDoubleDaemon))
                bulletQueue.add(bulletDoubleDaemon);

        });

    }

}
