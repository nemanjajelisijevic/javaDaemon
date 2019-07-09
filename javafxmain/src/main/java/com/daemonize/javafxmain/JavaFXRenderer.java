package com.daemonize.javafxmain;

import com.daemonize.game.renderer.DrawConsumer;
import com.daemonize.game.renderer.Renderer2D;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.daemonize.game.scene.Scene2D;
import com.daemonize.game.scene.views.ImageView;


public class JavaFXRenderer implements Renderer2D<JavaFXRenderer> {

    private double width;
    private double height;

    private Scene2D scene;
    private DrawConsumer drawConsumer;

    private volatile boolean dirtyFlag;

    private GraphicsContext gc;
    private AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if (dirtyFlag) {
                drawScene();
                dirtyFlag = false;
            }
        }
    };

    public JavaFXRenderer(GraphicsContext gc, int width, int height) {
        this.gc = gc;
        this.gc.setFill(Color.BLACK);
        this.width = width;
        this.height = height;
        this.drawConsumer = new DrawConsumer(this, "Renderer draw consumer");
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
    public JavaFXRenderer setDirty() {
        this.dirtyFlag = true;
        return this;
    }

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
    public void stop() {
        drawConsumer.stop();
        animationTimer.stop();
    }

    @Override
    public JavaFXRenderer drawScene() {
        gc.fillRect(0, 0, width, height);
        for (ImageView view : scene.getViews())
            if (view.isShowing())
                gc.drawImage(
                        (javafx.scene.image.Image) view.getImage().getImageImp(),
                        view.getStartingX(),
                        view.getStartingY()
                );
        return this;
    }

    @Override
    public boolean consume(Runnable runnable) {
        return drawConsumer.consume(runnable);
    }

    @Override
    public JavaFXRenderer setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        drawConsumer.setUncaughtExceptionHandler(handler);
        return this;
    }
}
