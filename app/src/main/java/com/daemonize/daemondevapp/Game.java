package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.renderer.Renderer;
import com.daemonize.daemondevapp.view.ImageView;
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
    private Renderer renderer;
    private DaemonConsumer gameConsumer = new DaemonConsumer("Game Consumer");
    private DaemonConsumer guiConsumer = new DaemonConsumer("Gui Consumer");

    //state holder
    private DaemonChainScript chain = new DaemonChainScript();

    //screen borders
    private int borderX;
    private int borderY;

    //grid
    private Grid grid;
    private int rows;
    private int columns;
    private ImageView[][] viewMatrix;

    private Image fieldImage;
    private Image fieldImageTower;
    private Image fieldImageTowerDen;

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
    private long enemyCounter;
    private float enemyVelocity = 1;
    private int enemyHp = 10;
    private long enemyGenerateinterval = 5000;

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

    public Game(Renderer renderer, int rows, int columns, float x, float y, int fieldWidth) {
        this.renderer = renderer;
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

    {
        //init spell (state)
        chain.addState(()-> {

            viewMatrix = new ImageView[rows][columns];

            for(int j = 0; j < rows; ++j ) {
                for (int i = 0; i < columns; ++i) {
                    viewMatrix[j][i] = renderer.createImageView(1);
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
                        ).setView(renderer.createImageView(2))
                                .setHpView(renderer.createImageView(2))
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
                        ).setView(renderer.createImageView(0))
                ).setName("Bullet no. " + i);

                bulletDoubleDaemon.getPrototype().setBorders(borderX, borderY);
                bulletDoubleDaemon.setAnimateSideQuest().setClosure(aReturn -> {

                    ImageMover.PositionedImage posBmp = aReturn.get();

                    gameConsumer.consume(()-> {
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

            renderer.start();

            chain.next();

        }).addState(()->{//gameState

            guiConsumer.consume(()->{
                for(int j = 0; j < rows; ++j ) {
                    for (int i = 0; i < columns; ++i) {
                        viewMatrix[j][i].setX(grid.getGrid()[j][i].getCenterX());
                        viewMatrix[j][i].setY(grid.getGrid()[j][i].getCenterY());
                        viewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower).show();
                    }
                }
            });

            Field firstField = grid.getField(0, 0);

            enemyGenerator = new DummyDaemon(gameConsumer, (int) enemyGenerateinterval).setClosure(ret->{

                enemyCounter++;

                Log.d(DaemonUtils.tag(), "Enemy queue size: " + enemyQueue.size());

                //every 15 enemies increase the pain!!!!
                if (enemyCounter % 15 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

                    if (enemyHp < 80)
                        enemyHp += 5;

                    if (enemyGenerateinterval > 1000)
                        enemyGenerateinterval -= 500;

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

                if(towerShootInterval > 200)
                    towerShootInterval -= 50;

                towerScanClosure.setSleepInterval(towerShootInterval);
                return;
            }

            boolean b = grid.setTower(field.getRow(), field.getColumn());

            Image image = grid.getField(
                    field.getRow(),
                    field.getColumn()
            ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : towerSprite[0];

            guiConsumer.consume(()-> viewMatrix[field.getRow()][field.getColumn()].setImage(image).show());

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

                towerDaemon.setView(viewMatrix[field.getRow()][field.getColumn()]);

                towers.add(towerDaemon);

                field.setTower(towerDaemon);

                towerDaemon.setAnimateSideQuest().setClosure(new ImageAnimateClosure(viewMatrix[field.getRow()][field.getColumn()]));

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
