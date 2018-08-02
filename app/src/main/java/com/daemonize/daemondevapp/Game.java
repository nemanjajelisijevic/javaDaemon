package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Pair;

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

import java.util.List;
import java.util.Queue;


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


    private Queue<DaemonView> enemyViews;
    //private DaemonView enemyView;
    private List<Bitmap> enemySprite;
    //private ImageMoverMDaemon enemy;

    private DummyDaemon enemyGenerator;

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


    public Game(int rows, int columns, DaemonView[][] viewMatrix, Queue<DaemonView> enemyViews) {
        this.rows = rows;
        this.columns = columns;
        this.viewMatrix = viewMatrix;
        //TODO validate enemyViews
        this.enemyViews = enemyViews;

        this.grid = new Grid(rows, columns, Pair.create(0, 0), Pair.create(rows - 1, columns - 1));
        this.enemyGenerator = new DummyDaemon(gameConsumer, 2000).setClosure(ret->{

            ImageMoverMDaemon enemy = new ImageMoverMDaemon(
                    new Enemy(
                            20,
                            enemySprite,
                            enemySprite, //TODO fix explosions!!!!!!!!!!!!!!!!!!
                            new ImageMoverM.Velocity(
                                    3,
                                    new ImageMoverM.Direction(
                                            (float) borderX/2,
                                            (float) borderY/2
                                    )
                            ),
                            Pair.create( (float)0, (float)0),
                            grid
                    ).setBorders(borderX, borderY).setView(enemyViews.poll()) //TODO check null ref polling
            ).setName("Enemy").setConsumer(guiConsumer);

            enemy.setMoveSideQuest().setClosure(aReturn->{ //this will execute in android looper consumer!!!!
                ImageMoverM.PositionedBitmap posBmp = aReturn.get();

                if (posBmp.positionX >= 20 * 80 || posBmp.positionY >= 11 * 80) {
                    enemy.stop();
                    enemyViews.add(((Enemy) enemy.getPrototype()).getView());
                }

                ((Enemy) enemy.getPrototype()).getView().setX(posBmp.positionX);
                ((Enemy) enemy.getPrototype()).getView().setY(posBmp.positionY);
                ((Enemy) enemy.getPrototype()).getView().setImage(posBmp.image);
            });

            enemy.start();
        });
    }

    public Game run() {
        gameConsumer.start();
        chain.run();
        return this;
    }

    public Game stop(){
        enemyGenerator.stop();
        gameConsumer.stop();
        return this;
    }

    public Game setTower(float x, float y) { //TODO to be called from Activity.onTouch()

        if (x >= 20 * 80 || y >= 11 * 80) {
            return this;
        }

        Field field = grid.getField(x, y);

        viewMatrix[field.getRow()][field.getColumn()].setImage(fieldImageTower);

        boolean b = grid.setTower(field.getRow(), field.getColumn());
        viewMatrix[field.getRow()][field.getColumn()].setImage(
                grid.getField(
                        field.getRow(),
                        field.getColumn()
                ).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : fieldImageTower
        );

        return this;
    }



}
