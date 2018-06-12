package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.BouncingImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.daemonscroll.DaemonSpell;


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

            if (ret.get() == null)
                return;

            ImageMover.PositionedBitmap returnVal = ret.get();
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

            for (ImageMoverDaemon starMover : starMovers) {
                Pair<Float, Float> starMoverPos = starMover.getLastCoordinates();
                if(Math.abs(ret.get().positionX - starMoverPos.first) <= bulletSprite.get(0).getWidth()
                        && Math.abs(ret.get().positionY - starMoverPos.second) <= bulletSprite.get(0).getHeight()) {

                    ImageTranslationMover prototype = (ImageTranslationMover) starMover.getPrototype();

                    if (!prototype.isExploading()) {

                        bulletDaemon.stop();
                        layout.removeView(view);

                        wastedCntView.setText(wastedCntText + Long.toString(++wastedCounter));
                        starMover.explode(
                                explosionSprite,
                                binder.bindViewToClosure(prototype.getView()),
                                ret1 -> {
                                    prototype.getView().setImageBitmap(ret1.get().image);
                                    prototype.setLastCoordinates(
                                            getRandomInt(0, borderX),
                                            getRandomInt(0, borderY)
                                    );
                                }
                        );
                    }
                }
            }

            super.onReturn(ret);
        }
    }

    private static int getRandomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private ImageView createImageView(int width, int height) {
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

    private class MachineGunSpell implements DaemonSpell {

        private Pair<Float, Float> initBulletCoord;
        private ImageMover.Velocity velocity;
        private float offset = -3;

        public MachineGunSpell(float offset, Pair<Float, Float> initBulletCoord, ImageMover.Velocity velocity) {
            this.offset = offset;
            this.initBulletCoord = initBulletCoord;
            this.velocity = velocity;
        }

        @Override
        public void cast() {
            ImageView bulletView;
            ImageMoverDaemon bullet;

            for (int i = 0; i < 3; ++i) {

                bulletView = createImageView(40, 40);
                bullet = new ImageMoverDaemon(
                        new ImageTranslationMover(
                                bulletSprite,
                                50,
                                initBulletCoord
                        ).setBorders(borderX, borderY)
                ).setName("Bullet");

                bullet.setVelocity(50)
                      .setMoveSideQuest()
                      .setClosure(new BulletClosure(bulletView, bullet));
                velocity.direction.coeficientX += Math.pow(offset, i);
                velocity.direction.coeficientY -= Math.pow(offset, i);
                bullet.setVelocity(velocity);

            }
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

        wastedCntView = findViewById(R.id.response);
        wastedCntView.setWidth(borderX / 3);
        wastedCntView.setHeight(borderY / 10);
        wastedCntView.setTextColor(WHITE);

        hpView = findViewById(R.id.hp);
        hpView.setWidth(borderX / 3);
        hpView.setHeight(borderY / 10);
        hpView.setTextColor(WHITE);

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

        int i = 5;
        for(int j = 0; j < 40; ++j) {

            ImageView view = createImageView(80, 80);
            ImageMoverDaemon starMover = new ImageMoverDaemon(
                    new BouncingImageTranslationMover(
                            sprite,
                            8,
                            Pair.create(
                                    (float) borderX % i,
                                    (float) borderY % i
                            )
                    ).setBorders(borderX, borderY).setView(view)
            ).setName("Star " + Integer.toString(i));
            starMover.setMoveSideQuest().setClosure(binder.bindViewToClosure(view));
            starMover.start();

            starMovers.add(starMover);
            i += 5;

        }

        mainMover = new ImageMoverDaemon(
                new MainImageTranslationMover(
                        spriteMain,
                        10f,
                        Pair.create(borderX/2f, borderY/2f),
                        starMovers
                )
                .setBorders(borderX, borderY)
                .setHpClosure(hp -> {
                    if (hp.get() <= 0) {
                        for (ImageMoverDaemon star : starMovers) {
                            //star.stop();
                            BouncingImageTranslationMover prototype = ((BouncingImageTranslationMover) star.getPrototype());
                            star.explode(explosionSprite,
                                    binder.bindViewToClosure(prototype.getView()),
                                    ret1 -> {
                                        prototype.getView().setImageBitmap(ret1.get().image);
                                        prototype.setLastCoordinates(
                                                getRandomInt(0, borderX),
                                                getRandomInt(0, borderY)
                                        );
                                        star.stop();
                                    });
                        }
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

}
