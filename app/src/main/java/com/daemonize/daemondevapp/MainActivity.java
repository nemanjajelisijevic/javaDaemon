package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

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
import android.widget.TextView;
import android.widget.Toast;


import com.daemonize.daemondevapp.imagemovers.BouncingImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.GravityImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.MainImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.borders.Border;
import com.daemonize.daemondevapp.imagemovers.borders.MapBorder;
import com.daemonize.daemondevapp.imagemovers.borders.OuterRectangleBorder;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

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


    private ExampleDaemon exampleDaemon;
    private TextView textView;

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            mainMover.setVelocity(0);
            mainMover.setLastCoordinates(
                    e.getX(),
                    e.getY(),
                    new ImageMoveClosure(mainView)
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

    @FunctionalInterface
    private interface ViewBinder {
        ImageMoveClosure bindViewToClosure(ImageView view);
    }

    private class ImageMoveClosure implements Closure<ImageMover.PositionedBitmap> {

        private WeakReference<ImageView> view;

        public ImageMoveClosure(ImageView view) {
            this.view = new WeakReference<>(view);
        }

        @Override
        public void onReturn(Return<ImageMover.PositionedBitmap> ret) {
            ImageMover.PositionedBitmap returnVal = ret.get();
            view.get().setX(returnVal.positionX);
            view.get().setY(returnVal.positionY);
            if (returnVal.image != null)
                view.get().setImageBitmap(returnVal.image);
        }
    }

    private ViewBinder binder = ImageMoveClosure::new;

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

        textView = findViewById(R.id.response);
        textView.setTextColor(WHITE);

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

                        int i = 5;
                        for (ImageMoverDaemon starMover : starMovers) {
                            starMover.setPrototype(
                                    new ImageTranslationMover(
                                            sprite,
                                            i / 14,
                                            Pair.create((float) borderX/2, (float) borderY/2)
                                    ).setBorders(borderX, borderY)
                            ).setName("Star " + Integer.toString(i));
                            i+=5;
                        }

                        mainMover.setPrototype(
                                new MainImageTranslationMover(
                                        spriteMain,
                                        40f,
                                        Pair.create(borderX / 2f, borderY / 2f),
                                        starMovers,
                                        MainImageTranslationMover.Mode.CHASE
                                ).setBorders(borderX, borderY)
                        ).setName("Exceptione");

                        mode = Mode.CHASE;
                        Toast.makeText(MainActivity.this, "MODE: CHASE", Toast.LENGTH_LONG).show();
                    }
                        break;

                    case CHASE: {

                        int i = 5;
                        for (ImageMoverDaemon starMover : starMovers) {
                            starMover.setPrototype(
                                        new BouncingImageTranslationMover(
                                                sprite,
                                                i / 20,
                                                Pair.create(
                                                        (float) borderX % i,
                                                        (float) borderY % i
                                                )
                                        ).setBorders(borderX, borderY)
                            ).setName("Star " + Integer.toString(i));
                            i+=5;
                        }

                        mainMover.setPrototype(
                                new MainImageTranslationMover(
                                        spriteMain,
                                        50f,
                                        Pair.create(borderX/2f, borderY/2f),
                                        starMovers,
                                        MainImageTranslationMover.Mode.COLLIDE
                                ).setBorders(borderX, borderY)
                        ).setName("Exceptione");

                        mode = Mode.COLLIDE;
                        Toast.makeText(MainActivity.this, "MODE: COLLIDE", Toast.LENGTH_LONG).show();
                    }
                        break;

                    case COLLIDE:{

                        int i = 5;
                        for (ImageMoverDaemon starMover : starMovers) {
                            starMover.setPrototype(
                                    new GravityImageMover(
                                            sprite,
                                            /*i/5*/30,
                                            Pair.create((float)borderX % i, (float) borderY % i)
                                    ).setBorders(borderX, borderY)
                            ).setName("Star " + Integer.toString(i));
                            i+=5;
                        }

                        mainMover.setPrototype(
                                new MainImageTranslationMover(
                                        spriteMain,
                                        50f,
                                        Pair.create(borderX/2f, borderY/2f),
                                        starMovers,
                                        MainImageTranslationMover.Mode.NONE
                                ).setBorders(borderX, borderY)
                        ).setName("Exceptione");

                        mode = Mode.GRAVITY;
                        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();

                    }
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

            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
            spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), 150, 150, false));
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


            //spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), 100, 100, false));
            //spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), 100, 100, false));
            //spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), 100, 100, false));
            //spriteMain.add(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), 100, 100, false));

        } catch (IOException e) {
            e.printStackTrace();
        }

        int i = 5;
        for(ImageView view : views) {
            ImageMoverDaemon starMover = new ImageMoverDaemon(
                    new GravityImageMover(
                            sprite,
                            /*i/5*/20,
                            Pair.create((float)borderX % i, (float) borderY % i)
                    ).setBorders(borderX, borderY)
            );

            starMover.setSideQuest(starMover.moveSideQuest.setClosure(binder.bindViewToClosure(view)));
            starMover.start();
            starMovers.add(starMover);
            i += 5;
        }

        mainMover = new ImageMoverDaemon(
                new MainImageTranslationMover(
                        spriteMain,
                        30f,
                        Pair.create(borderX/2f, borderY/2f),
                        starMovers,
                        MainImageTranslationMover.Mode.NONE
                ).setBorders(borderX, borderY)
        ).setName("Exceptione");

        mainMover.setSideQuest(mainMover.moveSideQuest.setClosure(binder.bindViewToClosure(mainView)));
        mainMover.start();

        exampleDaemon = new ExampleDaemon(new Example())
                .setName("ExampleDaemon")
                .evenMoreComplicated(
                        "Constantly updated from another thread: ",
                        update -> textView.setText(update.get()),
                        ret -> {
                            textView.setText(ret.get());
                            exampleDaemon.evenMoreComplicated(
                                    "Here we go again: ",
                                    update -> textView.setText(update.get()),
                                    ret2 ->  textView.setText(ret2.get())
                                    );
                        }
                );

//        RestClientTestScript restClientTestScript = new RestClientTestScript(
//                textView,
//                new RestClientDaemon(new RestClient("https://reqres.in"))
//        );

//        restClientTestScript.run();
        Toast.makeText(MainActivity.this, "MODE: GRAVITY", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //exampleDaemon.stop();
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

    private static Bitmap drawTextOnBitmap(Bitmap bitmap, String text) {

        Bitmap ret = Bitmap.createBitmap(bitmap);
        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColor(BLACK);
        paint.setStrokeWidth(300);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(ret, 0, 0, paint);
        canvas.drawText(text, 60, 60, paint);

        return ret;
    }

}
