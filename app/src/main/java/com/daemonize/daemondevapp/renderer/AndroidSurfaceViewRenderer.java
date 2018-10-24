package com.daemonize.daemondevapp.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.daemonize.daemondevapp.scene.Scene2D;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.TimeUnits;

import java.util.Collections;

public class AndroidSurfaceViewRenderer extends SurfaceView implements Renderer2D, Runnable, SurfaceHolder.Callback {

    private Scene2D scene;

    @Override
    public Scene2D getScene() {
        return scene;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer setScene(Scene2D scene) {
        this.scene = scene;
        return this;
    }

    private Thread drawThread;
    private volatile boolean drawing;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}


    public AndroidSurfaceViewRenderer(Context context) {
        super(context);
        //this.views = new ArrayList<>();
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        this.paint = new Paint();
        //this.backgroundView = new ImageViewImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer start() {

        if(scene.getViews() == null)
            throw new IllegalStateException("Scene not set!");

        if(scene.getViews().isEmpty())
            throw new IllegalStateException("No views to be drawn. Add some views!");

        if(!scene.isLocked())
            throw new IllegalStateException("Scene not locked!");

        drawThread = new Thread(this);
        drawThread.setName("AndroidSurfaceViewRenderer");
        drawing = true;
        drawThread.start();

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer stop() {
        drawing = false;
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            //
        }
        return this;
    }

    @Override
    public void run() {
        while (drawing){
            long t0 = System.nanoTime();
            drawViews();
            double duration = DaemonUtils.convertNanoTimeUnits(System.nanoTime() - t0, TimeUnits.MILLISECONDS);
            if (duration < 15) {
                try {
                    Thread.sleep(16 - (long) duration);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    protected void drawViews(){

        if (surfaceHolder.getSurface().isValid()) {

            canvas = surfaceHolder.lockCanvas();

            for (ImageView view : scene.getViews()) {

                if (view.isShowing() && view.getImage() != null)//TODO this should never be null
                    canvas.drawBitmap(
                            ((Bitmap) view.getImage().getImageImp()),
                            view.getStartingX(),
                            view.getStartingY(),
                            paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }



}
