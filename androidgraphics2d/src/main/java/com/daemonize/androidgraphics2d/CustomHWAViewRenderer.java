package com.daemonize.androidgraphics2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.View;

import com.daemonize.graphics2d.renderer.DrawConsumer;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;

public class CustomHWAViewRenderer implements Renderer2D<CustomHWAViewRenderer> {

    private CustomHWAView view;
    private Scene2D scene;

    private volatile boolean dirtyFlag;
    private DrawConsumer drawConsumer;

    private Canvas canvas;
    private Paint paint;

    public CustomHWAViewRenderer(CustomHWAView view) {
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.view = view.setRenderer(this);
        this.view.setLayerType(View.LAYER_TYPE_HARDWARE, this.paint);
        this.drawConsumer = new DrawConsumer(this, "CustomHWAViewRenderer draw consumer");
    }

    public CustomHWAViewRenderer setCanvas(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }

    @Override
    public Scene2D getScene() {
        return scene;
    }

    @Override
    public CustomHWAViewRenderer setScene(Scene2D scene) {
        this.scene = scene;
        return this;
    }

    @Override
    public CustomHWAViewRenderer setDirty() {
        this.dirtyFlag = true;
        return this;
    }

    public boolean getDirty() {
        return dirtyFlag;
    }

    @Override
    public CustomHWAViewRenderer drawScene() {
        setDirty();
        return this;
    }

    CustomHWAViewRenderer drawSceneFromView() {
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
    public CustomHWAViewRenderer start() {
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
    public void stop() {
        drawConsumer.stop();
    }

    @Override
    public CustomHWAViewRenderer setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        drawConsumer.setUncaughtExceptionHandler(handler);
        return null;
    }
}
