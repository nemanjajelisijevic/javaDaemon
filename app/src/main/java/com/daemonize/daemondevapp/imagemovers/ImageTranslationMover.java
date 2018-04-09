package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.Iterator;
import java.util.List;

public class ImageTranslationMover implements ImageMover {

    protected List<Bitmap> sprite;
    protected Iterator<Bitmap> spriteIterator;
    protected  float initVelocity = 20;
    //protected float velocity = initVelocity;

    protected Momentum momentum;

    protected float lastX;
    protected float lastY;

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

//    @Override
//    public void updatePosition(int id, PositionUpdate update) {}

    private volatile boolean paused = false;

    public void pause(){
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    protected float borderX;
    protected float borderY;

    //protected volatile float currentDirX = 80;
    //protected volatile float currentDirY = 20;

    public ImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        this.sprite = sprite;
        this.initVelocity = velocity;
        //this.velocity = initVelocity;

        this.momentum = new Momentum();
        this.momentum.velocity = velocity;
        this.momentum.direction = new Direction(80, 20);

        lastX = startingPos.first;
        lastY = startingPos.second;
        spriteIterator = sprite.iterator();
    }

    protected Bitmap iterateSprite() {
        if(!spriteIterator.hasNext()) {
            spriteIterator = sprite.iterator();
        }
        return spriteIterator.next();
    }

    @Override
    public void setDirection(Direction direction) {
        this.momentum.direction = direction;

        //this.currentDirX = direction.coeficientX;
        //this.currentDirY = direction.coeficientY;
    }

    @Override
    public void setMomentum(Momentum momentum) {
        this.momentum = momentum;
    }

    @Override
    public void setTouchDirection(float x, float y) {

        float diffX = x - lastX;
        float diffY = y - lastY;

        float a;
        boolean signY = diffY >= 0;
        boolean signX = diffX >= 0;

        if (Math.abs(diffY) >= Math.abs(diffX)) {
            a = Math.abs((100*diffX)/diffY);
            float aY =  100 - a;
            momentum.direction = new Direction(signX ? a : - a, signY ? aY : - aY);
        } else {
            a = Math.abs((100*diffY)/diffX);
            float aX =  100 - a;
            momentum.direction = new Direction(signX ? aX : -aX, signY ? a : -a);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ImageTranslationMover setBorders(float x, float y) {
        this.borderX = x;
        this.borderY = y;
        return this;
    }

    @Override
    public void setVelocity(float velocity) {
        this.momentum.velocity = velocity;
    }

    @Override
    public void checkCollisionAndBounce(
            Pair<Float, Float> colliderCoordinates,
            Momentum momentum
    ) {}

    @Override
    public PositionedBitmap move() {

        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite();

        //check borders and recalculate
        if (lastX <= 0) {
            momentum.direction.coeficientX = - momentum.direction.coeficientX;
            lastX = 0;
        } else if (lastX >= borderX) {
            momentum.direction.coeficientX = - momentum.direction.coeficientX;
            lastX = borderX;
        }

        if(lastY <= 0) {
            momentum.direction.coeficientY = - momentum.direction.coeficientY;
            lastY = 0;
        } else if( lastY >= borderY) {
            momentum.direction.coeficientY = - momentum.direction.coeficientY;
            lastY = borderY;
        }

        if (!paused) {
            lastX += momentum.velocity * (momentum.direction.coeficientX * 0.01f);
            lastY += momentum.velocity * (momentum.direction.coeficientY * 0.01f);
        }

        ret.positionX = lastX;
        ret.positionY = lastY;

        return ret;
    }
}

