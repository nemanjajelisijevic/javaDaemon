package com.daemonize.daemondevapp.renderer;

//package renderer;
//
//import com.daemonize.daemonengine.utils.DaemonUtils;
//import com.daemonize.daemonengine.utils.TimeUnits;
//import images.Image;
//import images.JavaFXImage;
//import javafx.event.Event;
//import javafx.event.EventHandler;
//import javafx.event.EventType;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.effect.BlendMode;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.Paint;
//import javafx.stage.Stage;
//import view.ImageView;
//import view.ImageViewImpl;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaFXRenderer {//implements Renderer2D, Runnable {

//    private volatile int windowSizeX;
//    private volatile int windowSizeY;
//
//    private ImageViewImpl backgroundView;
//    private List<ImageViewImpl> views;
//
//    private Thread drawThread;
//    private volatile boolean drawing;
//
//    //    private Stage stage;
//    //    private Scene scene;
//    //    private Canvas canvas;
//    private GraphicsContext gc;
//
//    public JavaFXRender(GraphicsContext gc, int width, int height) {
//        //        this.stage = stage;
//        //        this.canvas = new Canvas(width,height);
//        //
//        //        Group root = new Group(canvas);
//        //        this.scene = new Scene(root);
//        //        this.stage.setTitle("Tower Defense");
//        //        this.stage.setScene(scene);
//
//        this.windowSizeX = width;// (int) canvas.getWidth();
//        this.windowSizeY = height; //(int) canvas.getHeight();
//        this.backgroundView = new ImageViewImpl();
//        this.views = new ArrayList<>();
//        this.gc = gc;//canvas.getGraphicsContext2D();
//
//
//    }
//
//
//    @Override
//    public JavaFXRender setWindowSize(int x, int y) {
//        return this;
//    }
//
//    @Override
//    public JavaFXRender setBackgroundImage(Image image) {
//        if (drawing)
//            throw new IllegalStateException("Can not set background image while the engine is drawing!");
//
//        if (image.getWidth() != windowSizeX || image.getHeight() != windowSizeY) {
//            javafx.scene.image.Image rescaledBackground = (javafx.scene.image.Image) image.getImageImp();
//            backgroundView.setImage(new JavaFXImage(rescaledBackground)); //TODO optimize this
//
//        } else {
//            this.backgroundView.setImage(image);
//        }
//
//        return this;
//    }
//
//    @Override
//    public JavaFXRender start() {
//        if(views.isEmpty())
//            throw new IllegalStateException("No views to be drawn. Add some views!");
//
//        drawThread = new Thread(this);
//        drawThread.setName("JavaFXRenderer");
//        drawing = true;
//        drawThread.start();
//
//        return this;
//    }
//
//    @Override
//    public JavaFXRender stop() {
//        drawing = false;
//        try {
//            drawThread.join();
//        } catch (InterruptedException e) {
//            //
//        }
//        return this;
//    }
//
//    @Override
//    public ImageView addImageView(int zIndex) {
//        if (drawing)
//            throw new IllegalStateException("Can not create views while the engine is drawing!");
//
//        ImageViewImpl view = new ImageViewImpl().hide().setAbsoluteX(0).setAbsoluteY(0).setZindex(zIndex);
//        views.add(view);
//
//        return view;
//    }
//
//    @Override
//    public int viwesSize() {
//        return views.size();
//    }
//
//    @Override
//    public void run() {
//
//        Collections.sort(views);
//        while (drawing){
//            long t0 = System.nanoTime();
//            drawViews();
//            double duration = DaemonUtils.convertNanoTimeUnits(System.nanoTime() - t0, TimeUnits.MILLISECONDS);
//            //
//            // System.out.println("DRAW LASTED: " + Double.toString(duration));
//            if (duration < 15) {
//                try {
//                    Thread.sleep(16 - (long) duration);
//                } catch (InterruptedException e) {
//                    //
//                }
//            }
//        }
//    }
//
//    protected void drawViews(){
//
//
//        gc.drawImage((javafx.scene.image.Image) backgroundView.getImage().getImageImp(),0,0);
//
//        for (ImageViewImpl view : views) {
//            //Drawing the player
//            if (view.isShowing() && view.getImage() != null)//TODO this should never be null
//                gc.drawImage((javafx.scene.image.Image) view.getImage().getImageImp(),
//                        view.getAbsoluteX() - view.getxOffset(),
//                        view.getAbsoluteY() - view.getyOffset());
//        }
//
//    }



}
