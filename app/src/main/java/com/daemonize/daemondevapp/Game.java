package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemondevapp.proba.Bullet;
import com.daemonize.daemondevapp.proba.Enemy;
import com.daemonize.daemondevapp.proba.ImageMoverM;
import com.daemonize.daemondevapp.proba.ImageMoverMDaemon;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.view.DaemonView;
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
    private Set<ImageMoverMDaemon> activeEnemies = new HashSet<>();
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

    {
        //init spell (state)
        chain.addSpell(()->{

            grid.setTower(10, 9);// TODO recalc when instancing grid!

            enemyGenerator.setClosure(ret->{

                Log.d(DaemonUtils.tag(), "Enemy views queue size: " + enemyViews.size());

                DaemonView enemyView = enemyViews.poll();

                if (enemyView == null) //TODO this should never be null
                    return;

                ImageMoverMDaemon enemy = new ImageMoverMDaemon(
                        new Enemy(
                                30,
                                enemySprite,
                                explodeSprite, //TODO fix explosions!!!!!!!!!!!!!!!!!!
                                new ImageMoverM.Velocity(
                                        3,
                                        new ImageMoverM.Direction(
                                                (float) borderX/2,
                                                (float) borderY/2
                                        )
                                ),
                                Pair.create( (float)0, (float)0),
                                grid
                        ).setBorders(borderX, borderY).setView(enemyView) //TODO check null ref polling
                ).setName("Enemy").setConsumer(guiConsumer);

                enemy.setMoveSideQuest().setClosure(aReturn->{ //gui consumer

                    ImageMoverM.PositionedBitmap posBmp = aReturn.get();

                    //draw enemy
                    ((Enemy) enemy.getPrototype()).getView().setX(posBmp.positionX);
                    ((Enemy) enemy.getPrototype()).getView().setY(posBmp.positionY);
                    ((Enemy) enemy.getPrototype()).getView().setImage(posBmp.image);

                    //check if inside the map
                    gameConsumer.consume(()-> {
                        if (posBmp.positionX >= 20 * 80 || posBmp.positionY >= 11 * 80) {
                            enemy.stop();
                            activeEnemies.remove(enemy);
                            enemyViews.add(((Enemy) enemy.getPrototype()).getView().hide());
                        }
                    });
                });

                activeEnemies.add(enemy);
                enemy.start();
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

    public Game(int rows, int columns, DaemonView[][] viewMatrix, Queue<DaemonView> enemyViews, Queue<DaemonView> bulletQueue) {
        this.rows = rows;
        this.columns = columns;
        this.viewMatrix = viewMatrix;
        //TODO validate enemyViews
        this.enemyViews = enemyViews;
        this.grid = new Grid(rows, columns, Pair.create(0, 0), Pair.create(rows - 1, columns - 1));
        this.enemyGenerator = new DummyDaemon(gameConsumer, 2000);
        this.bulletQueue = bulletQueue;
    }

    public Game run() {
        gameConsumer.start();
        chain.run();
        return this;
    }

    public Game stop(){

        enemyGenerator.stop();

        for(ImageMoverMDaemon enemy : activeEnemies) {
            enemy.stop();
        }

        for (DummyDaemon tower : towers) {
            tower.stop();
        }

        gameConsumer.stop();

        return this;
    }

    public Game setTower(float x, float y) { //TODO to be called from Activity.onTouch()

        if (x >= 20 * 80 || y >= 11 * 80) {
            return this;
        }

        gameConsumer.consume(()-> {

            Field field = grid.getField(x, y);

            guiConsumer.consume(()->viewMatrix[field.getRow()][field.getColumn()].setImage(fieldImageTower));

            boolean b = grid.setTower(field.getRow(), field.getColumn());

            Bitmap image = grid.getField(
                    field.getRow(),
                    field.getColumn()
            ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : fieldImageTower;

            guiConsumer.consume(()-> viewMatrix[field.getRow()][field.getColumn()].setImage(image));

            if (b) {
                DummyDaemon tower = new DummyDaemon(gameConsumer, 1000).setClosure(ret -> {

                    for (ImageMoverMDaemon enemy : activeEnemies) {

                        Pair<Float, Float> enemyCoord = enemy.getLastCoordinates();

                        if (Math.abs((float) field.getCenterX() - enemyCoord.first) < 200
                                && Math.abs((float) field.getCenterY() - enemyCoord.second) < 200) {
                            fireBullet(Pair.create((float) field.getCenterX(), (float) field.getCenterY()), enemy);
                            break;
                        }
                    }

                });

                tower.start();
                towers.add(tower);
            }
        });

        return this;
    }

    private void fireBullet(Pair<Float, Float> sourceCoord, ImageMoverMDaemon enemy) {

        Pair<Float, Float> enemyCoord = enemy.getLastCoordinates();

        DaemonView bulletView = bulletQueue.poll();
        guiConsumer.consume(()->bulletView.show());

        ImageMoverMDaemon bullet = new ImageMoverMDaemon(//TODO keep active bullets to destroy them
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
        ).setName("Bullet").setConsumer(guiConsumer);

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

                    Enemy baddy = ((Enemy) enemy.getPrototype());
                    int enemyHp = baddy.getHp();
                    if (enemyHp > 0) {
                        baddy.setHp(--enemyHp);
                    } else {
                        enemy.explode(
                                aReturn -> ((Enemy) enemy.getPrototype()).getView().setImage(aReturn.get().image),
                                aReturn -> ((Enemy) enemy.getPrototype()).getView().hide()
                        );
                        enemy.queueStop();
                        activeEnemies.remove(enemy);
                        guiConsumer.consume(()-> baddy.getView().hide());
                        if (!enemyViews.contains(baddy.getView()))
                            enemyViews.add(baddy.getView());//TODO dead enemy should return a borrowed view
                    }
                    bullet.stop();
                    DaemonView view = ((Bullet) bullet.getPrototype()).getView();
                    guiConsumer.consume(()->view.hide());
                    bulletQueue.add(view);
                }

            });


        });

        bullet.start();
    }




}
