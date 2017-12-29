package com.daemonize.daemondevapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.BouncingImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.FullColliderImageMover;
import com.daemonize.daemondevapp.imagemovers.GravityImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.borders.Border;
import com.daemonize.daemondevapp.imagemovers.borders.MapBorder;
import com.daemonize.daemondevapp.imagemovers.borders.OuterRectangleBorder;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.exceptions.DaemonException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private enum Mode {
        GRAVITY,
        CHASE,
        COLLIDE
    }

    private Mode mode = Mode.GRAVITY;

    private List<Bitmap> sprite;
    private List<Bitmap> spriteMain;

    private List<ImageView> views;
    private List<ImageMoverDaemon> starMovers;

    private ImageMoverDaemon mainMover;
    private ImageView mainView;

    private int borderX;
    private int borderY;

    private boolean paused = false;


    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            mainMover.setVelocity(0);
            mainMover.setLastCoordinates(
                    e.getX(),
                    e.getY(),
                    new ImageMoveClosure(MainActivity.this, mainView)
            );
        }
    });

    private interface EventHandler {
        void handleEvent(MotionEvent event);
    }

    private EventHandler onTouch = new EventHandler() {
        @Override
        public void handleEvent(MotionEvent event) {
            switch (mode) {
                case GRAVITY:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        for(ImageMoverDaemon starMover : starMovers) {
                            starMover.setTouchDirection(event.getX(), event.getY());
                        }
                    }
                    break;
                case CHASE:
                case COLLIDE:
                    if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_DOWN) {
                        mainMover.setTouchDirection(event.getX(), event.getY());
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onTouch.handleEvent(event);
        return true;
    }

    private class ImageMoveClosure extends Closure<ImageMover.PositionedBitmap> {

        private WeakReference<ImageView> view;

        public ImageMoveClosure(Activity activity, ImageView view) {
            super(activity);
            this.view = new WeakReference<>(view);
        }

        @Override
        public void doTheGuiStuff() {
            try {
                view.get().setX(getResult().positionX);
                view.get().setY(getResult().positionY);
                if (getResult().image != null)
                    view.get().setImageBitmap(getResult().image);
            } catch (DaemonException e) {
                e.printStackTrace();
            }
        }
    }

    private void initViews(List<ImageView> views) {

        views.clear();

        views.add((ImageView) findViewById(R.id.imageView));
        views.add((ImageView) findViewById(R.id.imageView2));
        views.add((ImageView) findViewById(R.id.imageView3));
        views.add((ImageView) findViewById(R.id.imageView4));
        views.add((ImageView) findViewById(R.id.imageView5));

        views.add((ImageView) findViewById(R.id.imageView6));
        views.add((ImageView) findViewById(R.id.imageView7));
        views.add((ImageView) findViewById(R.id.imageView8));
        views.add((ImageView) findViewById(R.id.imageView9));
        views.add((ImageView) findViewById(R.id.imageView10));

        views.add((ImageView) findViewById(R.id.imageView11));
        views.add((ImageView) findViewById(R.id.imageView12));
        views.add((ImageView) findViewById(R.id.imageView13));
        views.add((ImageView) findViewById(R.id.imageView14));
        views.add((ImageView) findViewById(R.id.imageView15));

//        views.add((ImageView) findViewById(R.id.imageView16));
//        views.add((ImageView) findViewById(R.id.imageView17));
//        views.add((ImageView) findViewById(R.id.imageView18));
//        views.add((ImageView) findViewById(R.id.imageView19));
//        views.add((ImageView) findViewById(R.id.imageView20));
//
//        views.add((ImageView) findViewById(R.id.imageView21));
//        views.add((ImageView) findViewById(R.id.imageView22));
//        views.add((ImageView) findViewById(R.id.imageView23));
//        views.add((ImageView) findViewById(R.id.imageView24));
//        views.add((ImageView) findViewById(R.id.imageView25));
//
//        views.add((ImageView) findViewById(R.id.imageView26));
//        views.add((ImageView) findViewById(R.id.imageView27));
//        views.add((ImageView) findViewById(R.id.imageView28));
//        views.add((ImageView) findViewById(R.id.imageView29));
//        views.add((ImageView) findViewById(R.id.imageView30));
//
//        views.add((ImageView) findViewById(R.id.imageView31));
//        views.add((ImageView) findViewById(R.id.imageView32));
//        views.add((ImageView) findViewById(R.id.imageView33));
//        views.add((ImageView) findViewById(R.id.imageView34));
//        views.add((ImageView) findViewById(R.id.imageView35));
//
//        views.add((ImageView) findViewById(R.id.imageView36));
//        views.add((ImageView) findViewById(R.id.imageView37));
//        views.add((ImageView) findViewById(R.id.imageView38));
//        views.add((ImageView) findViewById(R.id.imageView39));
//        views.add((ImageView) findViewById(R.id.imageView40));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        borderX = getResources().getDisplayMetrics().widthPixels - 100;
        borderY = getResources().getDisplayMetrics().heightPixels - 200;

        final Border mapBorder = new MapBorder(0, borderX, 0, borderY);
        final Border centerBorderSquare = new OuterRectangleBorder(
                borderX/2 - 20,
                borderX/2 + 20,
                borderY/2 - 20,
                borderY/2 + 20
        );


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(!paused) {
                        for(ImageMoverDaemon starMover : starMovers) {
                            starMover.pause();
                        }
                        paused = true;
                    } else {
                        for(ImageMoverDaemon starMover : starMovers) {
                            starMover.resume();
                        }
                        paused = false;
                    }
                }

        });

        FloatingActionButton fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mode) {
                    case GRAVITY: {

                        mainMover.stop();
                        mainMover = null;
                        for(ImageMoverDaemon imageMoverDaemon : starMovers) {
                            imageMoverDaemon.stop();
                        }

                        starMovers.clear();
                        starMovers = new ArrayList<>(40);
                        views = new ArrayList<>(40);
                        initViews(views);

                        int i = 5;
                        for (ImageView v : views) {
                            ImageMoverDaemon starMover = new ImageMoverDaemon(
                                    new ImageTranslationMover(
                                            sprite,
                                            i / 14,
                                            Pair.create((float) borderX/2, (float) borderY/2)
                                    )
                            ).setBorders(borderX, borderY);
                            //.addBorders(mapBorder).addBorders(centerBorderSquare);//
                            starMover.setSideQuest(starMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, v)));
                            starMover.start();
                            starMovers.add(starMover);
                            i += 5;
                        }

                        mainMover = new ImageMoverDaemon(new MainImageTranslationMover(
                                spriteMain,
                                40f,
                                Pair.create(borderX / 2f, borderY / 2f),
                                starMovers,
                                MainImageTranslationMover.Mode.CHASE)
                        ).setBorders(borderX, borderY);
                                //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
                        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
                        mainMover.start();

                        mode = Mode.CHASE;
                        Toast.makeText(MainActivity.this, "MODE: CHASE", Toast.LENGTH_LONG).show();
                    }
                        break;

                    case CHASE: {

                        mainMover.stop();
                        mainMover = null;
                        for(ImageMoverDaemon imageMoverDaemon : starMovers) {
                            imageMoverDaemon.stop();
                        }

                        starMovers.clear();
                        starMovers = new ArrayList<>(40);
                        views = new ArrayList<>(40);
                        initViews(views);

                        int i = 5;

                        for(ImageView vv : views) {
                            ImageMoverDaemon starMover = new ImageMoverDaemon(
                                        new FullColliderImageMover(
                                                sprite,
                                                i / 20,
                                                Pair.create((float) borderX % i, (float) borderY % i),
                                                MainImageTranslationMover.Mode.COLLIDE
                                        )
                            ).setBorders(borderX, borderY);
                            //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
                            starMover.setSideQuest(starMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, vv)));
                            starMover.start();
                            starMovers.add(starMover);
                            i += 5;
                        }

                        for (ImageMoverDaemon subscriber : starMovers) {
                            for (ImageMoverDaemon observer : starMovers) {
                                if(!subscriber.getPrototype().equals(observer.getPrototype())) {
                                    subscriber.setObserver(observer);
                                }
                            }
                        }

                        mainMover = new ImageMoverDaemon(new MainImageTranslationMover(
                                spriteMain,
                                50f,
                                Pair.create(borderX/2f, borderY/2f),
                                starMovers,
                                MainImageTranslationMover.Mode.COLLIDE)
                        ).setBorders(borderX, borderY);
                        //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
                        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
                        mainMover.start();

                        mode = Mode.COLLIDE;
                        Toast.makeText(MainActivity.this, "MODE: COLLIDE", Toast.LENGTH_LONG).show();
                    }
                        break;

                    case COLLIDE:{

                        mainMover.stop();
                        mainMover = null;
                        for(ImageMoverDaemon imageMoverDaemon : starMovers) {
                            imageMoverDaemon.stop();
                        }

                        //fullStarMovers.clear();
                        starMovers.clear();
                        starMovers = new ArrayList<>(40);
                        views = new ArrayList<>(40);

                        initViews(views);

                        int i = 5;
                        for(ImageView vieww : views) {
                            ImageMoverDaemon starMover = new ImageMoverDaemon(
                                    new GravityImageMover(
                                            sprite,
                                            /*i/5*/30,
                                            Pair.create((float)borderX % i, (float) borderY % i)
                                    )
                            ).setBorders(borderX, borderY);
                            //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
                            starMover.setSideQuest(starMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, vieww)));
                            starMover.start();
                            starMovers.add(starMover);
                            i += 5;
                        }

                        mainMover = new ImageMoverDaemon(new MainImageTranslationMover(
                                spriteMain,
                                50f,
                                Pair.create(borderX/2f, borderY/2f),
                                starMovers,
                                MainImageTranslationMover.Mode.NONE)
                        ).setBorders(borderX, borderY);
                        //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
                        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
                        mainMover.start();
                    }
                        mode = Mode.GRAVITY;
                        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();
                        break;

                }

            }

        });

        mode = Mode.GRAVITY;
        mainView = findViewById(R.id.imageViewMain);
        views = new ArrayList<>(40);
        starMovers = new ArrayList<>(40);
        initViews(views);

        try {
            sprite = new ArrayList<>();
            sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar.png")), 100, 100, false));
            sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar90.png")), 100, 100, false));
            sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar180.png")), 100, 100, false));
            sprite.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar270.png")), 100, 100, false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            spriteMain = new ArrayList<>();
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), 100, 100, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), 100, 100, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), 100, 100, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), 100, 100, false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 5;
        for(ImageView view : views) {
            ImageMoverDaemon starMover = new ImageMoverDaemon(
                    new GravityImageMover(
                            sprite,
                            /*i/5*/30,
                            Pair.create((float)borderX % i, (float) borderY % i)
                    )
            ).setBorders(borderX, borderY);
            //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
            starMover.setSideQuest(starMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, view)));
            starMover.start();
            starMovers.add(starMover);
            i += 5;
        }

        mainMover = new ImageMoverDaemon(new MainImageTranslationMover(
                spriteMain,
                50f,
                Pair.create(borderX/2f, borderY/2f),
                starMovers,
                MainImageTranslationMover.Mode.NONE)
        ).setBorders(borderX, borderY);
        //.addBorders(mapBorder).addBorders(centerBorderSquare);//.setBorders(borderX, borderY);
        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
        mainMover.start();

        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainMover.stop();
        for(ImageMoverDaemon mover : starMovers) {
            mover.stop();
        }
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
