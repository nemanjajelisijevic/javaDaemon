package com.daemonize.javafxmain;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.TimeUnits;
import com.daemonize.game.renderer.DrawConsumer;
import com.daemonize.game.renderer.Renderer2D;

import com.daemonize.game.images.Image;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.daemonize.game.scene.Scene2D;
import com.daemonize.game.view.ImageView;
import com.daemonize.game.view.ImageViewImpl;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JavaFXRenderer implements Renderer2D<JavaFXRenderer> {

    private double width;
    private double height;

    private Scene2D scene;
    private DaemonConsumer drawConsumer;

    private GraphicsContext gc;
    private AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            drawViews();
        }
    };


    public JavaFXRenderer(GraphicsContext gc, int width, int height) {
        this.gc = gc;
        this.gc.setFill(Color.BLACK);
        this.width = width;
        this.height = height;
        this.drawConsumer = new DaemonConsumer("Renderer draw consumer");
    }

    @Override
    public Scene2D getScene() {
        return scene;
    }

    @Override
    public JavaFXRenderer setScene(Scene2D scene) {
        this.scene = scene;
        return this;
    }

    @Override
    public void setDirty() {}

    @Override
    public JavaFXRenderer start() {

        if(scene.getViews() == null)
            throw new IllegalStateException("Scene not set!");

        if(scene.getViews().isEmpty())
            throw new IllegalStateException("No views to be drawn. Add some views!");

        if(!scene.isLocked())
            throw new IllegalStateException("Scene not locked!");

        animationTimer.start();
        drawConsumer.start();
        return this;
    }

    @Override
    public JavaFXRenderer stop() {
        drawConsumer.stop();
        animationTimer.stop();
        return this;
    }

    protected void drawViews() {
        gc.fillRect(0, 0, width, height);
        for (ImageView view : scene.getViews())
            if (view.isShowing())
                gc.drawImage(
                        (javafx.scene.image.Image) view.getImage().getImageImp(),
                        view.getStartingX(),
                        view.getStartingY()
                );
    }

    @Override
    public boolean consume(Runnable runnable) {
        return drawConsumer.consume(runnable);
    }

}
