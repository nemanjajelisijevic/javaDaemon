package com.daemonize.androidgraphics2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.TextureView;
import android.view.View;

import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.renderer.DrawConsumer;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AndroidTextureViewRenderer implements Renderer2D<AndroidTextureViewRenderer>, Runnable {

    private Scene2D scene;

    private volatile boolean dirtyFlag;
    private Lock dirtyLock = new ReentrantLock();
    private Condition dirtyCondition = dirtyLock.newCondition();

    private DrawConsumer drawConsumer;

    private Thread drawThread;
    private volatile boolean drawing;

    private Paint paint;
    private Canvas canvas;
    private TextureView textureView;

    @Override
    public AndroidTextureViewRenderer setDirty() {
        dirtyLock.lock();
        this.dirtyFlag = true;
        dirtyCondition.signal();
        dirtyLock.unlock();
        return this;
    }

    @Override
    public AndroidTextureViewRenderer setCamera(Camera2D camera) {
        return this;
    }

    @Override
    public Scene2D getScene() {
        return scene;
    }

    @Override
    public AndroidTextureViewRenderer setScene(Scene2D scene) {
        this.scene = scene;
        return this;
    }


    public AndroidTextureViewRenderer(TextureView textureView, int closureQueueSize) {
        this.textureView = textureView;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.textureView.setLayerType(View.LAYER_TYPE_HARDWARE, this.paint);
        //this.textureView.getSurfaceTexture().attachToGLContext();
//        this.surfaceHolder = surfaceView.getHolder();
//        this.surfaceHolder.addCallback(this);
//        this.textureView.setFormat(PixelFormat.TRANSPARENT);
        this.drawConsumer = new DrawConsumer(this, "Renderer draw consumer", closureQueueSize);
    }

    @Override
    public AndroidTextureViewRenderer start() {

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
    public AndroidTextureViewRenderer drawScene() {
        if (textureView.isAvailable()) {
            canvas = textureView.lockCanvas();
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
            textureView.unlockCanvasAndPost(canvas);
        }
        return this;
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
    public AndroidTextureViewRenderer setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
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
