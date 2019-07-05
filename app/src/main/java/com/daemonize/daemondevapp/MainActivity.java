package com.daemonize.daemondevapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.WindowManager;


import com.daemonize.game.Game;
import com.daemonize.game.images.imageloader.ImageManager;
import com.daemonize.game.renderer.Renderer2D;

public class MainActivity extends AppCompatActivity {

    //private ConstraintLayout layout;
    private Game game;

    private int getPercentageValue(int percent, int hundredPercentValue) {
        return (hundredPercentValue * percent) / 100;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int borderX = getResources().getDisplayMetrics().widthPixels;
        int borderY = getResources().getDisplayMetrics().heightPixels;

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setZOrderOnTop(true);

        //renderer init
        Renderer2D renderer = new AndroidSurfaceViewRenderer(surfaceView);
        ImageManager imageManager = new AndroidImageManager(this);

        int rows = 6;
        int columns = 9;

        game = new Game(renderer, imageManager, new AndroidSoundManager(this, 16), borderX, borderY, rows, columns,50,50);

        if(!game.isRunning())
            game.run();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            game.onTouch(event.getX(), event.getY());
        return true;
        //return super.onTouchEvent(event);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (game.isRunning() && game.isPaused())
            game.cont();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (game.isRunning() && !game.isPaused())
            game.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.stop();
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
