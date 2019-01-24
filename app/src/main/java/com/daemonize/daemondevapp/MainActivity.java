package com.daemonize.daemondevapp;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;


import com.daemonize.game.Game;
import com.daemonize.game.images.Image;
import com.daemonize.game.images.imageloader.ImageLoader;
import com.daemonize.daemonengine.utils.DaemonUtils;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;
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

        layout = findViewById(R.id.cl);

        //renderer init
        AndroidSurfaceViewRenderer renderer = new AndroidSurfaceViewRenderer(this);
        //renderer.setWindowSize(borderX, borderY);
        setContentView(renderer);

        ImageLoader imageLoader = new AndroidImageLoader(this);

        int rows = 6;
        int columns = 9;

        game = new Game(renderer, imageLoader, borderX, borderY, rows, columns,50,50);

        if(!game.isRunning())
            game.run();

        Log.e(DaemonUtils.tag(), "ACTIVITY ON CREATE----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(DaemonUtils.tag(), "ACTIVITY ON START----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
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
        Log.w(DaemonUtils.tag(), "ACTIVITY ON RESUME----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
        if (game.isRunning() && game.isPaused())
            game.cont();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(DaemonUtils.tag(), "ACTIVITY ON ON PAUSE----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
        if (game.isRunning() && !game.isPaused())
            game.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(DaemonUtils.tag(), "ACTIVITY ON STOP----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(DaemonUtils.tag(), "ACTIVITY ON DESTROY----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
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
