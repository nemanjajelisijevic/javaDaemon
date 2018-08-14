package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.EnemyDoubleDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.proba.Bullet;
import com.daemonize.daemondevapp.proba.ImageMoverM;
import com.daemonize.daemondevapp.proba.ImageMoverMDaemon;
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
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Game {

    private DaemonConsumer gameConsumer = new DaemonConsumer("Game Consumer");
    private DaemonChainScript chain = new DaemonChainScript();
    private Consumer guiConsumer = new AndroidLooperConsumer();

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

    private List<DummyDaemon> towers = new ArrayList<>();

    private Queue<DaemonView> enemyViews;
    private Set<EnemyDoubleDaemon> activeEnemies = new HashSet<>();
    private List<Bitmap> enemySprite;

    private DummyDaemon enemyGenerator;

    private Queue<DaemonView> bulletQueue;
    private List<Bitmap> bulletSprite;

    private List<Bitmap> explodeSprite;

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

    public Game(int rows, int columns, DaemonView[][] viewMatrix, Queue<DaemonView> enemyViews, Queue<DaemonView> bulletQueue) {
        this.rows = rows;
        this.columns = columns;
        this.viewMatrix = viewMatrix;
        //TODO validate enemyViews
        this.enemyViews = enemyViews;
        this.grid = new Grid(rows, columns, Pair.create(0, 0), Pair.create(rows - 1, 6 - 1));
        this.bulletQueue = bulletQueue;
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

        for (DummyDaemon tower : towers) {
            tower.stop();
        }

        gameConsumer.stop();

        return this;
    }

    {
        //init spell (state)
        chain.addSpell(()->{

            enemyGenerator = new DummyDaemon(gameConsumer, 2000).setClosure(ret->{

                Log.d(DaemonUtils.tag(), "Enemy views queue size: " + enemyViews.size());

                DaemonView enemyView = enemyViews.poll();

                if (enemyView == null)//TODO should never be null
                    return;

                guiConsumer.consume(()->enemyView.show());

                Field firstField = grid.getField(1, 1);

                EnemyDoubleDaemon enemy = new EnemyDoubleDaemon(
                        gameConsumer,
                        guiConsumer,
                        new CoordinatedImageTranslationMover(
                                enemySprite,
                                3,
                                Pair.create((float)0, (float)0),
                                Pair.create((float)firstField.getCenterX(), (float)firstField.getCenterY())
                        ).setView(enemyView)
                );

                enemy.getPrototype().setBorders(borderX, borderY);

                enemy.setMoveSideQuest().setClosure(aReturn -> {//gui consumer
                    ImageMover.PositionedBitmap posBmp = aReturn.get();
                    enemy.getView().setX(posBmp.positionX);
                    enemy.getView().setY(posBmp.positionY);
                    enemy.getView().setImage(posBmp.image);
                });

                enemy.start();

                activeEnemies.add(enemy);

                enemy.goTo(firstField.getCenterX(), firstField.getCenterY(), 3,
                        new Closure<Boolean>() {// gameConsumer
                            @Override
                            public void onReturn(Return<Boolean> aReturn) {
                                Pair<Float, Float> currentCoord = enemy.getPrototype().getLastCoordinates();
                                Field current = grid.getField(currentCoord.first, currentCoord.second);
                                Field next = grid.getMinWeightOfNeighbors(current);

                                enemy.goTo(next.getCenterX(), next.getCenterY(), 3, this);

                            }
                        }
                );

                enemy.start();

//                ImageMoverMDaemon enemy = new ImageMoverMDaemon(
//                        guiConsumer,
//                        new Enemy(
//                                40,
//                                enemySprite,
//                                explodeSprite,
//                                new ImageMoverM.Velocity(
//                                        3,
//                                        new ImageMoverM.Direction(
//                                                (float) borderX/2,
//                                                (float) borderY/2
//                                        )
//                                ),
//                                Pair.create( (float)0, (float)0),
//                                grid
//                        ).setBorders(borderX, borderY).setView(enemyView)
//                ).setName("Enemy");
//
//                enemy.setMoveSideQuest().setClosure(aReturn->{ //gui consumer
//
//                    ImageMoverM.PositionedBitmap posBmp = aReturn.get();
//
//                    //draw enemy
//                    ((Enemy) enemy.getPrototype()).getView().setX(posBmp.positionX);
//                    ((Enemy) enemy.getPrototype()).getView().setY(posBmp.positionY);
//                    ((Enemy) enemy.getPrototype()).getView().setImage(posBmp.image);
//
//                    //check if inside the map
//                    gameConsumer.consume(()-> {
//                        if (
//                            posBmp.positionX >= grid.getGridHeight() + 40 ||
//                            posBmp.positionY >= grid.getGridWidth() + 40 ||
//                            grid.getEndPoint().equals(grid.getFieldCoord(posBmp.positionX, posBmp.positionY))
//                                ) {
//                            enemy.stop();
//                            activeEnemies.remove(enemy);
//                            guiConsumer.consume(()->((Enemy) enemy.getPrototype()).getView().hide());
//                            enemyViews.add(((Enemy) enemy.getPrototype()).getView());
//                        }
//
//                    });
//                });
//
//                activeEnemies.add(enemy);
//                enemy.start();


            });

            enemyGenerator.start();

            guiConsumer.consume(()->{
                for(int j = 0; j < rows; ++j ) {
                    for (int i = 0; i < columns; ++i) {
                        viewMatrix[j][i].setX(grid.getGrid()[j][i].getCenterX() - (fieldImage.getWidth() / 2) + 40);
                        viewMatrix[j][i].setY(grid.getGrid()[j][i].getCenterY() - (fieldImage.getHeight() / 2) + 40);
                        viewMatrix[j][i].setImage(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower);
                    }
                }
            });

        });

    }

    public Game setTower(float x, float y) { //TODO to be called from Activity.onTouch()

        if (x >= 20 * 80 || y >= 11 * 80) {
            return this;
        }

        gameConsumer.consume(()-> {

            Field field = grid.getField(x, y);

//            if (field.getTower() != null) {
//                field.getTower().stop();
//                field.setTower(null);
//                guiConsumer.consume(()-> viewMatrix[field.getRow()][field.getColumn()].setImage(fieldImage));
//                grid.recalcGrid();
//                return;
//            }

            guiConsumer.consume(()->viewMatrix[field.getRow()][field.getColumn()].setImage(fieldImageTower));

            boolean b = grid.setTower(field.getRow(), field.getColumn());

            Bitmap image = grid.getField(
                    field.getRow(),
                    field.getColumn()
            ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : fieldImageTower;

            guiConsumer.consume(()-> viewMatrix[field.getRow()][field.getColumn()].setImage(image));

            if (b) {
                DummyDaemon tower = new DummyDaemon(gameConsumer, 1000).setClosure(ret -> {

                    for (EnemyDoubleDaemon enemy : activeEnemies) {

                        Pair<Float, Float> enemyCoord = enemy.getPrototype().getLastCoordinates();

                        if (Math.abs((float) field.getCenterX() - enemyCoord.first) < 200
                                && Math.abs((float) field.getCenterY() - enemyCoord.second) < 200) {
                            fireBullet(Pair.create((float) field.getCenterX(), (float) field.getCenterY()), enemy);
                            break;
                        }
                    }

                });

                tower.start();
                //field.setTower(tower);
                towers.add(tower);
            }
        });

        return this;
    }

    private void fireBullet(Pair<Float, Float> sourceCoord, EnemyDoubleDaemon enemy) {

        Pair<Float, Float> enemyCoord = enemy.getPrototype().getLastCoordinates();

        Log.i(DaemonUtils.tag(), "Bullet view queue size: " + bulletQueue.size());

        DaemonView bulletView = bulletQueue.poll();
        guiConsumer.consume(()->bulletView.show());

        ImageMoverMDaemon bullet = new ImageMoverMDaemon(//TODO keep active bullets to destroy them
                guiConsumer,
                new Bullet(
                        bulletSprite,
                        new ImageMoverM.Velocity(
                                13,
                                new ImageMoverM.Direction(enemyCoord.first, enemyCoord.second)
                        ),
                        1,
                        Pair.create(
                                sourceCoord.first + 40,
                                sourceCoord.second + 40
                        )
                ).setBorders(borderX, borderY).setView(bulletView)
        ).setName("Bullet");

        bullet.setMoveSideQuest().setClosure(ret->{ //gui thread

            ImageMoverM.PositionedBitmap posBmp = ret.get();

            //draw the bullet
            ((Bullet) bullet.getPrototype()).getView().setX(posBmp.positionX);
            ((Bullet) bullet.getPrototype()).getView().setY(posBmp.positionY);
            ((Bullet) bullet.getPrototype()).getView().setImage(posBmp.image);

            //if hits enemy or goes out of the map borders destroy
            gameConsumer.consume(() -> {

                if (posBmp.positionX >= 20 * 80 || posBmp.positionY >= 11 * 80) {
                    bullet.stop();
                    bulletQueue.add(((Bullet) bullet.getPrototype()).getView());
                } else if (Math.abs(posBmp.positionX - enemyCoord.first) <= bulletSprite.get(0).getWidth()
                        && Math.abs(ret.get().positionY - enemyCoord.second) <= bulletSprite.get(0).getHeight()) {

                    //EnemyDaemon baddy = enemy.getPrototype();
                    int enemyHp = enemy.getHp();
                    if (enemyHp > 0) {
                        enemy.setHp(--enemyHp);
                    } else {
//                        enemy.explode(
//                                aReturn -> ((Enemy) enemy.getPrototype()).getView().setImage(aReturn.get().image),
//                                aReturn -> ((Enemy) enemy.getPrototype()).getView().hide());
                        //enemy.pushSprite(explodeSprite, aReturn -> {});
                        enemy.stop();
                        guiConsumer.consume(()->enemy.getView().hide());
                        activeEnemies.remove(enemy);
                        if (!enemyViews.contains(enemy.getView()))
                            enemyViews.add(enemy.getView());//TODO dead enemy should return a borrowed view


                        activeEnemies.remove(enemy);
                        if (!enemyViews.contains(enemy.getView()))
                            enemyViews.add(enemy.getView());//TODO dead enemy should return a borrowed view
                    }
                    bullet.stop();
                    DaemonView view = ((Bullet) bullet.getPrototype()).getView();
                    guiConsumer.consume(()->view.hide());
                    if(!bulletQueue.contains(view))
                        bulletQueue.add(view);
                }

            });


        });

        bullet.start();
    }




}
