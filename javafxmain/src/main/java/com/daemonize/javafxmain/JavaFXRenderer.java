package com.daemonize.javafxmain;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.TimeUnits;
import com.daemonize.game.renderer.DrawConsumer;
import com.daemonize.game.renderer.Renderer2D;

import com.daemonize.game.images.Image;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

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

public class JavaFXRenderer implements Renderer2D<JavaFXRenderer>, Runnable {

    private Scene2D scene;

    private volatile boolean dirtyFlag;
    private Lock dirtyLock = new ReentrantLock();
    private Condition dirtyCondition = dirtyLock.newCondition();

    private DrawConsumer drawConsumer;

    private Thread drawThread;
    private volatile boolean drawing;

    private GraphicsContext gc;

    public JavaFXRenderer(GraphicsContext gc) {
        this.gc = gc;
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
    public JavaFXRenderer start() {

        if(scene.getViews() == null)
            throw new IllegalStateException("Scene not set!");

        if(scene.getViews().isEmpty())
            throw new IllegalStateException("No views to be drawn. Add some views!");

        if(!scene.isLocked())
            throw new IllegalStateException("Scene not locked!");

        drawThread = new Thread(this);
        drawThread.setName("JavaFXRenderer");
        drawing = true;
        drawThread.start();
        drawConsumer.start();
        return this;
    }

    @Override
    public JavaFXRenderer stop() {
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

            long t0 = System.nanoTime();
            drawViews();
            double duration = DaemonUtils.convertNanoTimeUnits(System.nanoTime() - t0, TimeUnits.MILLISECONDS);
            //
            // System.out.println("DRAW LASTED: " + Double.toString(duration));
            if (duration < 15) {
                try {
                    Thread.sleep(16 - (long) duration);
                } catch (InterruptedException e) {
                    //
                }
            }


            clean();
        }
    }

    protected void drawViews(){
        for (ImageView view : scene.getViews()) {
            if (view.isShowing())
                gc.drawImage(
                        (javafx.scene.image.Image) view.getImage().getImageImp(),
                        view.getStartingX(),
                        view.getStartingY()
                );
        }
    }

    @Override
    public boolean consume(Runnable runnable) {
        return drawConsumer.consume(runnable);
    }

}
