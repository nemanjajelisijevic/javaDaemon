package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
import com.daemonize.daemondevapp.proba.Bullet;
import com.daemonize.daemondevapp.proba.Enemy;
import com.daemonize.daemondevapp.proba.ImageMoverM;
import com.daemonize.daemondevapp.proba.ImageMoverMDaemon;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemondevapp.tabel.Grid;
import com.daemonize.daemondevapp.tabel.PathFinding;
import com.daemonize.daemondevapp.view.AndroidImageView;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.consumer.androidconsumer.AndroidLooperConsumer;
import com.daemonize.daemonengine.dummy.DummyDaemon;
import com.daemonize.daemonengine.utils.DaemonUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private Game game;

    //private BackgroundScrollerDaemon backgroundScrollerDaemon;

    private ImageView background;
    private Bitmap backgroundImg;

//    private Bitmap fieldImage;
//    private Bitmap fieldImagePath;
//    private Bitmap fieldImageTower;
//    private Bitmap fieldImageTowerDen;

    private AndroidImageView[][] fieldViews;
    private ArrayList<ImageView> bulletsViews;

    public Grid grid;

    private DummyDaemon dummyDaemon;

//    //private List<Bitmap> sprite;
//    private List<Bitmap> spriteMain;
//    private List<Bitmap> bulletSprite;
//    private List<Bitmap> explosionSprite;
//    private List<Bitmap> bigExplosionSprite;

    private List<ImageMoverDaemon> starMovers;
    private List<ImageMoverMDaemon> enemyList;

    private ImageMoverDaemon mainMover;
    private ImageView mainView;
    private ImageMoverDaemon[][] mainMover2;


//    private MassiveImageMoverDaemon massiveDaemon;
    private List<ImageView> massiveViews;

    private int borderX;
    private int borderY;

    private long wastedCounter;
    private String wastedCntText = "EXPLOSIONS COUNTER: ";
    private TextView wastedCntView;

    public TextView hpView;
    private String hpText = "HP: ";

    public static class ImageMoveClosure implements Closure<ImageMover.PositionedBitmap> {

        protected ImageView view;

        public ImageMoveClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedBitmap> ret) {
//            ImageMover.PositionedBitmap returnVal = null;
//            try {
//                returnVal = ret.checkAndGet();
//                view.setX(returnVal.positionX);
//                view.setY(returnVal.positionY);
//                view.setImageBitmap(returnVal.image);
//            } catch (DaemonException e) {
//                e.printStackTrace();
//            }
            ImageMover.PositionedBitmap returnVal = ret.get();
            view.setX(returnVal.positionX);
            view.setY(returnVal.positionY);
            view.setImageBitmap(returnVal.image);
        }
    }

    public class ImageMoveMClosure implements Closure<ImageMoverM.PositionedBitmap> {

        protected ImageView view;

        public ImageMoveMClosure(ImageView view) {
            this.view = view;
        }

        @Override
        public void onReturn(Return<ImageMoverM.PositionedBitmap> ret) {
            ImageMoverM.PositionedBitmap returnVal = ret.get();
            view.setX(returnVal.positionX);
            view.setY(returnVal.positionY);
            view.setImageBitmap(returnVal.image);
        }
    }

    public class EnemyClosure extends ImageMoveMClosure {

        private ImageMoverMDaemon enemy;

        public EnemyClosure(ImageMoverMDaemon enemy, ImageView view) {
            super(view);
            this.enemy = enemy;
        }

        @Override
        public void onReturn(Return<ImageMoverM.PositionedBitmap> ret) {

            ImageMoverM.PositionedBitmap pb = ret.get();

            if (pb.positionX >= 20 *80 || pb.positionY >= 11* 80) {
                enemy.stop();
                return;
            }

            Field field = grid.getField(pb.positionX, pb.positionY);

            int row = field.getRow();
            int column = field.getColumn();

            if(row == 6 && column == 12) {
                grid.setTower(8, 14);
                //fieldViews[8][14].setImageBitmap(fieldImageTower);
            }

//            if (row > 0 && column > 0)
//                fieldViews[row - 1][column - 1].setImageBitmap(fieldImage);

            //fieldViews[row][column].setImageBitmap(fieldImagePath);

            super.onReturn(ret);
        }
    }

//    private class BulletClosure extends ImageMoveMClosure {
//
//        private ImageMoverMDaemon bulletDaemon;
//
//        public BulletClosure(ImageView view, ImageMoverMDaemon bulletdaemon) {
//            super(view);
//            this.bulletDaemon = bulletdaemon;
//        }
//
//        @Override
//        public void onReturn(Return<ImageMoverM.PositionedBitmap> ret) {
//
//            if (
//                    ret.get().positionX <= 20
//                    || ret.get().positionX >= borderX - 20
//                    || ret.get().positionY <= 20
//                    || ret.get().positionY >= borderY - 20
//                    ) {
//
//                bulletDaemon.stop();
//                layout.removeView(view);
//                return;
//            }
//
//            for (ImageMoverMDaemon enemy : enemyList) {
//                Pair<Float, Float> enemyPos = enemy.getLastCoordinates();
//                if(Math.abs(ret.get().positionX - enemyPos.first) <= bulletSprite.get(0).getWidth()
//                        && Math.abs(ret.get().positionY - enemyPos.second) <= bulletSprite.get(0).getHeight()) {
//
//                    Enemy prototype = (Enemy) enemy.getPrototype();
//
//                    //if (!prototype.isExploading()) {
//
//                        bulletDaemon.stop();
//                        layout.removeView(view);
//
//                        wastedCntView.setText(wastedCntText + Long.toString(++wastedCounter));
//                        prototype.setHp(prototype.getHp() - 1);
////                        if (prototype.getHp() == 0) {
////                            enemy.explode(
////                                    binderm.bindViewToClosureM(prototype.getView()),
////                                    ret1 -> {
////                                        prototype.getView().setImageBitmap(ret1.get().image);
////                                        prototype.setLastCoordinates(
////                                                getRandomInt(0, borderX),
////                                                getRandomInt(0, borderY)
////                                        );
////                                    }
////                            );
////                        }
//                }
//            }
//
//            super.onReturn(ret);
//        }
//    }

    private static int getRandomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public ImageView createImageView(int width, int height) {
        ImageView view = new ImageView(getApplicationContext());
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(lp);
        view.getLayoutParams().height = height;
        view.getLayoutParams().width = width;
        layout.addView(view);
        view.requestLayout();
        return view;
    }

    @FunctionalInterface
    private interface ViewBinderM {
        ImageMoveMClosure bindViewToClosureM(ImageView view);
    }

    private ViewBinderM binderm = ImageMoveMClosure::new;

   @FunctionalInterface
    private interface ViewBinder {
        ImageMoveClosure bindViewToClosure(ImageView view);
    }

    private ViewBinder binder = ImageMoveClosure::new;

    //keyboard controller
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_D:
//                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(100, 0)));
//                return true;
//            case KeyEvent.KEYCODE_A:
//                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(- 100, 0)));
//                return true;
//            case KeyEvent.KEYCODE_W:
//                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(0, - 100)));
//                return true;
//            case KeyEvent.KEYCODE_S:
//                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(0,  100)));
//                return true;
//
//            case KeyEvent.KEYCODE_L:
//                fireBullet(mainMover.getLastCoordinates(), 0);
//                fireBullet(mainMover.getLastCoordinates(),  - 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  - 0.1F);
//                fireBullet(mainMover.getLastCoordinates(),  0.2F);
//                return true;
//            case KeyEvent.KEYCODE_I:
//                fireBullet(mainMover.getLastCoordinates(), 90 * 0.0174533F);
//                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F - 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F + 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F - 0.1F);
//                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F + 0.2F);
//                return true;
//            case KeyEvent.KEYCODE_J:
//                fireBullet(mainMover.getLastCoordinates(), 180 * 0.0174533F);
//                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F - 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F + 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F - 0.1F);
//                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F  +0.1F);
//                return true;
//            case KeyEvent.KEYCODE_K:
//                fireBullet(mainMover.getLastCoordinates(), 270 * 0.0174533F);
//                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F - 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F + 0.2F);
//                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F - 0.1F);
//                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F  +0.1F);
//                return true;
//            default:
//                return super.onKeyDown(keyCode, event);
//        }
//    }

//    private void fireBullet(Pair<Float, Float> sourceCoord, Pair<Float, Float> enemyCoord ) {//float angleInRadians) {
//        ImageView bulletView = createImageView(40, 40);
//        ImageMoverMDaemon bullet = new ImageMoverMDaemon(
//                new Bullet(
//                        bulletSprite,
//                        new ImageMoverM.Velocity(
//                        10,
////                        new ImageMoverM.Direction((float) Math.cos(angleInRadians) * 100, -(float) Math.sin(angleInRadians) * 100)),
//                        new ImageMoverM.Direction(enemyCoord.first, enemyCoord.second)),
//                        1,
//                        Pair.create(
//                                sourceCoord.first + spriteMain.get(0).getWidth() / 2,
//                                sourceCoord.second + spriteMain.get(0).getHeight() / 2 )
//                ).setBorders(borderX, borderY)
//        ).setName("Bullet");
//
//        bullet.setVelocity(10);
//        bullet.setMoveSideQuest().setClosure(new BulletClosure(bulletView, bullet));
////        bullet.setVelocity(new ImageMover.Velocity(
////                50,
////                new ImageMover.Direction((float) Math.cos(angleInRadians) * 100, -(float) Math.sin(angleInRadians) * 100)
////        ));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.w("onCreate","Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        borderX = getResources().getDisplayMetrics().widthPixels - 100;
        borderY = getResources().getDisplayMetrics().heightPixels - 200;

        layout = findViewById(R.id.cl);

        background = findViewById(R.id.img_large);

        mainView = findViewById(R.id.imageViewMain);
        starMovers = new ArrayList<>(60);
        enemyList = new ArrayList<>(10);

        wastedCntView = findViewById(R.id.response);
        wastedCntView.setWidth(borderX / 3);
        wastedCntView.setHeight(borderY / 10);
        wastedCntView.setTextColor(WHITE);

        hpView = findViewById(R.id.hp);
        hpView.setWidth(borderX / 3);
        hpView.setHeight(borderY / 10);
        hpView.setTextColor(WHITE);




//        layout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
//                    if (motionEvent.getX() >= 20 *80 || motionEvent.getY() >= 11* 80) {
//                        return false;
//                    }
//                    Field field = grid.getField(motionEvent.getX(), motionEvent.getY());
//
//                    fieldViews[field.getRow()][field.getColumn()].setImageBitmap(fieldImageTower);
//
//                    boolean b = grid.setTower(field.getRow(), field.getColumn());
//                    Log.w("ADD TOWER", "Tower is " + (b ? " accept " : " rejected "));
//                    //fieldViews[field.getRow()][field.getColumn()].setImageBitmap(b ? fieldImageTower : (haveTower?fieldImageTower:fieldImageTowerDen));
//                    fieldViews[field.getRow()][field.getColumn()].setImageBitmap(grid.getField(field.getRow(), field.getColumn()).isWalkable() ? (!b ? fieldImageTowerDen : fieldImage) : fieldImageTower);
//                    if (b) {
//
//                        mainMover2[field.getRow()][field.getColumn()] = new ImageMoverDaemon(
//                                new MainImageTranslationMover(
//                                        spriteMain,
//                                        1f,
//                                        Pair.create((float)field.getCenterX(), (float)field.getCenterY()),
//                                        enemyList
//                                )
//                                        .setBorders(borderX, borderY)
//                                        .setHpClosure(hp -> {
//                                            Log.w("HpClosure", "izvrsavanje");
//                                            if (hp.get() <= 0) {
//                                                //                        for (ImageMoverDaemon star : starMovers) {
//                                                //                            //star.stop();
//                                                //                            BouncingImageTranslationMover prototype = ((BouncingImageTranslationMover) star.getPrototype());
//                                                //                            star.explode(explosionSprite,
//                                                //                                    binder.bindViewToClosure(prototype.getView()),
//                                                //                                    ret1 -> {
//                                                //                                        prototype.getView().setImageBitmap(ret1.get().image);
//                                                //                                        prototype.setLastCoordinates(
//                                                //                                                getRandomInt(0, borderX),
//                                                //                                                getRandomInt(0, borderY)
//                                                //                                        );
//                                                //                                        star.stop();
//                                                //                                    });
//                                                //                        }
//                                                wastedCounter = 0;
//                                                hpView.setTextColor(RED);
//                                                hpView.setText("!!!!!!WASTED!!!!!!!!!");
//                                               // ((MainImageTranslationMover) mainMover2.getPrototype()).setHp(1000);
//
//                                            } else {
//
//                                                if (hp.get() % 50 == 0) {
//                                                //    ((MainImageTranslationMover) mainMover2.getPrototype()).pushSprite(bigExplosionSprite);
//                                                }
//
//                                                hpView.setTextColor(WHITE);
//                                                hpView.setText(hpText + new Integer(hp.get() / 10).toString());
//                                            }
//                                        })
//                                        .setBulletFireClosure(bf->{
//                                            Pair<Float,Float> enemyCoordinates = bf.get();
//
//                                            //Math.sin();
//                                            //fireBullet(mainMover.getLastCoordinates(), 270 * 0.0174533F);
//                                            fireBullet(mainMover2[field.getRow()][field.getColumn()].getLastCoordinates(),  enemyCoordinates);
//
//                                        })
//                        ).setName("Exceptione");
//                        mainMover2[field.getRow()][field.getColumn()].setMoveSideQuest().setClosure(binder.bindViewToClosure(fieldViews[field.getRow()][field.getColumn()]));
//                        mainMover2[field.getRow()][field.getColumn()].start();
//                    }
//                }
//                return true;
//            }
//        });


//        int row = 11;
//        int column = 20;
//        grid = new Grid(row, column, new Pair<>(0,0),new Pair<>(row - 1,column - 1));

//
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(1,2) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(1,3) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(1,0) ? " accept ":" rejected "));
////        Log.w("ADD TOWER","Tower is " + (grid.setTower(1,1) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(1,4) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(1,5) ? " accept ":" rejected "));
//
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(2,5) ? " accept ":" rejected "));
//
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,2) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,3) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,4) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,5) ? " accept ":" rejected "));
////        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,1) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,5) ? " accept ":" rejected "));
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(3,0) ? " accept ":" rejected "));
//
//        Log.w("ADD TOWER","Tower is " + (grid.setTower(5,10) ? " accept ":" rejected "));
//
//
//                grid.setTower(4,1);
//                grid.setTower(4,3);
//                grid.setTower(4,5);
//
//
//                grid.setTower(5,2);
//                grid.setTower(5,0);
//                grid.setTower(5,4);

        //pathFinding.pathToString();
//        Log.w("border","x: "+borderX+" y: "+borderY);
//        //        pathFinding.getGrid().getPath()
//        try {
//
//            {
//                fieldImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), 80, 80, false);
//                fieldImagePath = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), 80, 80, false);
//                fieldImageTower = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("blue.png")), 80, 80, false);
//                fieldImageTowerDen = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("red.png")), 80, 80, false);
//            }
//
//            bulletsViews = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                bulletsViews.add(createImageView(fieldImage.getWidth(), fieldImage.getHeight()));
//            }

//            fieldViews = new ImageView[row][column];
//            mainMover2 =  new ImageMoverDaemon[row][column];
//
//            for(int j = 0; j < row; ++j ) {
//                for (int i = 0; i < column; ++i) {
//
//                    ImageView view = createImageView(fieldImage.getWidth(), fieldImage.getHeight());
//                    fieldViews[j][i] = view;
//                    view.setX(grid.getGrid()[j][i].getCenterX() - (fieldImage.getWidth() / 2) + 40);
//                    view.setY(grid.getGrid()[j][i].getCenterY() - (fieldImage.getHeight() / 2) + 40);
//                    view.setImageBitmap(grid.getField(j,i).isWalkable()?fieldImage:fieldImageTower);
//                }
//            }
//
//            Log.w("ADD TOWER","Tower is " + (grid.setTower(5,10) ? " accept ":" rejected "));
//            fieldViews[5][10].setImageBitmap(fieldImageTower);

//
//            backgroundImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("maphi.jpg")), borderX, borderY, false);
//            background.setImageBitmap(backgroundImg);

//            sprite = new ArrayList<>();
//
//            int i = 0;
//            for (; i < 3; ++i) {
//                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar.png")), 80, 80, false));
//            }
//
//            for (; i < 6; ++i) {
//                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar90.png")), 80, 80, false));
//            }
//
//            for (; i < 9; ++i) {
//                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar180.png")), 80, 80, false));
//            }
//
//            for (; i < 12; ++i) {
//                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar270.png")), 80, 80, false));
//            }
//
//            bulletSprite = new ArrayList<>();
//            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), 40, 40, false));
//            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), 40, 40, false));
//            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), 60, 40, false));
//            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), 40, 40, false));
//
//            explosionSprite = new ArrayList<>();
//
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")), 80, 80, false));
//
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")), 80, 80, false));
//
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")), 80, 80, false));
//
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")), 80, 80, false));
//            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")), 80, 80, false));
//
//            bigExplosionSprite = new ArrayList<>();
//
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")), 200, 200, false));
//
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")), 200, 200, false));
//
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")), 200, 200, false));
//
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")), 200, 200, false));
//            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")), 200, 200, false));
//
//            spriteMain = new ArrayList<>();
//            int withM = 80;
//            int heightM = 80;
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), withM, heightM, false));
//
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), withM, heightM, false));
//            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), withM, heightM, false));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        List<ImageMover> masivePrototypes = new ArrayList<>(10);
//        massiveViews = new ArrayList<>(10);
//
//        for (int k = 0; k < 10; ++k) {
//            masivePrototypes.add(
//                    new BouncingImageTranslationMover(
//                            bulletSprite,
//                            10,
//                            Pair.create(
//                                    (float)getRandomInt(borderX / 4, borderX * 3 / 4),
//                                    (float)getRandomInt(borderY / 4, borderY * 3 / 4)
//                            )
//                    ).setBorders(borderX, borderY)
//            );
//            massiveViews.add(createImageView(60, 60));
//        }


//        massiveDaemon = new MassiveImageMoverDaemon(
//                new MassiveImageTranslationMover(masivePrototypes)
//        ).setName("MASSIVEEEE");
//
//        massiveDaemon.setSideQuest(massiveDaemon.moveSideQuest.setClosure(
//                ret -> {
//                    int i = 0;
//                    for (ImageMover.PositionedBitmap pb : ret.get()) {
//                        massiveViews.get(i).setX(pb.positionX);
//                        massiveViews.get(i).setY(pb.positionY);
//                        massiveViews.get(i).setImageBitmap(pb.image);
//                        i++;
//                    }
//                }));
//
//        massiveDaemon.start();

//        ImageView enemyView = createImageView(80,80);
//        ImageMoverMDaemon enemy;
//        enemy = new ImageMoverMDaemon(
//                new Enemy(
//                        20,
//                        sprite,
//                        explosionSprite,
//                        new ImageMoverM.Velocity(
//                                3,
//                                new ImageMoverM.Direction(
//                                        (float) borderX/2,
//                                        (float) borderY/2
//                                )
//                        ),
//                        Pair.create( (float)0, (float)0),
//                        grid
//                        ).setView(enemyView)
//                        .setBorders(borderX,borderY)
//        ).setName("Enemy");
//
//        enemy.setMoveSideQuest().setClosure(new EnemyClosure(enemy, enemyView));
//        enemy.start();
//
//        enemyList.add(enemy);


//        int i = 5;
//        for(int j = 0; j < 40; ++j) {
//
//            ImageView view = createImageView(80, 80);
//            ImageMoverDaemon starMover = new ImageMoverDaemon(
//                    new BouncingImageTranslationMover(
//                            sprite,
//                            8,
//                            Pair.create(
//                                    (float) borderX % i,
//                                    (float) borderY % i
//                            )
//                    ).setBorders(borderX, borderY).setView(view)
//            ).setName("Star " + Integer.toString(i));
//            starMover.setMoveSideQuest().setClosure(binder.bindViewToClosure(view));
//            starMover.start();
//
//            starMovers.add(starMover);
//            i += 5;

        //}

//        mainMover = new ImageMoverDaemon(
//                new MainImageTranslationMover(
//                        spriteMain,
//                        1f,
//                        Pair.create(borderX/2f, borderY/2f),
//                        enemyList
//                )
//                        .setBorders(borderX, borderY)
//                        .setHpClosure(hp -> {
//                            Log.w("HpClosure", "izvrsavanje");
//                            if (hp.get() <= 0) {
//                                //                        for (ImageMoverDaemon star : starMovers) {
//                                //                            //star.stop();
//                                //                            BouncingImageTranslationMover prototype = ((BouncingImageTranslationMover) star.getPrototype());
//                                //                            star.explode(explosionSprite,
//                                //                                    binder.bindViewToClosure(prototype.getView()),
//                                //                                    ret1 -> {
//                                //                                        prototype.getView().setImageBitmap(ret1.get().image);
//                                //                                        prototype.setLastCoordinates(
//                                //                                                getRandomInt(0, borderX),
//                                //                                                getRandomInt(0, borderY)
//                                //                                        );
//                                //                                        star.stop();
//                                //                                    });
//                                //                        }
//                                wastedCounter = 0;
//                                hpView.setTextColor(RED);
//                                hpView.setText("!!!!!!WASTED!!!!!!!!!");
//                                ((MainImageTranslationMover) mainMover.getPrototype()).setHp(1000);
//
//                            } else {
//
//                                if (hp.get() % 50 == 0) {
//                                    ((MainImageTranslationMover) mainMover.getPrototype()).pushSprite(bigExplosionSprite);
//                                }
//
//                                hpView.setTextColor(WHITE);
//                                hpView.setText(hpText + new Integer(hp.get() / 10).toString());
//                            }
//                        })
//                        .setBulletFireClosure(bf->{
//                            Pair<Float,Float> enemyCoordinates = bf.get();
//
//                            //Math.sin();
//                            //fireBullet(mainMover.getLastCoordinates(), 270 * 0.0174533F);
//                            fireBullet(mainMover.getLastCoordinates(),  enemyCoordinates);
//
//                        })
//        ).setName("Exceptione");
//        mainMover.setMoveSideQuest().setClosure(binder.bindViewToClosure(mainView));
//        mainMover.start();

        //mainView2 = createImageView(fieldImage.getWidth(), fieldImage.getHeight());


//        backgroundScrollerDaemon = new BackgroundScrollerDaemon(new BackgroundScroller(mainMover)).setName("Background scroller");
//        backgroundScrollerDaemon.setScrollSideQuest().setClosure(ret -> {
//            horizontalSv.scrollTo(ret.get().first, ret.get().second);
//            verticalSv.scrollTo(ret.get().first, ret.get().second);
//        });
//        backgroundScrollerDaemon.start();

//        joystickViewLeft = findViewById(R.id.joystickLeft);
//        joystickViewLeft.setOnMoveListener((angle, strength) -> {
//            if (strength > 0) {
//                float angleF = (float) angle * 0.0174533F;
//                float coeficientX = (float) Math.cos(angleF) * 100;
//                float coeficientY = -(float) Math.sin(angleF) * 100;
//                mainMover.setVelocity(new ImageMover.Velocity(
//                        strength / 8,
//                        new ImageMover.Direction(coeficientX, coeficientY)
//                ));
//            }
//        }, 100);
//
//        joystickViewRight = findViewById(R.id.joystickRight);
//        joystickViewRight.setOnMoveListener((angle, strength) -> {
//
//            if (mainMover.getVelocity().intensity < 1)
//                return;
//
//            float angleF = (float) angle * 0.0174533F;
//            Pair<Float, Float> lastMainCoord = mainMover.getLastCoordinates();
//
//            //bullet 1
//            fireBullet(lastMainCoord, angleF);
//            if (strength > 30) {
//                //bullet 2
//                fireBullet(lastMainCoord, angleF - 0.2F);
//                if (strength > 60) {
//                    //bullet 3
//                    fireBullet(lastMainCoord, angleF + 0.2F);
//                    if (strength > 70) {
//                        //bullet 4
//                        fireBullet(lastMainCoord, angleF - 0.1F);
//                        if (strength > 98) {
//                            //bullet 5
//                            fireBullet(lastMainCoord, angleF + 0.1F);
//                        }
//                    }
//                }
//            }
//        }, 100);

//
//        ExampleDaemon exampleDaemon = new ExampleDaemon(
//                new Example()
//        ).setName("ExampleDaemon");
//        exampleDaemon.evenMoreComplicated(
//                        "Constantly updated from another thread: ",
//                        update -> wastedCntView.setText(update.get()),
//                        ret -> {
//                            try {
//                                wastedCntView.setText(ret.checkAndGet());
//                            } catch (DaemonException e) {
//                                Log.e("DAEMON ERROR", Log.getStackTraceString(e));
//                                wastedCntView.setText(e.getMessage());
//                                return;
//                            }
//                            exampleDaemon.evenMoreComplicated(
//                                    "Here we go again: ",
//                                    update -> wastedCntView.setText(update.get()),
//                                    ret2 ->  wastedCntView.setText(ret2.get())
//                            );
//                        }
//                );


//        new RestClientTestScript(
//                wastedCntView,
//                new RestClientDaemon(new RestClient("https://reqres.in"))
//        ).run();

//        dummyDaemon = new DummyDaemon(
//                new AndroidLooperConsumer(),
//                1000
//        ).setClosure(ret-> fireBullet(Pair.create((float)0, (float)0), Pair.create((float) borderX, (float)borderY)));
//
//        dummyDaemon.start();
//
//        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();


        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////


        int rows = 11;
        int columns = 20;


        //init view matrix
        fieldViews = new AndroidImageView[rows][columns];
        for(int j = 0; j < rows; ++j ) {
            for (int i = 0; i < columns; ++i) {
                fieldViews[j][i] = new AndroidImageView(createImageView(80, 80)); //TODO unhardcode
            }
        }


        //init enemy view
        ImageView enemyView = createImageView(80,80);

        try {

            //init enemy sprite
            List<Bitmap> sprite = new ArrayList<>();

            int i = 0;
            for (; i < 3; ++i) {
                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar.png")), 80, 80, false));
            }

            for (; i < 6; ++i) {
                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar90.png")), 80, 80, false));
            }

            for (; i < 9; ++i) {
                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar180.png")), 80, 80, false));
            }

            for (; i < 12; ++i) {
                sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar270.png")), 80, 80, false));
            }

            //bullet sprite
            List<Bitmap> bulletSprite = new ArrayList<>();
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), 40, 40, false));
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), 40, 40, false));
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), 40, 40, false));
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), 40, 40, false));


            //explosion sprite
            List<Bitmap> explosionSprite = new ArrayList<>();

            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")), 80, 80, false));

            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")), 80, 80, false));

            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")), 80, 80, false));

            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")), 80, 80, false));
            explosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")), 80, 80, false));


            //TODO unnecessary background
            backgroundImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("maphi.jpg")), borderX, borderY, false);
            background.setImageBitmap(backgroundImg);


            Queue<DaemonView> enemyQueue = new LinkedList<>();

            for (int cnt = 0; cnt < 50; ++cnt) {
                enemyQueue.add(new AndroidImageView(createImageView(80, 80)));
            }

            Queue<DaemonView> bulletQueue = new LinkedList<>();

            for (int cnt = 0; cnt < 100; ++cnt) {
                bulletQueue.add(new AndroidImageView(createImageView(40, 40)));
            }

            game = new Game(rows, columns, fieldViews, enemyQueue, bulletQueue)
                    .setFieldImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), 80, 80, false))
                    .setFieldImagePath(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), 80, 80, false))
                    .setFieldImageTower(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 80, 80, false))
                    .setFieldImageTowerDen(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("red.png")), 80, 80, false))
                    .setEnemySprite(sprite)
                    .setBulletSprite(bulletSprite)
                    .setExplodeSprite(explosionSprite)
                    .setBorders(borderX, borderY);

        } catch (IOException ex) {
            Log.e(DaemonUtils.tag(), "Could not init game!", ex);
        }


//        ExampleDaemon exampleDaemon = new ExampleDaemon(
//                new Example()
//        ).setName("Example recursion");
//
//        exampleDaemon.increment(new Closure<String>() {
//            @Override
//            public void onReturn(Return<String> aReturn) {
//                Log.e(DaemonUtils.tag(), aReturn.get());
//                exampleDaemon.increment(this);
//            }
//        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            game.setTower(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    @Override
    public void onResume(){
        super.onResume();
        game.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        game.stop();

//        mainMover.stop();
//        for(ImageMoverDaemon mover : starMovers) {
//            mover.stop();
//        }
//        dummyDaemon.stop();
        //massiveDaemon.stop();
        //exampleDaemon.stop();
        //backgroundScrollerDaemon.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
