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

    private ImageView background;
    private Bitmap backgroundImg;

    private AndroidImageView[][] fieldViews;
    private ArrayList<ImageView> bulletsViews;

    public Grid grid;

    private DummyDaemon dummyDaemon;

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
            ImageMover.PositionedBitmap returnVal = ret.get();
            view.setX(returnVal.positionX);
            view.setY(returnVal.positionY);
            view.setImageBitmap(returnVal.image);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.w("onCreate","Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        borderX = getResources().getDisplayMetrics().widthPixels ;
        borderY = getResources().getDisplayMetrics().heightPixels ;

        layout = findViewById(R.id.cl);

        background = findViewById(R.id.img_large);

        //mainView = findViewById(R.id.imageViewMain);
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


            List<Bitmap> towerSprite = new ArrayList<>(36);
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower0.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower10.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower20.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower30.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower40.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower50.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower60.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower70.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower80.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower90.png")), 80, 80, false));

            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower100.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower110.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower120.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower130.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower140.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower150.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower160.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower170.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower180.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower190.png")), 80, 80, false));

            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower200.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower210.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower220.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower230.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower240.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower250.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower260.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower270.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower280.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower290.png")), 80, 80, false));

            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower300.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower310.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower320.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower330.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower340.png")), 80, 80, false));
            towerSprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower350.png")), 80, 80, false));

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
                    .setTowerSprite(towerSprite)
                    .setBorders(borderX, borderY);

        } catch (IOException ex) {
            Log.e(DaemonUtils.tag(), "Could not init game!", ex);
        }


//        ExampleDaemon exampleDaemon = new ExampleDaemon(
//                new AndroidLooperConsumer(),
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
