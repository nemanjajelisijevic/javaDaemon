package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.consumer.androidconsumer.AndroidLooperConsumer;
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

    private DaemonConsumer gameConsumer = new DaemonConsumer("Game Consumer");
    private DaemonChainScript chain = new DaemonChainScript();
    private Consumer guiConsumer = new AndroidLooperConsumer();
    //private DaemonHelperConsumer enemyDrawConsumer = new DaemonHelperConsumer("Enemy Draw Consumer", guiConsumer, 10);

    //screen borders
    private int borderX;
    private int borderY;

    public Game setBorders(int x, int y) {
        this.borderX = x;
        this.borderY = y;
        return this;
    }

    private int rows;
    private int columns;
    private DaemonView[][] viewMatrix;

    private Grid grid;

    private Bitmap fieldImage;
    private Bitmap fieldImagePath;
    private Bitmap fieldImageTower;
    private Bitmap fieldImageTowerDen;

    private List<TowerDaemon> towers = new ArrayList<>();
    private Queue<EnemyDoubleDaemon> enemyQueue = new LinkedList<>();
    private int towerShootInterval = 1500;
    private int range = 320;

    private Queue<DaemonView> enemyViews;
    private Queue<DaemonView> enemyHpViews;
    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();
    private List<Bitmap> enemySprite;

    private DummyDaemon enemyGenerator;
    private long enemyCounter;
    private float enemyVelocity = 1;
    private int enemyHp = 10;
    private long enemyGenerateinterval = 5000;

    private List<Bitmap> healthBarImage;

    public Game setHealthBarImage(List<Bitmap> healthBarImage) {
        this.healthBarImage = healthBarImage;
        return this;
    }

    private Queue<DaemonView> bulletViewQueue;
    private Queue<BulletDoubleDaemon> bulletQueue = new LinkedList<>();
    private List<Bitmap> bulletSprite;

    private List<Bitmap> explodeSprite;

    private class ImageAnimateClosure implements Closure<ImageMover.PositionedBitmap> {

        private DaemonView view;

        public ImageAnimateClosure(DaemonView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedBitmap> aReturn) {
            ImageMover.PositionedBitmap posBmp = aReturn.get();
            view.setX(posBmp.positionX);
            view.setY(posBmp.positionY);
            view.setImage(posBmp.image);
        }
    }

    private class EnemyAnimateClosure implements Closure<GenericNode<Pair<ImageMover.PositionedBitmap, DaemonView>>> {
        @Override
        public void onReturn(Return<GenericNode<Pair<ImageMover.PositionedBitmap, DaemonView>>> aReturn) {
            GenericNode.forEach(aReturn.uncheckAndGet(), actionret -> {
                Pair<ImageMover.PositionedBitmap, DaemonView> imageAndView = actionret.get();
                imageAndView.second.setX(imageAndView.first.positionX);
                imageAndView.second.setY(imageAndView.first.positionY);
                imageAndView.second.setImage(imageAndView.first.image);
            });
        }
    }

    public Game setTowerSprite(List<Bitmap> towerSprite) {
        this.towerSprite = towerSprite;
        return this;
    }

    private List<Bitmap> towerSprite;

    public Game setExplodeSprite(List<Bitmap> explodeSprite) {
        this.explodeSprite = explodeSprite;
        return this;
    }

    public Game setBulletSprite(List<Bitmap> bulletSprite) {
        this.bulletSprite = bulletSprite;
        return this;
    }

    public Game setEnemySprite(List<Bitmap> enemySprite) {
        this.enemySprite = enemySprite;
        return this;
    }

    public Game setFieldImage(Bitmap fieldImage) {
        this.fieldImage = fieldImage;
        return this;
    }

    public Game setFieldImagePath(Bitmap fieldImagePath) {
        this.fieldImagePath = fieldImagePath;
        return this;
    }

    public Game setFieldImageTower(Bitmap fieldImageTower) {
        this.fieldImageTower = fieldImageTower;
        return this;
    }

    public Game setFieldImageTowerDen(Bitmap fieldImageTowerDen) {
        this.fieldImageTowerDen = fieldImageTowerDen;
        return this;
    }

    public Game(
            int rows,
            int columns,
            DaemonView[][] viewMatrix,
            Queue<DaemonView> enemyViews,
            Queue<DaemonView> enemyHpViewQueue,
            Queue<DaemonView> bulletViewQueue,
            float x,
            float y,
            int fieldWidth) {
        this.rows = rows;
        this.columns = columns;
        this.viewMatrix = viewMatrix;
        //TODO validate enemyViews
        this.enemyViews = enemyViews;
        this.enemyHpViews = enemyHpViewQueue;
        this.grid = new Grid(rows, columns, Pair.create(0, 0), Pair.create(rows - 1, columns - 1), x, y, fieldWidth);
        this.bulletViewQueue = bulletViewQueue;
    }

    public Game run() {
        gameConsumer.start();
        chain.run();
        return this;
    }

    public Game stop(){

        enemyGenerator.stop();

        for(EnemyDoubleDaemon enemy : activeEnemies) {
            enemy.stop();
        }

        for (TowerDaemon tower : towers) {
            tower.stop();
        }

        gameConsumer.stop();

        return this;
    }


    {
        //init spell (state)
        chain.addSpell(()->{

            Field firstField = grid.getField(0, 0);

            for (DaemonView enemyView : enemyViews) {
                EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                        gameConsumer,
                        guiConsumer,
                        new Enemy(
                                enemySprite,
                                enemyVelocity,
                                enemyHp,
                                Pair.create((float)0, (float)0),
                                Pair.create(firstField.getCenterX(), firstField.getCenterY())
                        ).setView(enemyView).setHpView(enemyHpViews.poll())
                        .setHealthBarImage(healthBarImage)
                ).setName("Enemy");

                enemy.getPrototype().setBorders(borderX, borderY);

                enemy.setAnimateEnemySideQuest().setClosure(new EnemyAnimateClosure());//gui consumer

                enemyQueue.add(enemy);

            }


            for (DaemonView bulletView : bulletViewQueue) {

                BulletDoubleDaemon bulletDoubleDaemon = new BulletDoubleDaemon(
                        gameConsumer,
                        guiConsumer,
                        new Bullet(
                                bulletSprite,
                                0,
                                Pair.create((float)0, (float)0),
                                Pair.create((float)0, (float)0),
                                2
                        ).setView(bulletView)
                ).setName("Bullet");

                bulletDoubleDaemon.getPrototype().setBorders(borderX,borderY);
                bulletDoubleDaemon.setAnimateSideQuest().setClosure(new ImageAnimateClosure(bulletView));

                bulletQueue.add(bulletDoubleDaemon);
            }


            enemyGenerator = new DummyDaemon(gameConsumer, (int) enemyGenerateinterval).setClosure(ret->{

                enemyCounter++;

                Log.d(DaemonUtils.tag(), "Enemy queue size: " + enemyQueue.size());

                //every 20 enemys increase the pain!!!!
                if (enemyCounter % 15 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

//                    if (enemyHp < 80)
////                        enemyHp += 5;//TODO pay attention to enemy max hp

                    if (enemyGenerateinterval > 1000)
                        enemyGenerateinterval -= 500;

                    enemyGenerator.setSleepInterval((int)enemyGenerateinterval);
                }


                EnemyDoubleDaemon enemy = enemyQueue.poll();
                enemy.setName("Enemy no." + enemyCounter);
                enemy.setHp(enemyHp);
                enemy.getPrototype().setVelocity(enemyVelocity);


                guiConsumer.consume(()->enemy.getView().show());
                activeEnemies.add(enemy);
                enemy.setShootable(true);
                enemy.start();

                activeEnemies.add(enemy);

                enemy.goTo(firstField.getCenterX(), firstField.getCenterY(), enemyVelocity,
                        new Closure<Boolean>() {// gameConsumer
                            @Override
                            public void onReturn(Return<Boolean> aReturn) {
                                Pair<Float, Float> currentCoord = enemy.getPrototype().getLastCoordinates();
                                Field current = grid.getField(currentCoord.first, currentCoord.second);

                                if (current == null) return;
                                else if (current.getColumn() ==  6 - 1 && current.getRow() == rows - 1) {
                                    enemy.setShootable(false);
                                    guiConsumer.consume(()-> enemy.getHpView().hide());
                                    enemy.pushSprite(explodeSprite, 0,  aReturn2-> {
                                        enemy.stop();
                                        guiConsumer.consume(() -> {
                                            enemy.getView().hide();
                                        });
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

            guiConsumer.consume(()->{
                for(int j = 0; j < rows; ++j ) {
                    for (int i = 0; i < columns; ++i) {
                        viewMatrix[j][i].setX(grid.getGrid()[j][i].getCenterX() - (fieldImage.getWidth() / 2));
                        viewMatrix[j][i].setY(grid.getGrid()[j][i].getCenterY() - (fieldImage.getHeight() / 2));
                        viewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower);
                    }
                }
            });

            //try to garbage collect
            new DummyDaemon(gameConsumer, 5000).setClosure(aReturn -> System.gc()).start();

        });

    }

    public Game setTower(float x, float y) { //TODO to be called from Activity.onTouch()

        Log.i(DaemonUtils.tag(), "KLIK koordinate:  X: " + x+",  Y: "+y);
        gameConsumer.consume(()-> {

            Field field = grid.getField(x, y);
            if (field == null) return;

            //upgrade existing tower
            TowerDaemon tow = field.getTower();
            if (tow != null) {

                if(towerShootInterval > 500) {
                    towerShootInterval -= 50;
                }

                towerScanClosure.setSleepInteraval(towerShootInterval);
                return;
            }

            boolean b = grid.setTower(field.getRow(), field.getColumn());

            Bitmap image = grid.getField(
                    field.getRow(),
                    field.getColumn()
            ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : towerSprite.get(0);

            guiConsumer.consume(()-> viewMatrix[field.getRow()][field.getColumn()].setImage(image));

            if (b) {

                List<Bitmap> initTowerSprite = new ArrayList<>(1);
                initTowerSprite.add(towerSprite.get(0));

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

                List<EnemyDoubleDaemon> activeEnemyList = new ArrayList<>();
                for(EnemyDoubleDaemon enemy : activeEnemies) {
                    activeEnemyList.add(enemy);
                }

                towerScanClosure = new TowerScanClosure(towerDaemon, towerShootInterval);
                //towerDaemon.setScanClosure(scanClosure);
                towerDaemon.scan(activeEnemyList, towerScanClosure);
            }
        });

        return this;
    }


    public class TowerScanClosure implements Closure<Pair<Boolean, EnemyDoubleDaemon>> {

        private TowerDaemon tower;
        private int sleepInteraval;

        public TowerScanClosure setSleepInteraval(int sleepInteraval) {
            this.sleepInteraval = sleepInteraval;
            return this;
        }

        public TowerScanClosure(TowerDaemon tower, int sleepInteraval) {
            this.tower = tower;
            this.sleepInteraval = sleepInteraval;
        }

        @Override
        public void onReturn(Return<Pair<Boolean, EnemyDoubleDaemon>> aReturn) {

            if (aReturn.get() != null && aReturn.get().first) {
                fireBullet(tower.getPrototype().getLastCoordinates(), aReturn.get().second,15);
                tower.sleep(sleepInteraval, aReturn1 -> {

                    List<EnemyDoubleDaemon> activeEnemyList = new LinkedList<>();
                    activeEnemyList.addAll(activeEnemies);

                    tower.scan(activeEnemyList, this);
                });
            } else {
                List<EnemyDoubleDaemon> activeEnemyList = new LinkedList<>();
                activeEnemyList.addAll(activeEnemies);
                tower.scan(activeEnemyList, this);
            }
        }
    }

    private TowerScanClosure towerScanClosure;

    private void fireBullet(Pair<Float, Float> sourceCoord, EnemyDoubleDaemon enemy, float velocity) {//velocity = 13

        if (!enemy.isShootable()) {
            return;
        }

        Pair<Float, Float> enemyCoord = enemy.getPrototype().getLastCoordinates();

        Log.i(DaemonUtils.tag(), "Bullet queue size: " + bulletQueue.size());

        BulletDoubleDaemon bulletDoubleDaemon = bulletQueue.poll();
        guiConsumer.consume(()->bulletDoubleDaemon.getView().show());
        bulletDoubleDaemon.setStartingCoords(sourceCoord);
        bulletDoubleDaemon.start();

        bulletDoubleDaemon.goTo(enemyCoord.first, enemyCoord.second, velocity, aReturn-> {

            int enemyHp = enemy.getHp();
            if (enemyHp > 0) {
                enemy.setHp(enemyHp - bulletDoubleDaemon.getPrototype().getDamage());
            } else {
                enemy.setShootable(false);
                guiConsumer.consume(()->enemy.getHpView().hide());
                enemy.pushSprite(explodeSprite, 0,  aReturn2-> {
                    guiConsumer.consume(() -> {
                                enemy.getView().hide();
                    });
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
