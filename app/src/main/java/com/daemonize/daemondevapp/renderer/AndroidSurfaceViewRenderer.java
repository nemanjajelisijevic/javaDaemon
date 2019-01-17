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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AndroidSurfaceViewRenderer extends SurfaceView implements Renderer2D<AndroidSurfaceViewRenderer>, Runnable, SurfaceHolder.Callback {

    private Scene2D scene;

    private volatile boolean dirtyFlag;
    private Lock dirtyLock = new ReentrantLock();
    private Condition dirtyCondition = dirtyLock.newCondition();

    @Override
    public void setDirty() {
        dirtyLock.lock();
        this.dirtyFlag = true;
        dirtyCondition.signal();
        dirtyLock.unlock();
    }

    private void clean() {
        dirtyLock.lock();
        dirtyFlag = false;
        dirtyLock.unlock();
    }

    @Override
    public Scene2D getScene() {
        return scene;
    }

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

    @Override
    public AndroidSurfaceViewRenderer stop() {
        drawing = false;
        try {

            dirtyLock.lock();
            dirtyFlag = true;
            dirtyCondition.signal();
            dirtyLock.unlock();

            drawThread.join();
        } catch (InterruptedException e) {
            //
        }
        return this;
    }

    @Override
    public void run() {
        while (drawing){

            dirtyLock.lock();
            try {
                while (!dirtyFlag)
                    dirtyCondition.await();
            } catch (InterruptedException e) {
             //
            } finally {
                dirtyLock.unlock();
            }

            drawViews();
            clean();
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
