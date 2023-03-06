package com.daemonize.androidgraphics2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.renderer.DrawConsumer;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AndroidSurfaceViewRenderer implements Renderer2D<AndroidSurfaceViewRenderer>, Runnable, SurfaceHolder.Callback {

    private Scene2D scene;

    private volatile boolean dirtyFlag;
    private Lock dirtyLock = new ReentrantLock();
    private Condition dirtyCondition = dirtyLock.newCondition();

    private DrawConsumer drawConsumer;

    private Thread drawThread;
    private volatile boolean drawing;

    private Paint paint;
    private Canvas canvas;
    protected SurfaceHolder surfaceHolder;

    @Override
    public AndroidSurfaceViewRenderer setDirty() {
        dirtyLock.lock();
        this.dirtyFlag = true;
        dirtyCondition.signal();
        dirtyLock.unlock();
        return this;
    }

    @Override
    public AndroidSurfaceViewRenderer setCamera(Camera2D camera) {
        return this;
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

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}


    public AndroidSurfaceViewRenderer(SurfaceView surfaceView, int closureQueueSize) {
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        surfaceView.setLayerType(View.LAYER_TYPE_HARDWARE, this.paint);
        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        this.drawConsumer = new DrawConsumer(this, "Renderer draw consumer", closureQueueSize);
    }

    @Override
    public AndroidSurfaceViewRenderer start() {

        if(scene.getViews() == null)
            throw new IllegalStateException("Scene not set!");

        if(scene.getViews().isEmpty())
            throw new IllegalStateException("No views to be rendered. Add some views!");

        if(!scene.isLocked())
            throw new IllegalStateException("Scene not locked!");

        drawThread = new Thread(this);
        drawThread.setPriority(Thread.MAX_PRIORITY);
        drawThread.setName("AndroidSurfaceViewRenderer");
        drawing = true;
        drawConsumer.start();
        drawThread.start();
        return this;
    }

    @Override
    public void stop() {
        drawConsumer.stop();
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
                dirtyFlag = false;
                dirtyLock.unlock();
            }
            drawScene();
        }
    }

    @Override
    public AndroidSurfaceViewRenderer drawScene() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = getCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            for (ImageView view : scene.getViews()) {
                if (view.isShowing()) {
                    canvas.drawBitmap(
                            ((Bitmap) view.getImage().getImageImp()),
                            view.getRenderingX(),
                            view.getRenderingY(),
                            paint
                    );
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
        return this;
    }

    protected Canvas getCanvas() {
        return surfaceHolder.lockCanvas();
    }

    @Override
    public boolean consume(Runnable runnable) {
        return drawConsumer.consume(runnable);
    }

    @Override
    public int closureQueueSize() {
        return drawConsumer.closureQueueSize();
    }

    @Override
    public AndroidSurfaceViewRenderer setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        drawThread.setUncaughtExceptionHandler(handler);
        drawConsumer.setUncaughtExceptionHandler(handler);
        return this;
    }

    @Override
    public void pause() {
        drawConsumer.pause();
    }

    @Override
    public void cont() {
        drawConsumer.cont();
    }
}
