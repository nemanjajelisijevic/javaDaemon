package com.daemonize.javafxgraphics2d;

import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.renderer.DrawConsumer;
import com.daemonize.graphics2d.renderer.Renderer2D;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.SceneDrawer;
import com.daemonize.graphics2d.scene.views.ImageView;


public class JavaFXRenderer implements Renderer2D<JavaFXRenderer> {

    private double width;
    private double height;

    private Scene2D scene;
    private DrawConsumer drawConsumer;

    private volatile boolean dirtyFlag;

    private GraphicsContext gc;


    private class CameraSceneDrawer implements SceneDrawer {

        private Camera2D camera2D;
        private int cameraX, cameraY;

        public CameraSceneDrawer(Camera2D camera2D) {
            this.camera2D = camera2D;
            this.cameraX = camera2D.getX();
            this.cameraY = camera2D.getY();
        }

        @Override
        public void drawView(ImageView view, float x, float y) {
            gc.drawImage((javafx.scene.image.Image) view.getImage().getImageImp(), x, y);
        }

        @Override
        public void drawView(ImageView view) {
            gc.drawImage(
                    (javafx.scene.image.Image) view.getImage().getImageImp(),
                    view.getStartingX() - cameraX,
                    view.getStartingY() - cameraY
            );
        }

        @Override
        public void drawScene(Scene2D scene2D) {

            gc.fillRect(0, 0, width, height);

            cameraX = camera2D.getX();
            cameraY = camera2D.getY();

            for (ImageView view : scene.getViews())
                    view.draw(this);
        }
    }

    private SceneDrawer sceneDrawer = new SceneDrawer() {

        @Override
        public void drawView(ImageView view, float x, float y) {
            gc.drawImage((javafx.scene.image.Image) view.getImage().getImageImp(), x, y);
        }

        @Override
        public void drawView(ImageView view) {
            drawView(view, view.getStartingX(), view.getStartingY());
        }

        @Override
        public void drawScene(Scene2D scene2D) {
            gc.fillRect(0, 0, width, height);
            for (ImageView view : scene.getViews())
                if (view.isShowing())
                    drawView(view);
        }
    };

    private AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if (dirtyFlag) {
                drawScene();
                dirtyFlag = false;
            }
        }
    };

    public JavaFXRenderer(GraphicsContext gc, int width, int height, int closureQueueSize) {
        this.gc = gc;
        this.gc.setFill(Color.BLACK);
        this.width = width;
        this.height = height;
        this.drawConsumer = new DrawConsumer(this, "Renderer draw consumer", closureQueueSize);
    }

    @Override
    public JavaFXRenderer setCamera(Camera2D camera) {
        this.sceneDrawer = new CameraSceneDrawer(camera);
        return this;
    }

    @Override
    public int closureQueueSize() {
        return drawConsumer.closureQueueSize();
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
        sceneDrawer.drawScene(scene);
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


    @Override
    public void pause() {
        drawConsumer.pause();
    }

    @Override
    public void cont() {
        drawConsumer.cont();
    }
}
