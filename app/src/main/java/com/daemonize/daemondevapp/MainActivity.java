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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
import com.daemonize.daemondevapp.proba.Enemy;
import com.daemonize.daemondevapp.proba.ImageMoverM;
import com.daemonize.daemondevapp.proba.ImageMoverMDaemon;
import com.daemonize.daemondevapp.tabel.PathFinding;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;

    private HorizontalScrollView horizontalSv;
    private ScrollView verticalSv;

    private BackgroundScrollerDaemon backgroundScrollerDaemon;

    private ImageView background;
    private Bitmap backgroundImg;


    private List<Bitmap> sprite;
    private List<Bitmap> spriteMain;
    private List<Bitmap> bulletSprite;
    private List<Bitmap> explosionSprite;
    private List<Bitmap> bigExplosionSprite;

    private List<ImageMoverDaemon> starMovers;
    private List<ImageMoverMDaemon> enemyList;

    private ImageMoverDaemon mainMover;
    private ImageView mainView;

//    private MassiveImageMoverDaemon massiveDaemon;
//    private List<ImageView> massiveViews;

    private int borderX;
    private int borderY;

    private JoystickView joystickViewLeft;
    private JoystickView joystickViewRight;

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

    public static class ImageMoveMClosure implements Closure<ImageMoverM.PositionedBitmap> {

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

    private class BulletClosure extends ImageMoveClosure {

        private ImageMoverDaemon bulletDaemon;

        public BulletClosure(ImageView view, ImageMoverDaemon bulletdaemon) {
            super(view);
            this.bulletDaemon = bulletdaemon;
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedBitmap> ret) {

            if (
                    ret.get().positionX <= 20
                    || ret.get().positionX >= borderX - 20
                    || ret.get().positionY <= 20
                    || ret.get().positionY >= borderY - 20
                    ) {

                bulletDaemon.stop();
                layout.removeView(view);
                return;
            }

            for (ImageMoverMDaemon enemy : enemyList) {
                Pair<Float, Float> enemyPos = enemy.getLastCoordinates();
                if(Math.abs(ret.get().positionX - enemyPos.first) <= bulletSprite.get(0).getWidth()
                        && Math.abs(ret.get().positionY - enemyPos.second) <= bulletSprite.get(0).getHeight()) {

                    Enemy prototype = (Enemy) enemy.getPrototype();

                    //if (!prototype.isExploading()) {

                        bulletDaemon.stop();
                        layout.removeView(view);

                        wastedCntView.setText(wastedCntText + Long.toString(++wastedCounter));
                        enemy.explode(
                                binderm.bindViewToClosureM(prototype.getView()),
                                ret1 -> {
                                    prototype.getView().setImageBitmap(ret1.get().image);
                                    prototype.setLastCoordinates(
                                            getRandomInt(0, borderX),
                                            getRandomInt(0, borderY)
                                    );
                                }
                        );
                    //}
                }
            }

            super.onReturn(ret);
        }
    }

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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_D:
                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(100, 0)));
                return true;
            case KeyEvent.KEYCODE_A:
                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(- 100, 0)));
                return true;
            case KeyEvent.KEYCODE_W:
                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(0, - 100)));
                return true;
            case KeyEvent.KEYCODE_S:
                mainMover.setVelocity(new ImageMover.Velocity(15, new ImageMover.Direction(0,  100)));
                return true;

            case KeyEvent.KEYCODE_L:
                fireBullet(mainMover.getLastCoordinates(), 0);
                fireBullet(mainMover.getLastCoordinates(),  - 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  0.2F);
                fireBullet(mainMover.getLastCoordinates(),  - 0.1F);
                fireBullet(mainMover.getLastCoordinates(),  0.2F);
                return true;
            case KeyEvent.KEYCODE_I:
                fireBullet(mainMover.getLastCoordinates(), 90 * 0.0174533F);
                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F - 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F + 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F - 0.1F);
                fireBullet(mainMover.getLastCoordinates(),  90 * 0.0174533F + 0.2F);
                return true;
            case KeyEvent.KEYCODE_J:
                fireBullet(mainMover.getLastCoordinates(), 180 * 0.0174533F);
                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F - 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F + 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F - 0.1F);
                fireBullet(mainMover.getLastCoordinates(),  180 * 0.0174533F  +0.1F);
                return true;
            case KeyEvent.KEYCODE_K:
                fireBullet(mainMover.getLastCoordinates(), 270 * 0.0174533F);
                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F - 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F + 0.2F);
                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F - 0.1F);
                fireBullet(mainMover.getLastCoordinates(),  270 * 0.0174533F  +0.1F);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void fireBullet(Pair<Float, Float> sourceCoord, float angleInRadians) {
        ImageView bulletView = createImageView(40, 40);
        ImageMoverDaemon bullet = new ImageMoverDaemon(
                new ImageTranslationMover(
                        bulletSprite,
                        50,
                        Pair.create(
                                sourceCoord.first + spriteMain.get(0).getWidth() / 2,
                                sourceCoord.second + spriteMain.get(0).getHeight() / 2
                        )
                ).setBorders(borderX, borderY)
        ).setName("Bullet");

        bullet.setVelocity(50);
        bullet.setMoveSideQuest().setClosure(new BulletClosure(bulletView, bullet));
        bullet.setVelocity(new ImageMover.Velocity(
                50,
                new ImageMover.Direction((float) Math.cos(angleInRadians) * 100, -(float) Math.sin(angleInRadians) * 100)
        ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.w("onCreate","Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        borderX = getResources().getDisplayMetrics().widthPixels - 100;
        borderY = getResources().getDisplayMetrics().heightPixels - 200;

        layout = findViewById(R.id.cl);
        horizontalSv = findViewById(R.id.horizontalSv);
        verticalSv = findViewById(R.id.verticalSv);

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

        int row = 6;
        int colon = 6;
        //        Field[][] playGround = initPlayGround( row,colon);
//        setWeightOnPlayGroundFromEndPoint(playGround,row,colon);
//
//        setTowerAndRecalculateWeight2(playGround,1,0);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//        setTowerAndRecalculateWeight2(playGround,1,1);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//        setTowerAndRecalculateWeight2(playGround,1,2);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//
//        setTowerAndRecalculateWeight2(playGround,2,4);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//
//
//        setTowerAndRecalculateWeight2(playGround,3,1);
//        Log.w("matrica putanja3\n", findPathToEnd(playGround,row,colon));
//        setTowerAndRecalculateWeight2(playGround,3,2);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//        setTowerAndRecalculateWeight2(playGround,3,3);
//        Log.w("matrica putanja3\n", findPathToEnd(playGround,row,colon));
//        setTowerAndRecalculateWeight2(playGround,3,5);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//
//        setTowerAndRecalculateWeight2(playGround,4,1);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//        setTowerAndRecalculateWeight2(playGround,4,4);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//
////        setTowerAndRecalculateWeight(playGround,1,4);
////        Log.w("matrica putanja\n", findPathToEnd(playGround,6,6));
//
//        setTowerAndRecalculateWeight2(playGround,5,3);
//        Log.w("matrica putanja\n", findPathToEnd(playGround,row,colon));
//


        PathFinding pathFinding = new PathFinding(row,colon,new Pair<>(0,0),new Pair<>(row - 1,colon - 1));
        //pathFinding.pathToString();
        Log.w("border","x: "+borderX+" y: "+borderY);
        //        pathFinding.getGrid().getPath()
        try {

            backgroundImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("maphi.jpg")), 2 * borderX, 2 * borderY, false);
            background.setImageBitmap(backgroundImg);

            sprite = new ArrayList<>();

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

            bulletSprite = new ArrayList<>();
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), 40, 40, false));
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), 40, 40, false));
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), 60, 40, false));
            bulletSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), 40, 40, false));

            explosionSprite = new ArrayList<>();

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

            bigExplosionSprite = new ArrayList<>();

            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")), 200, 200, false));

            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")), 200, 200, false));

            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")), 200, 200, false));

            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")), 200, 200, false));
            bigExplosionSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")), 200, 200, false));

            spriteMain = new ArrayList<>();

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione30.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione20.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione10.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione330.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione340.png")), 150, 150, false));

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione350.png")), 150, 150, false));

        } catch (IOException e) {
            e.printStackTrace();
        }

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

        ImageView enemyView = createImageView(80,80);
        ImageMoverMDaemon enemy;
        enemy = new ImageMoverMDaemon(
                new Enemy(
                        3,
                        sprite,
                        explosionSprite,
                        new ImageMoverM.Velocity(
                                3,
                                new ImageMoverM.Direction(
                                        (float) borderX/2,
                                        (float) borderY/2
                                )
                        ),
                        Pair.create( (float)0, (float)0),
                        pathFinding.getGrid().getPath()
                        ).setView(enemyView)
        ).setName("Enemy");

        enemy.setMoveSideQuest().setClosure(binderm.bindViewToClosureM(enemyView));
        enemy.start();

        enemyList.add(enemy);


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

        mainMover = new ImageMoverDaemon(
                new MainImageTranslationMover(
                        spriteMain,
                        1f,
                        Pair.create(borderX/2f, borderY/2f),
                        enemyList
                )
                .setBorders(borderX, borderY)
                .setHpClosure(hp -> {
                    if (hp.get() <= 0) {
//                        for (ImageMoverDaemon star : starMovers) {
//                            //star.stop();
//                            BouncingImageTranslationMover prototype = ((BouncingImageTranslationMover) star.getPrototype());
//                            star.explode(explosionSprite,
//                                    binder.bindViewToClosure(prototype.getView()),
//                                    ret1 -> {
//                                        prototype.getView().setImageBitmap(ret1.get().image);
//                                        prototype.setLastCoordinates(
//                                                getRandomInt(0, borderX),
//                                                getRandomInt(0, borderY)
//                                        );
//                                        star.stop();
//                                    });
//                        }
                        wastedCounter = 0;
                        hpView.setTextColor(RED);
                        hpView.setText("!!!!!!WASTED!!!!!!!!!");
                        ((MainImageTranslationMover) mainMover.getPrototype()).setHp(1000);

                    } else {

                        if (hp.get() % 50 == 0) {
                            ((MainImageTranslationMover) mainMover.getPrototype()).pushSprite(bigExplosionSprite);
                        }

                        hpView.setTextColor(WHITE);
                        hpView.setText(hpText + new Integer(hp.get() / 10).toString());
                    }
                })
        ).setName("Exceptione");
        mainMover.setMoveSideQuest().setClosure(binder.bindViewToClosure(mainView));
        mainMover.start();

        backgroundScrollerDaemon = new BackgroundScrollerDaemon(new BackgroundScroller(mainMover)).setName("Background scroller");
        backgroundScrollerDaemon.setScrollSideQuest().setClosure(ret -> {
            horizontalSv.scrollTo(ret.get().first, ret.get().second);
            verticalSv.scrollTo(ret.get().first, ret.get().second);
        });
        backgroundScrollerDaemon.start();

        joystickViewLeft = findViewById(R.id.joystickLeft);
        joystickViewLeft.setOnMoveListener((angle, strength) -> {
            if (strength > 0) {
                float angleF = (float) angle * 0.0174533F;
                float coeficientX = (float) Math.cos(angleF) * 100;
                float coeficientY = -(float) Math.sin(angleF) * 100;
                mainMover.setVelocity(new ImageMover.Velocity(
                        strength / 8,
                        new ImageMover.Direction(coeficientX, coeficientY)
                ));
            }
        }, 100);

        joystickViewRight = findViewById(R.id.joystickRight);
        joystickViewRight.setOnMoveListener((angle, strength) -> {

            if (mainMover.getVelocity().intensity < 1)
                return;

            float angleF = (float) angle * 0.0174533F;
            Pair<Float, Float> lastMainCoord = mainMover.getLastCoordinates();

            //bullet 1
            fireBullet(lastMainCoord, angleF);
            if (strength > 30) {
                //bullet 2
                fireBullet(lastMainCoord, angleF - 0.2F);
                if (strength > 60) {
                    //bullet 3
                    fireBullet(lastMainCoord, angleF + 0.2F);
                    if (strength > 70) {
                        //bullet 4
                        fireBullet(lastMainCoord, angleF - 0.1F);
                        if (strength > 98) {
                            //bullet 5
                            fireBullet(lastMainCoord, angleF + 0.1F);
                        }
                    }
                }
            }
        }, 100);

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

        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPause() {
        super.onPause();

        mainMover.stop();
        for(ImageMoverDaemon mover : starMovers) {
            mover.stop();
        }
        //massiveDaemon.stop();
        //exampleDaemon.stop();
        backgroundScrollerDaemon.stop();
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

    private class Field {
        int centerX;
        int centerY;
        int row; //i - n
        int colon;//j - m
        int weight;

        public Field(int centerX, int centerY, int row, int colon, int weight) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.row = row;
            this.colon = colon;
            this.weight = weight;
        }

        public int getCenterX() {
            return centerX;
        }

        public void setCenterX(int centerX) {
            this.centerX = centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        public void setCenterY(int centerY) {
            this.centerY = centerY;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColon() {
            return colon;
        }

        public void setColon(int colon) {
            this.colon = colon;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    int endPointJ ;
    int endPointI;

    public Field[][] initPlayGround(int n, int m){
        Field[][] playGround = new Field[n][m];
        //initialize playGround on start
        for (int i = 0; i < n;i++){
            for(int j=0; j<m;j++){
                Field field = new Field(i,j,i,j,0);
                if (i == (n-1) && (j%2) == 0){
                    field.setWeight(Integer.MAX_VALUE);
                }
                playGround[i][j] = field;
            }
        }
        return playGround;
    }
    // endPointI<playGround
    public void setWeightOnPlayGroundFromEndPoint(Field[][] playGround, int endPointI, int endPointJ){
        Field temp = playGround[endPointI-1][endPointJ-1];
        temp.setWeight(1);
        this.endPointI = endPointI;
        this.endPointJ = endPointJ;
        int row = playGround.length;
        int colon = playGround[0].length;
        for (int j = colon - 1 ;  j > -1; j-- ) {
            for (int i = row - 1; i > -1; i--) {
                Field field = playGround[i][j];
                if (field.getWeight() == 0) {

                    int minWeight = getMinWeightOfNeighbors(playGround,i,j);
                    field.setWeight(minWeight + 1);

                }

            }
        }
        String str = toString(playGround);
        Log.w("matrica\n",str);
    }

    public String toString(Field[][] playGround){
        String str = "" ;
        for (int i=0;i<playGround.length;i++){
            for(int j = 0;j<playGround[i].length;j++){
                String pomstr = playGround[i][j].getWeight() == Integer.MAX_VALUE ? "T" : playGround[i][j].getWeight()+"";
                str +="\t"+pomstr+"\t";
            }
            str+='\n';
        }
        return str;
    }

    public int getMinWeightOfNeighbors(Field[][] playGround, int i, int j){
        int minWeight = Integer.MAX_VALUE ;
        int iMax = playGround.length;
        int jMax = playGround[0].length;
        //List<Integer> neighborWight = new ArrayList<>(4);
        if(j%2==0){ //parni
            if (j+1 < jMax) {
                int weight = playGround[i][j+1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j+1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                }
            }
        } else { //neparni
            if ( i-1 > -1 ){
                int weight = playGround[i-1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                }
                if ( j+1 < jMax ){
                    weight = playGround[i-1][j+1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                    }
                }
            }
            if ( j+1 < jMax ){
                int weight = playGround[i][j+1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                }
            }
            if ( i+1 < iMax ){
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                }
            }
        }
        return minWeight;
    }

    public Field getMinWeightOfNeighbors2(Field[][] playGround, int i, int j){
        int minWeight = Integer.MAX_VALUE ;
        Field minNeighbor = null;
        int iMax = playGround.length;
        int jMax = playGround[0].length;
        //List<Integer> neighborWight = new ArrayList<>(4);
        if(j%2==0){ //parni
            if (j+1 < jMax) {
                int weight = playGround[i][j+1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j+1];
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j+1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i+1][j+1];
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i+1][j];
                }
            }
            if ( i-1 > -1 ) {
                int weight = playGround[i-1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i-1][j];
                }
            }
            if (j-1 > -1) {
                int weight = playGround[i][j-1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j-1];
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j-1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i+1][j-1];
                    }
                }
            }
        } else { //neparni

            if ( j+1 < jMax ){
                int weight = playGround[i][j+1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j+1];
                }
                if ( i-1 > -1 ){
                    weight = playGround[i-1][j+1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i-1][j+1];
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i+1][j];
                }
            }
            if ( i-1 > -1 ) {
                int weight = playGround[i-1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i-1][j];
                }
            }
            if ( j-1 > -1 ){
                int weight = playGround[i][j-1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j-1];
                }
                if ( i-1 > -1 ){
                    weight = playGround[i-1][j-1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i-1][j-1];
                    }
                }
            }
        }

        return minNeighbor;
    }

    public void increaseNeighborsWeight(Field[][] playGround, int i, int j){
        int minWeight = Integer.MAX_VALUE ;
        Field minNeighbor = null;
        int iMax = playGround.length;
        int jMax = playGround[0].length;
        //List<Integer> neighborWight = new ArrayList<>(4);
        if(j%2==0){ //parni
            if (j+1 < jMax) {
                int weight = playGround[i][j+1].weight;
                if(weight != Integer.MAX_VALUE ) {
                    weight+=1;
                    playGround[i][j+1].setWeight(weight);
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j+1].weight;
                    if(weight != Integer.MAX_VALUE ) {
                        weight+=1;
                        playGround[i+1][j+1].setWeight(weight);
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    if(weight != Integer.MAX_VALUE ) {
                        weight+=1;
                        playGround[i+1][j].setWeight(weight);
                    }
                }
            }
            if ( i-1 > -1 ) {
                int weight = playGround[i-1][j].weight;
                if(weight != Integer.MAX_VALUE ) {
                    weight+=1;
                    playGround[i-1][j].setWeight(weight);
                }
            }
            if (j-1 > -1) {
                int weight = playGround[i][j-1].weight;
                if(weight != Integer.MAX_VALUE ) {
                    weight+=1;
                    playGround[i][j-1].setWeight(weight);
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j-1].weight;
                    if(weight != Integer.MAX_VALUE ) {
                        weight+=1;
                        playGround[i+1][j-1].setWeight(weight);
                    }
                }
            }
        } else { //neparni

            if ( j+1 < jMax ){
                int weight = playGround[i][j+1].weight;
                if(weight != Integer.MAX_VALUE ) {
                    weight+=1;
                    playGround[i][j+1].setWeight(weight);
                }
                if ( i-1 > -1 ){
                    weight = playGround[i-1][j+1].weight;
                    if(weight != Integer.MAX_VALUE ) {
                        weight+=1;
                        playGround[i-1][j+1].setWeight(weight);
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i + 1][j].weight;
                if (weight != Integer.MAX_VALUE) {
                    weight += 1;
                    playGround[i + 1][j].setWeight(weight);
                }
            }
            if ( i-1 > -1 ) {
                int weight = playGround[i-1][j].weight;
                if(weight != Integer.MAX_VALUE ) {
                    weight+=1;
                    playGround[i-1][j].setWeight(weight);
                }
            }
            if ( j-1 > -1 ){
                int weight = playGround[i][j-1].weight;
                if(weight != Integer.MAX_VALUE ) {
                    weight+=1;
                    playGround[i][j-1].setWeight(weight);
                }
                if ( i-1 > -1 ){
                    weight = playGround[i-1][j-1].weight;
                    if(weight != Integer.MAX_VALUE ) {
                        weight+=1;
                        playGround[i-1][j-1].setWeight(weight);
                    }
                }
            }
        }
    }

    private void checkTower (int i,int j){}

    private  Field getMinFieldFromRow(Field[][] playGround, int row){
        Field minField = playGround[row][0];
        for (int j = 1; j < playGround[row].length; j++ ) {
            if (playGround[row][j].getWeight()<minField.getWeight()) {
                minField =playGround[row][j];
            }
        }
        return minField;
    }
    private  Field getMinFieldFromColon(Field[][] playGround, int colon){
        Field minField = playGround[0][colon];
        for (int i = 1; i < playGround.length; i++ ) {
            if (playGround[i][colon].getWeight()<minField.getWeight()) {
                minField =playGround[i][colon];
            }
        }
        return minField;
    }

    public void setTowerAndRecalculateWeight2(Field[][] playGround, int iTower,int jTower) {
        long startTime = System.nanoTime();
        Log.w("matrica2 start  :",   " start  time: "+(startTime));
        playGround[iTower][jTower].setWeight(Integer.MAX_VALUE);


        int row = playGround.length;
        int colon = playGround[0].length;

        //reset all cost
        for (int i = 0; i < row;i++){
            for(int j=0; j<colon;j++){
                if (playGround[i][j].getWeight() != Integer.MAX_VALUE) {
                    playGround[i][j].setWeight(0);
                }
            }
        }
        //recalculate
        Field temp = playGround[endPointI-1][endPointJ-1];
        temp.setWeight(1);
        for (int j = colon - 1 ;  j > -1; j-- ) {
            for (int i = row - 1; i > -1; i--) { //from down to top
                Field field = playGround[i][j];
                if ( j == endPointJ-1 && i == endPointI-1 ) {

                } else {
                    if (field.getWeight() != Integer.MAX_VALUE) {
                        Field minElem = getMinWeightOfNeighbors2(playGround, i, j);
                        int minWeight = -1;
                        if (minElem != null) {
                            minWeight = minElem.getWeight();
                        }
                        if(playGround[i][j].getWeight() == 0){
                            playGround[i][j].setWeight(minWeight + 1);
                        } else {
                            if ((minWeight + 1) != 0 && (minWeight + 1) < playGround[i][j].getWeight()) {

                                playGround[i][j].setWeight(minWeight + 1);
                            }
                        }
                        //check if in this colon is no  zero element
                        //if it is true there is no need for top down search
                    }
                }
            }
            for (int i = 0; i < row; i++) { //from top to down
                Field field = playGround[i][j];
                if (field.getWeight() != Integer.MAX_VALUE) {
                    Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                    int minWeight = -1;
                    if (minElem != null){
                        minWeight = minElem.getWeight();
                    }
                    if(playGround[i][j].getWeight() == 0){
                        playGround[i][j].setWeight(minWeight + 1);
                    } else {
                        if ((minWeight + 1) != 0 && (minWeight + 1) < playGround[i][j].getWeight()) {

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }

                }

            }
        }
        String str = toString(playGround);
        Log.w("matrica2 first for \n",str);
        for (int i = row - 1; i > -1; i--) {
            for (int j = 0; j < colon; j++) {  //        ^ -------------->> in this direction
                if (playGround[i][j].getWeight() != Integer.MAX_VALUE) {
                    Field minElem = getMinWeightOfNeighbors2(playGround, i, j);
                    int minWeight = -1;
                    if (minElem != null) {
                        minWeight = minElem.getWeight();
                    }
                    if (playGround[i][j].getWeight() == 0) {
                        playGround[i][j].setWeight(minWeight + 1);
                    } else {
                        if ((minWeight + 1) != 0 && (minWeight + 1) < playGround[i][j].getWeight()) {

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                }
            }
            for (int j = colon - 1; j > -1; j--) {  //         <<-------------- in this direction
                if(playGround[i][j].getWeight() != Integer.MAX_VALUE){
                    Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                    int minWeight = -1;
                    if (minElem != null){
                        minWeight = minElem.getWeight();
                    }
                    if(playGround[i][j].getWeight() == 0){
                        playGround[i][j].setWeight(minWeight + 1);
                    } else {
                        if ((minWeight + 1) != 0 && (minWeight + 1) < playGround[i][j].getWeight()) {

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                }
            }

        }
        for (int i = 0; i < row; i++){
            for (int j = 0; j < colon; j++) {  //        ^ -------------->> in this direction
                if (playGround[i][j].getWeight() != Integer.MAX_VALUE) {
                    Field minElem = getMinWeightOfNeighbors2(playGround, i, j);
                    int minWeight = -1;
                    if (minElem != null) {
                        minWeight = minElem.getWeight();
                    }
                    if (playGround[i][j].getWeight() == 0) {
                        playGround[i][j].setWeight(minWeight + 1);
                    } else {
                        if ((minWeight + 1) != 0 && (minWeight + 1) < playGround[i][j].getWeight()) {

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                }
            }
            for (int j = colon - 1; j > -1; j--) {  //         <<-------------- in this direction
                if(playGround[i][j].getWeight() != Integer.MAX_VALUE){
                    Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                    int minWeight = -1;
                    if (minElem != null){
                        minWeight = minElem.getWeight();
                    }
                    if(playGround[i][j].getWeight() == 0){
                        playGround[i][j].setWeight(minWeight + 1);
                    } else {
                        if ((minWeight + 1) != 0 && (minWeight + 1) < playGround[i][j].getWeight()) {

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                }
            }
        }

        String str1 = toString(playGround);
        Log.w("matrica2 second for \n",str1);
        Log.w("matrica2 second for :",   " end time: "+(System.nanoTime()-startTime));

    }
    public void setTowerAndRecalculateWeight(Field[][] playGround, int iTower,int jTower) {
        playGround[iTower][jTower].setWeight(Integer.MAX_VALUE);
       // increaseNeighborsWeight(playGround,iTower,jTower);
        //check is in this colon more then one free fields
        int cntTower =0;
        for(int i=0;i<playGround.length;i++){
            if(playGround[i][jTower].getWeight() == Integer.MAX_VALUE){
                cntTower++;
            }
        }
        int iMax = playGround.length;
        int jMAx = playGround[0].length;

        if (cntTower== playGround.length-1){
            //okreni racunanje

            for (int j = jTower - 1;j>-1;j--) {
                for (int i = 0;i < iMax;i++){
                    if (playGround[i][j].getWeight() != Integer.MAX_VALUE) {
                        playGround[i][j].setWeight(0);
                    }
                }
            }
            //find mini in row where did tower set
            Field minField = getMinFieldFromColon(playGround,jTower);

            if( minField.getWeight() == Integer.MAX_VALUE){
                Log.e("matrica2","Ceio jedan red je popunjen kulicama !");
                return;
            }

            //set path costs in row above tower start form min element
            int colon = jTower -1;
            if( colon > -1) {
                if (playGround[minField.getRow()][colon].getWeight() != Integer.MAX_VALUE) {
                    playGround[minField.getRow()][colon].setWeight(getMinWeightOfNeighbors2(playGround, minField.getRow(),colon).getWeight()+1);
                }
                for (int i = minField.getRow()-1; i> -1; i--) {  //         <<-------------- in this direction
                    if(playGround[i][colon].getWeight() != Integer.MAX_VALUE){
                        //                    int minWeight = getMinWeightOfNeighbors2(playGround,row,j).getWeight();
                        Field minElem = getMinWeightOfNeighbors2(playGround,i,colon);
                        int minWeight = -1;
                        if (minElem != null){
                            minWeight = minElem.getWeight();
                        }
                        playGround[i][colon].setWeight(minWeight + 1);
                    }
                }
                for (int i = minField.getRow()+1; i <iMax; i++) {  //         -------------->> in this direction
                    if(playGround[i][colon].getWeight() != Integer.MAX_VALUE){
                        //                    int minWeight = getMinWeightOfNeighbors2(playGround,row,j).getWeight();
                        Field minElem = getMinWeightOfNeighbors2(playGround,i,colon);
                        int minWeight = -1;
                        if (minElem != null){
                            minWeight = minElem.getWeight();
                        }
                        playGround[i][colon].setWeight(minWeight + 1);
                    }
                }

                for (int j = colon - 1; j > -1; j--){
                    for (int i = 0; i < iMax; i++) {  //         -------------->> in this direction
                        if(playGround[i][j].getWeight() != Integer.MAX_VALUE){
                            Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                            int minWeight = -1;
                            if (minElem != null){
                                minWeight = minElem.getWeight();
                            }

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                    for (int i = iMax - 1; i> -1; i--) {  //         <<-------------- in this direction
                        if(playGround[i][j].getWeight() != Integer.MAX_VALUE){
                            Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                            int minWeight = -1;
                            if (minElem != null){
                                minWeight = minElem.getWeight();
                            }
                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                }

            }
            String str = toString(playGround);
            Log.w("matrica2 then \n",str);

        }
        else {
            // all cost above tower are reset to 0;
            for(int i = iTower - 1;i>-1;i--){
                for(int j=0;j<jMAx;j++){
                    if(playGround[i][j].getWeight()!=Integer.MAX_VALUE){
                        playGround[i][j].setWeight(0);
                    }
                }
            }
            //find mini in row where did tower set
            Field minField = getMinFieldFromRow(playGround,iTower);

            if( minField.getWeight() == Integer.MAX_VALUE){
                Log.e("matrica2","Ceio jedan red je popunjen kulicama !");
                return;
            }

            //set path costs in row above tower start form min element
            int row = iTower -1;
            if( row > -1) {
                if (playGround[row][minField.getColon()].getWeight() != Integer.MAX_VALUE) {
                    playGround[iTower - 1][minField.getColon()].setWeight(getMinWeightOfNeighbors2(playGround, iTower - 1, minField.getColon()).getWeight()+1);
                }
                for (int j = minField.getColon()-1; j > -1; j--) {  //         <<-------------- in this direction
                    if(playGround[row][j].getWeight() != Integer.MAX_VALUE){
                        //                    int minWeight = getMinWeightOfNeighbors2(playGround,row,j).getWeight();
                        Field minElem = getMinWeightOfNeighbors2(playGround,row,j);
                        int minWeight = -1;
                        if (minElem != null){
                            minWeight = minElem.getWeight();
                        }
                        playGround[row][j].setWeight(minWeight + 1);
                    }
                }
                for (int j = minField.getColon()+1; j <jMAx; j++) {  //         -------------->> in this direction
                    if(playGround[row][j].getWeight() != Integer.MAX_VALUE){
                        //                    int minWeight = getMinWeightOfNeighbors2(playGround,row,j).getWeight();
                        Field minElem = getMinWeightOfNeighbors2(playGround,row,j);
                        int minWeight = -1;
                        if (minElem != null){
                            minWeight = minElem.getWeight();
                        }
                        playGround[row][j].setWeight(minWeight + 1);
                    }
                }

                for (int i = row - 1; i > -1; i--){
                    for (int j = 0; j < jMAx; j++) {  //         -------------->> in this direction
                        if(playGround[i][j].getWeight() != Integer.MAX_VALUE){
                            Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                            int minWeight = -1;
                            if (minElem != null){
                                minWeight = minElem.getWeight();
                            }

                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                    for (int j = jMAx - 1; j > -1; j--) {  //         <<-------------- in this direction
                        if(playGround[i][j].getWeight() != Integer.MAX_VALUE){
                            Field minElem = getMinWeightOfNeighbors2(playGround,i,j);
                            int minWeight = -1;
                            if (minElem != null){
                                minWeight = minElem.getWeight();
                            }
                            playGround[i][j].setWeight(minWeight + 1);
                        }
                    }
                }

            }

            String str = toString(playGround);
            Log.w("matrica2 else \n",str);
        }








//        for (int j = jMAx-1; j > -1; j-- ) {
//            for(int i = iMax-1; i > -1; i--) {
//                Field field = playGround[i][j];
//                if ( field.getWeight() != 1 && field.getWeight() != Integer.MAX_VALUE){
////todo check if field is null?????
//                    int minWeight = getMinWeightOfNeighbors2(playGround,i,j).getWeight();
//
//                    field.setWeight(minWeight + 1);
//
//                }
//            }
//
//        }

    }

    public String findPathToEnd(Field[][] playGround, int iEnd, int jEnd){
        String str= "";
        List<Field> path = new ArrayList<>();
        int i=0;
        int j=0;
        Field previous = playGround[0][0];
        while ( i != iEnd - 1 || j != jEnd - 1 ) {

            Field next = getMinWeightOfNeighbors2(playGround,i,j);
            if(next == null ){
                return "Nema puta do izlaza !";
            }
           // Field nextOfNext = getMinWeightOfNeighbors2(playGround,next.getRow(),next.getColon());
//            if(previous.equals(nextOfNext)){
//                break;
//            } else {
//                previous = next;
//            }
            if (next.getWeight() >= previous.getWeight() ){
                Field nextOfNext = getMinWeightOfNeighbors2(playGround,next.getRow(),next.getColon());
                if(previous.equals(nextOfNext)){
                    break;
                }
            }
            if (next==null) {
//                str= "Nema puta do izlaza !";
                break;
            }
            previous = next;
            path.add(next);
            i = next.getRow();
            j = next.getColon();
        }
        if(path.get(path.size()-1).row == iEnd-1 && path.get(path.size()-1).colon == jEnd-1){
            for(int k =0;k<path.size();k++){
                str+="\t("+path.get(k).getRow()+","+path.get(k).getColon()+")" ;
            }
        }else {
            str= "Nema puta do izlaza !";
        }

        return  str;
    }
}
