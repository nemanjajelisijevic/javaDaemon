package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.view.Renderer;
import com.daemonize.daemondevapp.view.ImageView;
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
    private ImageView[][] viewMatrix;

    private Grid grid;

    private Image fieldImage;
    private Image fieldImagePath;
    private Image fieldImageTower;
    private Image fieldImageTowerDen;

    private List<TowerDaemon> towers = new ArrayList<>();
    private Queue<EnemyDoubleDaemon> enemyQueue = new LinkedList<>();
    private int towerShootInterval = 1500;
    private int range = 320;

    private Queue<ImageView> enemyViews;
    private Queue<ImageView> enemyHpViews;
    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();
    private Image [] enemySprite;

    private DummyDaemon enemyGenerator;
    private long enemyCounter;
    private float enemyVelocity = 1;
    private int enemyHp = 10;
    private long enemyGenerateinterval = 5000;

    private Image [] healthBarImage;

    public Game setHealthBarImage(Image [] healthBarImage) {
        this.healthBarImage = healthBarImage;
        return this;
    }

    private Queue<ImageView> bulletViewQueue;
    private Queue<BulletDoubleDaemon> bulletQueue = new LinkedList<>();
    private Image [] bulletSprite;

    private Image [] explodeSprite;

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

    public Game setTowerSprite(Image [] towerSprite) {
        this.towerSprite = towerSprite;
        return this;
    }

    private Image [] towerSprite;

    public Game setExplodeSprite(Image [] explodeSprite) {
        this.explodeSprite = explodeSprite;
        return this;
    }

    public Game setBulletSprite(Image [] bulletSprite) {
        this.bulletSprite = bulletSprite;
        return this;
    }

    public Game setEnemySprite(Image [] enemySprite) {
        this.enemySprite = enemySprite;
        return this;
    }

    public Game setFieldImage(Image fieldImage) {
        this.fieldImage = fieldImage;
        return this;
    }

    public Game setFieldImagePath(Image fieldImagePath) {
        this.fieldImagePath = fieldImagePath;
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

    Renderer renderer;

    public Game(
            Renderer renderer,
            int rows,
            int columns,
            ImageView[][] viewMatrix,
            Queue<ImageView> enemyViews,
            Queue<ImageView> enemyHpViewQueue,
            Queue<ImageView> bulletViewQueue,
            float x,
            float y,
            int fieldWidth) {
        this.renderer = renderer;
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
        renderer.start();
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
        renderer.stop();
        return this;
    }


    {
        //init spell (state)
        chain.addState(()-> {

                    Field firstField = grid.getField(0, 0);

                    for (ImageView enemyView : enemyViews) {
                        EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                                gameConsumer,
                                guiConsumer,
                                new Enemy(
                                        enemySprite,
                                        enemyVelocity,
                                        enemyHp,
                                        Pair.create((float) 0, (float) 0),
                                        Pair.create(firstField.getCenterX(), firstField.getCenterY())
                                ).setView(enemyView).setHpView(enemyHpViews.poll())
                                        .setHealthBarImage(healthBarImage)
                        ).setName("Enemy");

                        enemy.getPrototype().setBorders(borderX, borderY);

                        enemy.setAnimateEnemySideQuest().setClosure(new EnemyAnimateClosure());//gui consumer

                        enemyQueue.add(enemy);

                    }


                    for (ImageView bulletView : bulletViewQueue) {

                        BulletDoubleDaemon bulletDoubleDaemon = new BulletDoubleDaemon(
                                gameConsumer,
                                guiConsumer,
                                new Bullet(
                                        bulletSprite,
                                        0,
                                        Pair.create((float) 0, (float) 0),
                                        Pair.create((float) 0, (float) 0),
                                        2
                                ).setView(bulletView)
                        ).setName("Bullet");

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
                                            return;
                                        }
                            });

                            bulletDoubleDaemon.getView().setX(posBmp.positionX);
                            bulletDoubleDaemon.getView().setY(posBmp.positionY);
                            bulletDoubleDaemon.getView().setImage(posBmp.image);

                        });

                        bulletQueue.add(bulletDoubleDaemon);
                    }

                    chain.next();

        }).addState(()->{//gameState


            guiConsumer.consume(()->{
                for(int j = 0; j < rows; ++j ) {
                    for (int i = 0; i < columns; ++i) {
                        viewMatrix[j][i].setX(grid.getGrid()[j][i].getCenterX()/* - (fieldImage.getWidth() / 2)*/);
                        viewMatrix[j][i].setY(grid.getGrid()[j][i].getCenterY() /*- (fieldImage.getHeight() / 2)*/);
                        viewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower).show();
                    }
                }
            });

            Field firstField = grid.getField(0, 0);

            enemyGenerator = new DummyDaemon(gameConsumer, (int) enemyGenerateinterval).setClosure(ret->{

                enemyCounter++;

                Log.d(DaemonUtils.tag(), "Enemy queue size: " + enemyQueue.size());

                //every 20 enemys increase the pain!!!!
                if (enemyCounter % 15 == 0) {

                    if(enemyVelocity < 6)
                        enemyVelocity += 1;

                    if (enemyHp < 80) {
                        enemyHp += 5;//TODO pay attention to enemy max hp
                    }

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
                                else if (current.getColumn() ==  columns - 1 && current.getRow() == rows - 1) {
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

                if(towerShootInterval > 500) {
                    towerShootInterval -= 50;
                }

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

                Image [] initTowerSprite = new Image[1];
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
                //towerDaemon.setScanClosure(scanClosure);
                List<EnemyDoubleDaemon> clone = new ArrayList<EnemyDoubleDaemon>(activeEnemies.size());
                clone.addAll(activeEnemies);
                towerDaemon.scan(clone, towerScanClosure);
            }
        });

        return this;
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
