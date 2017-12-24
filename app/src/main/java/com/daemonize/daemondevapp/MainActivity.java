package com.daemonize.daemondevapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.BouncingImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.GravityImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
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

    List<ImageView> views;
    List<ImageMoverDaemon> starMovers;

    private TextView wasted;

    ImageMoverDaemon mainMover;
    ImageView mainView;

    private int borderX;
    private int borderY;

    private boolean paused = false;

    private MotionEvent event;

    private Runnable onTouch = new Runnable() {
        @Override
        public void run() {
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
                    mainMover.setTouchDirection(event.getX(), event.getY());
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.event = event;
        onTouch.run();
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

        views.add((ImageView) findViewById(R.id.imageView16));
        views.add((ImageView) findViewById(R.id.imageView17));
        views.add((ImageView) findViewById(R.id.imageView18));
        views.add((ImageView) findViewById(R.id.imageView19));
        views.add((ImageView) findViewById(R.id.imageView20));

        views.add((ImageView) findViewById(R.id.imageView21));
        views.add((ImageView) findViewById(R.id.imageView22));
        views.add((ImageView) findViewById(R.id.imageView23));
        views.add((ImageView) findViewById(R.id.imageView24));
        views.add((ImageView) findViewById(R.id.imageView25));

        views.add((ImageView) findViewById(R.id.imageView26));
        views.add((ImageView) findViewById(R.id.imageView27));
        views.add((ImageView) findViewById(R.id.imageView28));
        views.add((ImageView) findViewById(R.id.imageView29));
        views.add((ImageView) findViewById(R.id.imageView30));

        views.add((ImageView) findViewById(R.id.imageView31));
        views.add((ImageView) findViewById(R.id.imageView32));
        views.add((ImageView) findViewById(R.id.imageView33));
        views.add((ImageView) findViewById(R.id.imageView34));
        views.add((ImageView) findViewById(R.id.imageView35));

        views.add((ImageView) findViewById(R.id.imageView36));
        views.add((ImageView) findViewById(R.id.imageView37));
        views.add((ImageView) findViewById(R.id.imageView38));
        views.add((ImageView) findViewById(R.id.imageView39));
        views.add((ImageView) findViewById(R.id.imageView40));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        borderX = getResources().getDisplayMetrics().widthPixels - 100;
        borderY = getResources().getDisplayMetrics().heightPixels - 200;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mode) {
                    case GRAVITY: {
                        for(ImageMoverDaemon imageMoverDaemon : starMovers) {
                            imageMoverDaemon.stop();
                        }
                        mainMover.stop();
                        starMovers.clear();
                        initViews(views);

                        int i = 5;
                        for (ImageView v : views) {
                            ImageMoverDaemon starMover = new ImageMoverDaemon(new ImageTranslationMover(sprite, i / 15, Pair.create((float) borderX/2, (float) borderY/2)));
                            starMover.setBorders(borderX, borderY);
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
                        );
                        mainMover.setBorders(borderX, borderY);
                        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
                        mainMover.start();

                        mode = Mode.CHASE;
                        Toast.makeText(MainActivity.this, "MODE: CHASE", Toast.LENGTH_LONG).show();
                    }
                        break;

                    case CHASE: {
                        for(ImageMoverDaemon imageMoverDaemon : starMovers) {
                            imageMoverDaemon.stop();
                        }
                        mainMover.stop();
                        starMovers.clear();
                        initViews(views);
                        int i = 5;
                        for(ImageView vie : views) {
                            ImageMoverDaemon starMover = new ImageMoverDaemon(new BouncingImageTranslationMover(sprite, i/20, Pair.create((float)borderX % i, (float) borderY % i)));
                            starMover.setBorders(borderX, borderY);
                            starMover.setSideQuest(starMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, vie)));
                            starMover.start();
                            starMovers.add(starMover);
                            i += 5;
                        }

                        mainMover = new ImageMoverDaemon(new MainImageTranslationMover(
                                spriteMain,
                                50f,
                                Pair.create(borderX/2f, borderY/2f),
                                starMovers,
                                MainImageTranslationMover.Mode.COLLIDE)
                        );
                        mainMover.setBorders(borderX, borderY);
                        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
                        mainMover.start();

                        mode = Mode.COLLIDE;
                        Toast.makeText(MainActivity.this, "MODE: COLLIDE", Toast.LENGTH_LONG).show();
                    }
                        break;

                    case COLLIDE:{
                        for(ImageMoverDaemon imageMoverDaemon : starMovers) {
                            imageMoverDaemon.stop();
                        }
                        starMovers.clear();
                        mainMover.stop();
                        initViews(views);

                        int i = 5;
                        for(ImageView vieww : views) {
                            ImageMoverDaemon starMover = new ImageMoverDaemon(new GravityImageMover(sprite, /*i/5*/30, Pair.create((float)borderX % i, (float) borderY % i)));
                            starMover.setBorders(borderX, borderY);
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
                        );
                        mainMover.setBorders(borderX, borderY);
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

        mainView = (ImageView) findViewById(R.id.imageViewMain);
        views = new ArrayList<>();
        starMovers = new ArrayList<>();

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
            ImageMoverDaemon starMover = new ImageMoverDaemon(new GravityImageMover(sprite, /*i/5*/30, Pair.create((float)borderX % i, (float) borderY % i)));
            starMover.setBorders(borderX, borderY);
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
        );
        mainMover.setBorders(borderX, borderY);
        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(new ImageMoveClosure(MainActivity.this, mainView)));
        mainMover.start();

        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainMover.stop();
        for(ImageMoverDaemon mover : starMovers) {
            mover.stop();
        }
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
