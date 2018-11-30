package com.daemonize.daemondevapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.daemonize.daemondevapp", appContext.getPackageName());
    }

    @Test
    public void rotateBmpByStep() {
//        Bitmap orBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getAssets().open("Explosion1.png")), width,height, false));

//        Bitmap bitmap = BitmapFactory.decodeFile("/storage/usb0/rocket00.png");//lightning.png");
        Bitmap bitmap = null;
        File f = new File("/storage/usb0/rocket00.png");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Matrix m = new Matrix();
        m.setRotate(10,(float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        Bitmap bmp = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth() / 2, bitmap.getHeight() / 2, m,true);
    }
}
