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
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
import com.daemonize.daemondevapp.images.AndroidBitmapImage;
import com.daemonize.daemondevapp.images.Image;
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

    private ImageView background;
    private Bitmap backgroundImg;

    private AndroidImageView[][] fieldViews;

    private int borderX;
    private int borderY;

    private long wastedCounter;
    private String wastedCntText = "EXPLOSIONS COUNTER: ";
    private TextView wastedCntView;

    public TextView hpView;
    private String hpText = "HP: ";


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.w("onCreate","Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        borderX = getResources().getDisplayMetrics().widthPixels ;
        borderY = getResources().getDisplayMetrics().heightPixels ;

        layout = findViewById(R.id.cl);

        background = findViewById(R.id.img_large);

        wastedCntView = findViewById(R.id.response);
        wastedCntView.setWidth(borderX / 3);
        wastedCntView.setHeight(borderY / 10);
        wastedCntView.setTextColor(WHITE);

        hpView = findViewById(R.id.hp);
        hpView.setWidth(borderX / 3);
        hpView.setHeight(borderY / 10);
        hpView.setTextColor(WHITE);

        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////


        int rows = 6;
        int columns = 11;

        int width = 160;
        int height = 160;

        //init view matrix
        fieldViews = new AndroidImageView[rows][columns];
        for(int j = 0; j < rows; ++j ) {
            for (int i = 0; i < columns; ++i) {
                fieldViews[j][i] = new AndroidImageView(createImageView(width, width)); //TODO unhardcode
            }
        }


        //init enemy view
        ImageView enemyView = createImageView(80,80);

        try {

            //init enemy sprite
            List<Image> sprite = new ArrayList<>();

            int i = 0;
            for (; i < 3; ++i) {
                sprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar.png")), width, height, false)));
            }

            for (; i < 6; ++i) {
                sprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar90.png")), width, height, false)));
            }

            for (; i < 9; ++i) {
                sprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar180.png")), width, height, false)));
            }

            for (; i < 12; ++i) {
                sprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar270.png")), width, height, false)));
            }

            //bullet sprite
            int bulletSize = 20;
            List<Image> bulletSprite = new ArrayList<>();
            bulletSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), bulletSize, bulletSize, false)));
            bulletSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), bulletSize, bulletSize, false)));
            bulletSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), bulletSize, bulletSize, false)));
            bulletSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), bulletSize, bulletSize, false)));


            //explosion sprite
            List<Image> explosionSprite = new ArrayList<>();

            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), width,height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")),width, height, false)));

            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")),width, height, false)));

            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")),width, height, false)));

            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")),width, height, false)));
            explosionSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")),width, height, false)));


            List<Image> towerSprite = new ArrayList<>(36);
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower0.png")), width, height,false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower10.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower20.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower30.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower40.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower50.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower60.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower70.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower80.png")), width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower90.png")), width, height, false)));

            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower100.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower110.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower120.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower130.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower140.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower150.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower160.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower170.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower180.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower190.png")),width, height, false)));

            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower200.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower210.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower220.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower230.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower240.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower250.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower260.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower270.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower280.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower290.png")),width, height, false)));

            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower300.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower310.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower320.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower330.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower340.png")),width, height, false)));
            towerSprite.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower350.png")),width, height, false)));

            //TODO unnecessary background
            backgroundImg = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("maphi.jpg")), borderX, borderY, false);
            background.setImageBitmap(backgroundImg);


            Queue<DaemonView> enemyQueue = new LinkedList<>();

            for (int cnt = 0; cnt < 100; ++cnt) {
                enemyQueue.add(new AndroidImageView(createImageView(width, height)));
            }

            int width_hp = 120;
            int height_hp = 30;
            Queue<DaemonView> enemyHpViewQueue = new LinkedList<>();

            for (int cnt = 0; cnt < 100; ++cnt) {
                enemyHpViewQueue.add(new AndroidImageView(createImageView(width_hp, height_hp)));
            }

            Queue<DaemonView> bulletQueue = new LinkedList<>();

            for (int cnt = 0; cnt < 200; ++cnt) {
                bulletQueue.add(new AndroidImageView(createImageView(bulletSize, bulletSize)));
            }


            List<Image> listHealthBarImg = new ArrayList<>();
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_0.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_10.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_20.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_30.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_40.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_50.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_60.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_70.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_80.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_90.png")), width_hp, height_hp, false)));
            listHealthBarImg.add(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_100.png")), width_hp, height_hp, false)));

            game = new Game(rows, columns, fieldViews, enemyQueue, enemyHpViewQueue, bulletQueue,50,50, width)
                    .setFieldImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), width, height, false)))
                    .setFieldImagePath(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), width, height, false)))
                    .setFieldImageTower(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), width, height, false)))
                    .setFieldImageTowerDen(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("red.png")), width, height, false)))
                    .setEnemySprite(sprite)
                    .setBulletSprite(bulletSprite)
                    .setExplodeSprite(explosionSprite)
                    .setTowerSprite(towerSprite)
                    .setHealthBarImage(listHealthBarImg)
                    .setBorders(borderX, borderY);

        } catch (IOException ex) {
            Log.e(DaemonUtils.tag(), "Could not init game!", ex);
        }

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
