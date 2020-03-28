package com.daemonize.androidgraphics2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.View;

import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.renderer.DrawConsumer;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;

public class AndroidCustomHWAViewRenderer implements Renderer2D<AndroidCustomHWAViewRenderer> {

    private AndroidCustomHWAView view;
    private Scene2D scene;

    private volatile boolean dirtyFlag;
    private DrawConsumer drawConsumer;

    private Paint paint;

    private Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            view.invalidate();
        }
    };

    public AndroidCustomHWAViewRenderer(AndroidCustomHWAView view, int closureQueueSize) {
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.view = view.setRenderer(this);
        this.view.setLayerType(View.LAYER_TYPE_HARDWARE, this.paint);
        this.drawConsumer = new DrawConsumer(this, "AndroidCustomHWAViewRenderer draw consumer", closureQueueSize);
    }

    @Override
    public AndroidCustomHWAViewRenderer setCamera(Camera2D camera) {
        return this;
    }

    @Override
    public Scene2D getScene() {
        return scene;
    }

    @Override
    public AndroidCustomHWAViewRenderer setScene(Scene2D scene) {
        this.scene = scene;
        return this;
    }

    @Override
    public AndroidCustomHWAViewRenderer setDirty() {
        this.dirtyFlag = true;
        this.view.postOnAnimation(invalidateRunnable);
        return this;
    }

    public boolean getDirty() {
        return dirtyFlag;
    }

    @Override
    public AndroidCustomHWAViewRenderer drawScene() {
        setDirty();
        return this;
    }

    AndroidCustomHWAViewRenderer drawSceneFromView(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (ImageView view : scene.getViews()) {
            if (view.isShowing()) {
                canvas.drawBitmap(
                        ((Bitmap) view.getImage().getImageImp()),
                        view.getStartingX(),
                        view.getStartingY(),
                        paint
                );
            }
        }
        return this;
    }

    @Override
    public boolean consume(Runnable runnable) {
        return drawConsumer.consume(runnable);
    }

    @Override
    public AndroidCustomHWAViewRenderer start() {
        if(scene.getViews() == null)
            throw new IllegalStateException("Scene not set!");

        if(scene.getViews().isEmpty())
            throw new IllegalStateException("No views to be rendered. Add some views!");

        if(!scene.isLocked())
            throw new IllegalStateException("Scene not locked!");

        drawConsumer.start();
        return this;
    }

    @Override
    public int closureQueueSize() {
        return drawConsumer.closureQueueSize();
    }

    @Override
    public void stop() {
        drawConsumer.stop();
    }

    @Override
    public AndroidCustomHWAViewRenderer setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
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
