package com.daemonize.daemondevapp.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.daemonize.daemondevapp.images.AndroidBitmapImage;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemondevapp.view.ImageViewImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidSurfaceViewRenderer extends SurfaceView implements Renderer, Runnable, SurfaceHolder.Callback {


    private volatile int windowSizeX;
    private volatile int windowSizeY;

    private ImageViewImpl backgroundView;
    private List<ImageViewImpl> views;

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
        this.views = new ArrayList<>();
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        this.paint = new Paint();
        this.backgroundView = new ImageViewImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer setWindowSize(int x, int y) {
        this.windowSizeX = x;
        this.windowSizeY = y;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer setBackgroundImage(Image image) {

        if (drawing)
            throw new IllegalStateException("Can not set background image while the engine is drawing!");

        if (image.getWidth() != windowSizeX || image.getHeight() != windowSizeY) {
            Bitmap rescaledBackground = Bitmap.createScaledBitmap(((Bitmap) image.getImageImp()), windowSizeX, windowSizeY, false);
            backgroundView.setImage(new AndroidBitmapImage(rescaledBackground)); //TODO optimize this

        } else {
            this.backgroundView.setImage(image);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer start() {

        if(views.isEmpty())
            throw new IllegalStateException("No views to be drawn. Add some views!");

        drawThread = new Thread(this);
        drawThread.setName("AndroidSurfaceViewDispatcherEngine");
        drawing = true;
        drawThread.start();

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AndroidSurfaceViewRenderer stop() {
        drawing = false;
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            //
        }
        return this;
    }

    @Override
    public ImageView createImageView(int zIndex) {

        if (drawing)
            throw new IllegalStateException("Can not create views while the engine is drawing!");

        ImageViewImpl view = new ImageViewImpl().hide().setX(0).setY(0).setZindex(zIndex);
        views.add(view);

        return view;
    }

    @Override
    public int viwesSize() {
        return views.size();
    }

    @Override
    public void run() {
        Collections.sort(views);
        while (drawing){
//            long t0 = System.nanoTime();
            drawViews();
//            double duration = DaemonUtils.convertNanoTimeUnits(System.nanoTime() - t0, TimeUnits.MILLISECONDS);
//            System.out.println("DRAW LASTED: " + Double.toString(duration));
//            if (duration < 15) {
//                try {
//                    Thread.sleep(16 - (long) duration);
//                } catch (InterruptedException e) {
//                    //
//                }
//            }
        }
    }

    protected void drawViews(){

        if (surfaceHolder.getSurface().isValid()) {

            //locking the canvas
            canvas = surfaceHolder.lockCanvas();

            canvas.drawBitmap(
                    (Bitmap) backgroundView.getImage().getImageImp(),
                    0,
                    0,
                    paint
            );

            for (ImageViewImpl view : views) {
                //Drawing the player
                if (view.isShowing() && view.getImage() != null)//TODO this should never be null
                    canvas.drawBitmap(
                            ((Bitmap) view.getImage().getImageImp()),
                            view.getX() - view.getxOffset(),
                            view.getY() - view.getyOffset(),
                            paint);
            }

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }



}
