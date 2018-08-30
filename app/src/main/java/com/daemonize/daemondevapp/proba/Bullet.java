package com.daemonize.daemondevapp.proba;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonengine.closure.Closure;

import java.util.Iterator;
import java.util.List;

public class Bullet implements ImageMoverM {

    protected int damage = 1;

    protected List<Bitmap> sprite;
    protected Iterator<Bitmap> spriteIterator;

    protected volatile Velocity velocity;

    protected volatile float lastX;
    protected volatile float lastY;

    protected float borderX;
    protected float borderY;

    private ImageView view;

    public Bullet(List<Bitmap> sprite,
                  Velocity velocity,
                  //new Velocity(velocity, new Direction(80, 20)); destination ov enemy
                  int damage,
                  Pair<Float, Float> startingPos ) {
        this.sprite = sprite;
        this.velocity = velocity;
        this.damage = damage;
        lastX = startingPos.first;
        lastY = startingPos.second;
        spriteIterator = sprite.iterator();
        setDirectionAndMove(velocity.direction.coeficientX, velocity.direction.coeficientY,velocity.intensity);
    }

    public ImageView getView() {
        return view;
    }

    public Bullet setView(ImageView view) {
        this.view = view;
        return this;
    }

    protected Bitmap iterateSprite() {
        if(!spriteIterator.hasNext()) {
            spriteIterator = sprite.iterator();
        }
        return spriteIterator.next();
    }

    @Override
    public Pair<Float, Float> getLastCoordinates() {
        return Pair.create(lastX, lastY);
    }

    @Override
    public Velocity getVelocity() {
        return null;
    }

    @Override
    public PositionedBitmap setLastCoordinates(float lastX, float lastY) {
        this.lastX = lastX;
        this.lastY = lastY;

        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite();

        ret.positionX = lastX;
        ret.positionY = lastY;

        return ret;
    }

    @Override
    public void setDirection(Direction direction) {
        this.velocity.direction = direction;
    }

    @Override
    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
        //startMoving();
    }

    @Override
    public void setDirectionAndMove(float x, float y, float velocityInt) {

        exploading = false;

        float dX = x - lastX;
        float dY = y - lastY;

        float a,b;
        boolean signY = dY >= 0;
        boolean signX = dX >= 0;
        velocity.intensity = velocityInt;

        //        if (Math.abs(dY) == Math.abs(dX)) {
        //            a = dY == 0 ? 0 : Math.abs((dX) / dY);
        //            b = dY == 0 ? 0 : Math.abs((dY) / dX);
        velocity.direction = new Direction(dX, dY);
        //startMoving();
    }


    @Override
    public void setVelocity(float velocity) {
        this.velocity.intensity = velocity;
//        startMoving();
    }


    @Override
    public PositionedBitmap move() {

        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite();
//        try {
//            awaitForMovement();
//        } catch (InterruptedException e) {
//            //
//        }

        //check borders and recalculate
//        if (lastX <= 0) {
//            lastX = 0;
//        } else if (lastX >= borderX) {
//            lastX = borderX;
//        }
//
//        if(lastY <= 0) {
//            lastY = 0;
//        } else if( lastY >= borderY) {
//            lastY = borderY;
//        }

        lastX += velocity.intensity * (velocity.direction.coeficientX * 0.01f);
        lastY += velocity.intensity * (velocity.direction.coeficientY * 0.01f);


        ret.positionX = lastX;
        ret.positionY = lastY;

        return ret;
    }

    private volatile boolean exploading;

    public Bullet setBorders(float x, float y) {
        this.borderX = x;
        this.borderY = y;
        return this;
    }

//    public boolean isExploading() {
//        return exploading;
//    }

    @Override
    public PositionedBitmap explode(Closure<PositionedBitmap> update) throws InterruptedException {
//        Handler handler = new Handler(Looper.getMainLooper());
//        PositionedImage updatePB = new PositionedImage();
//        exploading = true;
//
//        for (Bitmap bmp : explodeSprite) {
//
//            updatePB.image = bmp;
//            updatePB.positionX = lastX;
//            updatePB.positionY = lastY;
//            handler.post(new ReturnRunnable<>(update).setResult(updatePB));
//            Thread.sleep(25);
//        }
//
//        Thread.sleep(3000);
//        updatePB.image = explodeSprite.get(explodeSprite.size() - 1);
        return null;
    }
}

