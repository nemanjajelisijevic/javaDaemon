package com.daemonize.daemondevapp.proba;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.daemonize.daemondevapp.imagemovers.ImageMover;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.tabel.Field;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;

import java.util.Iterator;
import java.util.List;

public class Enemy implements ImageMoverM {

    protected int hp = 1;

    protected List<Bitmap> sprite;
    protected List<Bitmap> explodeSprite;
    protected Iterator<Bitmap> spriteIterator;

    protected volatile Velocity velocity;
    protected float initVelocity = 20;

    protected volatile float lastX;
    protected volatile float lastY;

    protected float borderX;
    protected float borderY;

    List<Field> path;
    Field currentField, nextField ;
    int currentIndexField, nextIndexField ;

    private ImageView view;

    public Enemy(int hp, List<Bitmap> sprite, List<Bitmap> explodeSprite, Velocity velocity,  Pair<Float, Float> startingPos, List<Field> path) {
        this.hp = hp;
        this.sprite = sprite;
        this.explodeSprite = explodeSprite;
        this.velocity = velocity;//
        // new Velocity(velocity, new Direction(80, 20));
        lastX = startingPos.first;
        lastY = startingPos.second;
        spriteIterator = sprite.iterator();
        this.path = path;
        currentField = path.get(0);
        currentIndexField = -1;
        nextField = path.get(0);
        setDirectionAndMove(currentField.getCenterX(),currentField.getCenterY(),velocity.intensity);
    }

    public ImageView getView() {
        return view;
    }

    public Enemy setView(ImageView view) {
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
//        }
        //            float aY =  100 - a;
//        } else if (Math.abs(dY) > Math.abs(dX)) {
//            a = Math.abs((100*dX)/dY);
//            b = Math.abs(dY/dX);
//            float aY =  100 - a;
//            velocity.direction = new Direction(signX ? a : - a, signY ? b : - b);
//        } else {
//            a = Math.abs((100*dY)/dX);
//            b = Math.abs(dX/dY);
//            float aX =  100 - a;
//            velocity.direction = new Direction(signX ? b : - b, signY ? a : -a);
//        }

        //startMoving();
    }


    @Override
    public void setVelocity(float velocity) {
        this.velocity.intensity = velocity;
//        startMoving();
    }

    @Override
    public Enemy setBorders(float x, float y) {
        this.borderX = x;
        this.borderY = y;
        return this;
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

        Log.w("Kordinate ","X: "+lastX+", Y "+lastY+";");
//        Log.w("Kordinate ","Y "+lastY);
        float r = velocity.intensity;
        if ( lastX < (nextField.getCenterX() + r) && lastX > (nextField.getCenterX() - r ) &&
                lastY < (nextField.getCenterY() + r) && lastY > (nextField.getCenterY() - r ) ) {
            currentField = nextField;
            currentIndexField ++;
            if ( path.size() > currentIndexField + 1) {
                nextField = path.get(currentIndexField + 1);
            }
            setDirectionAndMove(nextField.getCenterX(),nextField.getCenterY(),velocity.intensity);
        }
//
//        lastX += velocity.intensity * (velocity.direction.coeficientX *0.001f);
//        lastY += velocity.intensity * (velocity.direction.coeficientY*0.001f );

        lastX += (velocity.direction.coeficientX * (velocity.intensity / 100));
        lastY += (velocity.direction.coeficientY * (velocity.intensity / 100));

//        float dx = velocity.intensity * (velocity.direction.coeficientX );
//        lastX += dx ;
//        lastY += dx * (velocity.direction.coeficientY );


        ret.positionX = lastX;
        ret.positionY = lastY;

        return ret;
    }

    private volatile boolean exploading;

    public boolean isExploading() {
        return exploading;
    }

    @Override
    public PositionedBitmap explode(Closure<PositionedBitmap> update) throws InterruptedException {

        Handler handler = new Handler(Looper.getMainLooper());
        PositionedBitmap updatePB = new PositionedBitmap();
        exploading = true;

        for (Bitmap bmp : explodeSprite) {

            updatePB.image = bmp;
            updatePB.positionX = lastX;
            updatePB.positionY = lastY;
            handler.post(new ReturnRunnable<>(update).setResult(updatePB));
            Thread.sleep(25);
        }

        Thread.sleep(3000);
        updatePB.image = explodeSprite.get(explodeSprite.size() - 1);
        return updatePB;
    }
}

