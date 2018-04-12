package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.Iterator;
import java.util.List;

public class ImageTranslationMover implements ImageMover {

    protected List<Bitmap> sprite;
    protected Iterator<Bitmap> spriteIterator;
    protected float initVelocity = 20;

    protected volatile Velocity velocity;

    protected volatile float lastX;
    protected volatile float lastY;

    @Override
    public Pair<Float, Float> getLastCoordinates() {
        return Pair.create(lastX, lastY);
    }

    @Override
    public Velocity getVelocity() {
        return velocity;
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

    public ImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        this.sprite = sprite;
        this.initVelocity = velocity;
        this.velocity = new Velocity(velocity, new Direction(80, 20));
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
        this.velocity.direction = direction;
    }

    @Override
    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    @Override
    public void setTouchDirection(float x, float y) {

        float dX = x - lastX;
        float dY = y - lastY;

        float a;
        boolean signY = dY >= 0;
        boolean signX = dX >= 0;

        if (Math.abs(dY) >= Math.abs(dX)) {
            a = Math.abs((100*dX)/dY);
            float aY =  100 - a;
            velocity.direction = new Direction(signX ? a : - a, signY ? aY : - aY);
        } else {
            a = Math.abs((100*dY)/dX);
            float aX =  100 - a;
            velocity.direction = new Direction(signX ? aX : -aX, signY ? a : -a);
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
        this.velocity.intensity = velocity;
    }

    @Override
    public void checkCollisionAndBounce(
            Pair<Float, Float> colliderCoordinates,
            Velocity velocity
    ) {}

    @Override
    public PositionedBitmap move() {

        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite();

        //check borders and recalculate
        if (lastX <= 0) {
            velocity.direction.coeficientX = - velocity.direction.coeficientX;
            lastX = 0;
        } else if (lastX >= borderX) {
            velocity.direction.coeficientX = - velocity.direction.coeficientX;
            lastX = borderX;
        }

        if(lastY <= 0) {
            velocity.direction.coeficientY = - velocity.direction.coeficientY;
            lastY = 0;
        } else if( lastY >= borderY) {
            velocity.direction.coeficientY = - velocity.direction.coeficientY;
            lastY = borderY;
        }

        if (!paused) {
            lastX += velocity.intensity * (velocity.direction.coeficientX * 0.01f);
            lastY += velocity.intensity * (velocity.direction.coeficientY * 0.01f);
        }

        ret.positionX = lastX;
        ret.positionY = lastY;

        return ret;
    }
}

