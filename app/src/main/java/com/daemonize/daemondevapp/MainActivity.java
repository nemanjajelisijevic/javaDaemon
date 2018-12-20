package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;


import com.daemonize.daemondevapp.images.AndroidBitmapImage;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.renderer.AndroidSurfaceViewRenderer;
import com.daemonize.daemondevapp.renderer.Renderer2D;
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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int borderX = getResources().getDisplayMetrics().widthPixels;
        int borderY = getResources().getDisplayMetrics().heightPixels;

        layout = findViewById(R.id.cl);

        ///////////////////////////////////////////////////////////////////////////////////////////
        //                                GAME INITIALIZATION                                    //
        ///////////////////////////////////////////////////////////////////////////////////////////

        //renderer init
        AndroidSurfaceViewRenderer renderer = new AndroidSurfaceViewRenderer(this);
        //renderer.setWindowSize(borderX, borderY);
        setContentView(renderer);

        int rows = 6;
        int columns = 9;

        int width = 160;
        int height = 160;

        try {

            //init enemy sprite
            Image [] sprite = new Image[36];

            for(int i = 0; i < 36; i++) {
                sprite[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("plane" + Integer.toString(i) + "0.png")), width, height, false));
            }


//
//            int i = 0;
//            for (; i < 3; ++i)
//                sprite[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar.png")), width, height, false));
//
//            for (; i < 6; ++i)
//                sprite[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar90.png")), width, height, false));
//
//
//            for (; i < 9; ++i)
//                sprite[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar180.png")), width, height, false));
//
//            for (; i < 12; ++i)
//                sprite[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstar270.png")), width, height, false));

            //bullet sprite
            int bulletSize0 = 20;
            Image [] bulletSprite = new Image[4];
            bulletSprite[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed.png")), bulletSize0, bulletSize0, false));
            bulletSprite[1] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed90.png")), bulletSize0, bulletSize0, false));
            bulletSprite[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed180.png")), bulletSize0, bulletSize0, false));
            bulletSprite[3] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("thebarnstarRed270.png")), bulletSize0, bulletSize0, false));

            int bulletSize = 60;
            Image [] spriteRocket = new Image[36];

            for(int i = 0; i < 36; i++) {
                spriteRocket[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("rocket" + Integer.toString(i) + "0.png")), bulletSize, bulletSize, false));
            }

            //bullet sprite laser

            Image [] bulletSpriteLaser =new Image[36];
//            bulletSpriteLaser[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("blueBullet.png")),  bulletSize, bulletSize, false));
//            bulletSpriteLaser[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("redBullet.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning00_180.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[1] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning10_190.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning20_200.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[3] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning30_210.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[4] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning40_220.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[5] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning50_230.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[6] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning60_240.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[7] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning70_250.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[8] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning80_260.png")),  bulletSize, bulletSize, false));
            bulletSpriteLaser[9] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning90_270.png")),  bulletSize, bulletSize, false));

            bulletSpriteLaser[10] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning100_280.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[11] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning110_290.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[12] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning120_300.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[13] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning130_310.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[14] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning140_320.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[15] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning150_330.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[16] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning160_340.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[17] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning170_350.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[18] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning00_180.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[19] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning10_190.png")),bulletSize, bulletSize, false));

            bulletSpriteLaser[20] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning20_200.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[21] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning30_210.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[22] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning40_220.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[23] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning50_230.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[24] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning60_240.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[25] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning70_250.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[26] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning80_260.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[27] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning90_270.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[28] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning100_280.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[29] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning110_290.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[30] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning120_300.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[31] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning130_310.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[32] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning140_320.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[33] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning150_330.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[34] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning160_340.png")),bulletSize, bulletSize, false));
            bulletSpriteLaser[35] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("lightning170_350.png")),bulletSize, bulletSize, false));

            //explosion sprite
            Image [] explosionSprite = new Image[33];

            explosionSprite[0] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), width,height, false));
            explosionSprite[1] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), width,height, false));
            explosionSprite[2] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), width,height, false));
            explosionSprite[3] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), width,height, false));
            explosionSprite[4] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), width,height, false));
            explosionSprite[5] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), width,height, false));
            explosionSprite[6] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), width,height, false));
            explosionSprite[7] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), width,height, false));
            explosionSprite[8] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), width,height, false));
            explosionSprite[9] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")),width, height, false));

            explosionSprite[10] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")),width, height, false));
            explosionSprite[11] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")),width, height, false));
            explosionSprite[12] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")),width, height, false));
            explosionSprite[13] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")),width, height, false));
            explosionSprite[14] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")),width, height, false));
            explosionSprite[15] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")),width, height, false));
            explosionSprite[16] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")),width, height, false));
            explosionSprite[17] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")),width, height, false));
            explosionSprite[18] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")),width, height, false));
            explosionSprite[19] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")),width, height, false));

            explosionSprite[20] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")),width, height, false));
            explosionSprite[21] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")),width, height, false));
            explosionSprite[22] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")),width, height, false));
            explosionSprite[23] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")),width, height, false));
            explosionSprite[24] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")),width, height, false));
            explosionSprite[25] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")),width, height, false));
            explosionSprite[26] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")),width, height, false));
            explosionSprite[27] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")),width, height, false));
            explosionSprite[28] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")),width, height, false));
            explosionSprite[29] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")),width, height, false));

            explosionSprite[30] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")),width, height, false));
            explosionSprite[31] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")),width, height, false));
            explosionSprite[32] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")),width, height, false));


            int miniWidth = width / 3;
            int miniHeight = height / 3;

            Image[] miniExplosionSprite = new Image[33];

            miniExplosionSprite[0] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[1] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion2.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[2] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion3.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[3] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion4.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[4] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion5.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[5] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion6.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[6] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion7.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[7] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion8.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[8] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion9.png")), miniWidth, miniHeight, false));
            miniExplosionSprite[9] =  new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion10.png")),miniWidth, miniHeight, false));

            miniExplosionSprite[10] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion11.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[11] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion12.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[12] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion13.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[13] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion14.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[14] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion15.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[15] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion16.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[16] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion17.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[17] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion18.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[18] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion19.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[19] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion20.png")),miniWidth, miniHeight, false));

            miniExplosionSprite[20] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion21.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[21] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion22.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[22] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion23.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[23] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion24.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[24] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion25.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[25] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion26.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[26] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion27.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[27] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion28.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[28] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion29.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[29] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion30.png")),miniWidth, miniHeight, false));

            miniExplosionSprite[30] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion31.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[31] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion32.png")),miniWidth, miniHeight, false));
            miniExplosionSprite[32] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion33.png")),miniWidth, miniHeight, false));

            Image [] towerSprite =new Image[36];
            towerSprite[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower0.png")), width, height,false));
            towerSprite[1] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower10.png")), width, height, false));
            towerSprite[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower20.png")), width, height, false));
            towerSprite[3] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower30.png")), width, height, false));
            towerSprite[4] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower40.png")), width, height, false));
            towerSprite[5] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower50.png")), width, height, false));
            towerSprite[6] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower60.png")), width, height, false));
            towerSprite[7] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower70.png")), width, height, false));
            towerSprite[8] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower80.png")), width, height, false));
            towerSprite[9] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower90.png")), width, height, false));

            towerSprite[10] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower100.png")),width, height, false));
            towerSprite[11] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower110.png")),width, height, false));
            towerSprite[12] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower120.png")),width, height, false));
            towerSprite[13] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower130.png")),width, height, false));
            towerSprite[14] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower140.png")),width, height, false));
            towerSprite[15] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower150.png")),width, height, false));
            towerSprite[16] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower160.png")),width, height, false));
            towerSprite[17] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower170.png")),width, height, false));
            towerSprite[18] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower180.png")),width, height, false));
            towerSprite[19] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower190.png")),width, height, false));

            towerSprite[20] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower200.png")),width, height, false));
            towerSprite[21] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower210.png")),width, height, false));
            towerSprite[22] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower220.png")),width, height, false));
            towerSprite[23] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower230.png")),width, height, false));
            towerSprite[24] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower240.png")),width, height, false));
            towerSprite[25] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower250.png")),width, height, false));
            towerSprite[26] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower260.png")),width, height, false));
            towerSprite[27] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower270.png")),width, height, false));
            towerSprite[28] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower280.png")),width, height, false));
            towerSprite[29] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower290.png")),width, height, false));

            towerSprite[30] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower300.png")),width, height, false));
            towerSprite[31] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower310.png")),width, height, false));
            towerSprite[32] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower320.png")),width, height, false));
            towerSprite[33] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower330.png")),width, height, false));
            towerSprite[34] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower340.png")),width, height, false));
            towerSprite[35] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Tower350.png")),width, height, false));

            Image [] towerSprite2 =new Image[36];
            for (int i=0;i<36;i++) {
                towerSprite2[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("rocketTower" + i + "0.png")), width, height, false));
            }
            Image [] towerSprite3 =new Image[36];
            for (int i=0;i<36;i++) {
                towerSprite3[i] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("greenLS" + i + "0.png")), width, height, false));
            }

            Image [] towerSpriteEX =new Image[36];
            towerSpriteEX[0]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg00.png")), width, height,false));
            towerSpriteEX[1]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg10.png")), width, height, false));
            towerSpriteEX[2]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg20.png")), width, height, false));
            towerSpriteEX[3]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg30.png")), width, height, false));
            towerSpriteEX[4]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg40.png")), width, height, false));
            towerSpriteEX[5]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg50.png")), width, height, false));
            towerSpriteEX[6]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg60.png")), width, height, false));
            towerSpriteEX[7]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg70.png")), width, height, false));
            towerSpriteEX[8]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg80.png")), width, height, false));
            towerSpriteEX[9]  = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg90.png")), width, height, false));

            towerSpriteEX[10] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg100.png")),width, height, false));
            towerSpriteEX[11] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg110.png")),width, height, false));
            towerSpriteEX[12] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg120.png")),width, height, false));
            towerSpriteEX[13] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg130.png")),width, height, false));
            towerSpriteEX[14] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg140.png")),width, height, false));
            towerSpriteEX[15] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg150.png")),width, height, false));
            towerSpriteEX[16] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg160.png")),width, height, false));
            towerSpriteEX[17] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg170.png")),width, height, false));
            towerSpriteEX[18] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg180.png")),width, height, false));
            towerSpriteEX[19] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg190.png")),width, height, false));

            towerSpriteEX[20] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg200.png")),width, height, false));
            towerSpriteEX[21] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg210.png")),width, height, false));
            towerSpriteEX[22] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg220.png")),width, height, false));
            towerSpriteEX[23] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg230.png")),width, height, false));
            towerSpriteEX[24] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg240.png")),width, height, false));
            towerSpriteEX[25] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg250.png")),width, height, false));
            towerSpriteEX[26] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg260.png")),width, height, false));
            towerSpriteEX[27] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg270.png")),width, height, false));
            towerSpriteEX[28] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg280.png")),width, height, false));
            towerSpriteEX[29] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg290.png")),width, height, false));

            towerSpriteEX[30] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg300.png")),width, height, false));
            towerSpriteEX[31] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg310.png")),width, height, false));
            towerSpriteEX[32] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg320.png")),width, height, false));
            towerSpriteEX[33] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg330.png")),width, height, false));
            towerSpriteEX[34] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg340.png")),width, height, false));
            towerSpriteEX[35] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mg350.png")),width, height, false));

            int width_hp = 120;
            int height_hp = 30;

            Image [] listHealthBarImg = new Image[10];
            listHealthBarImg[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_10.png")), width_hp, height_hp, false));
            listHealthBarImg[1] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_20.png")), width_hp, height_hp, false));
            listHealthBarImg[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_30.png")), width_hp, height_hp, false));
            listHealthBarImg[3] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_40.png")), width_hp, height_hp, false));
            listHealthBarImg[4] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_50.png")), width_hp, height_hp, false));
            listHealthBarImg[5] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_60.png")), width_hp, height_hp, false));
            listHealthBarImg[6] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_70.png")), width_hp, height_hp, false));
            listHealthBarImg[7] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_80.png")), width_hp, height_hp, false));
            listHealthBarImg[8] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_90.png")), width_hp, height_hp, false));
            listHealthBarImg[9] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("health_bar_100.png")), width_hp, height_hp, false));

            Image dialog = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Atomic1.png")), 600, 500, false));

            Image score = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("SmallBox.png")), 300, 150, false));
            Image titleScore = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("HealthBar.png")), 300, 70, false));

            int numWidth = 50;
            int numHeight = 70;

            Image [] listNumberImg = new Image[10];
            listNumberImg[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("0.png")), numWidth, numHeight, false));
            listNumberImg[1] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("1.png")), numWidth, numHeight, false));
            listNumberImg[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("2.png")), numWidth, numHeight, false));
            listNumberImg[3] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("3.png")), numWidth, numHeight, false));
            listNumberImg[4] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("4.png")), numWidth, numHeight, false));
            listNumberImg[5] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("5.png")), numWidth, numHeight, false));
            listNumberImg[6] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("6.png")), numWidth, numHeight, false));
            listNumberImg[7] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("7.png")), numWidth, numHeight, false));
            listNumberImg[8] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("8.png")), numWidth, numHeight, false));
            listNumberImg[9] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("9.png")), numWidth, numHeight, false));

            Image [] dialogUpgradeTower1 = new Image[3];
            dialogUpgradeTower1[0] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mgDialog22.png")), 650, 390, false));
            dialogUpgradeTower1[1] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mgDialog33.png")), 650, 390, false));
            dialogUpgradeTower1[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("mgDialogTOP.png")), 650, 390, false));
//            dialogUpgradeTower1[2] = new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("2.png")), 650, 390, false));
//            new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("upgradeDialog.png")),650,390,false))


            Bitmap green = Bitmap.createBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")));
            Bitmap red = Bitmap.createBitmap(BitmapFactory.decodeStream(getAssets().open("red.png")));

            game = new Game(renderer, rows, columns,50,50, width)
                    .setBackgroundImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("maphi.jpg")), borderX, borderY, false)))
                    .setFieldImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("green.png")), width, height, false)))
                    .setFieldImageTower(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Exceptione.png")), width, height, false)))
                    .setFieldImageTowerDen(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("red.png")), width, height, false)))
                    .setEnemySprite(sprite)
                    .setBulletSprite(bulletSprite)
                    .setBulletSpriteLaser(spriteRocket)
                    .setLaserSprite(new Image[]{
                            new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("greenPhoton.png")), 10, 10, false))
                    })
                    .setExplodeSprite(explosionSprite)
                    .setMiniExplodeSprite(miniExplosionSprite)
                    .setTowerSprite1(towerSprite)
                    .setTowerSprite2(towerSprite2)
                    .setTowerSprite3(towerSprite3)
                    .setTowerSpriteEx(towerSpriteEX)
                    .setHealthBarSprite(listHealthBarImg)
                    .setBorders(borderX, borderY)
                    .setDialogueImageTowerUpgradeLevel(dialogUpgradeTower1)
                    .setUpgradeButtonImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("UPGRADE.png")),250,90,false)))
                    .setCloseButtonImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("CLOSE.png")),250,90,false)))
                    .setDialogue(new AndroidBitmapImage(Bitmap.createScaledBitmap(red, dialog.getWidth(), dialog.getHeight(), false))/*dialog*/)
                    .setRedNestedDialogue(new AndroidBitmapImage(Bitmap.createScaledBitmap(red, dialog.getWidth() / 2, dialog.getHeight() / 2, false))/*dialog*/)
//                    .setGreenDialogue(new AndroidBitmapImage(Bitmap.createScaledBitmap(green, dialog.getWidth(), dialog.getHeight(), false))/*dialog*/)
                    .setGreenDialogue(new AndroidBitmapImage(Bitmap.createScaledBitmap(green, 660, 490, false))/*dialog*/)
                    .setSelectTowerBackgroudnImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(red, 200, 600, false))/*dialog*/)
                    .setSelectionImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(green, 200, 200, false))/*dialog*/)
                    .setDeselectionImage(new AndroidBitmapImage(Bitmap.createScaledBitmap(red, 200, 200, false))/*dialog*/)
                    .setScoreBackGrImage(score)
                    .setScorenumbersImages(listNumberImg)
                    .setScoreTitle(titleScore);


        } catch (IOException ex) {
            Log.e(DaemonUtils.tag(), "Could not init game!", ex);
        }

        Log.e(DaemonUtils.tag(), "ACTIVITY ON CREATE----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(DaemonUtils.tag(), "ACTIVITY ON START----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
        game.run();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(DaemonUtils.tag(), "ACTIVITY ON ON PAUSE----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(DaemonUtils.tag(), "ACTIVITY ON STOP----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
        game.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(DaemonUtils.tag(), "ACTIVITY ON DESTROY----------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
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
