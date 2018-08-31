package com.daemonize.daemondevapp.imagemovers;

import android.util.Log;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonUtils;


public class ImageTranslationMover implements ImageMover {

    private Image [] sprite;
    private int spriteSize;
    private int spriteIndex;
    protected float initVelocity;

    protected volatile Velocity velocity;

    public Image [] getSprite() {
        return sprite;
    }

    public ImageTranslationMover setImage(Image image) { //TODO That's how it must be
        this.sprite = new Image[1];
        this.spriteIndex = 0;
        this.spriteSize = 1;
        this.sprite[0] = image;
        return this;
    }

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
    public PositionedImage setLastCoordinates(float lastX, float lastY) {
        this.lastX = lastX;
        this.lastY = lastY;

        PositionedImage ret = new PositionedImage();
        ret.image = iterateSprite();

        ret.positionX = lastX;
        ret.positionY = lastY;
        
        return ret;
    }

    protected float borderX;
    protected float borderY;

    public ImageTranslationMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos) {
        this.sprite = sprite;
        this.initVelocity = velocity;
        this.velocity = new Velocity(velocity, new Direction(0, 0));
        lastX = startingPos.getFirst();
        lastY = startingPos.getSecond();
        spriteIndex = 0;
        spriteSize = sprite.length;

    }

    protected Image iterateSprite() {
        if(spriteIndex == spriteSize) {
            spriteIndex = 0;
        }
        return sprite[spriteIndex++];
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

        float dX = x - lastX;
        float dY = y - lastY;

//        float a;
//        boolean signY = dY >= 0;
//        boolean signX = dX >= 0;
        velocity.intensity = velocityInt;
        velocity.direction = new ImageMover.Direction(dX, dY); //TODO check this shit
//
//        if (Math.abs(dY) >= Math.abs(dX)) {
//            a = Math.abs((100*dX)/dY);
//            float aY =  100 - a;
//            velocity.direction = new Direction(signX ? a : - a, signY ? aY : - aY);
//        } else {
//            a = Math.abs((100*dY)/dX);
//            float aX =  100 - a;
//            velocity.direction = new Direction(signX ? aX : -aX, signY ? a : -a);
//        }

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
    public PositionedImage animate() {

        PositionedImage ret = new PositionedImage();
        ret.image = iterateSprite();

        //check borders and recalculate
        if (lastX <= 0) {
            lastX = 0;
        } else if (lastX >= borderX) {
            lastX = borderX;
        }

        if(lastY <= 0) {
            lastY = 0;
        } else if( lastY >= borderY) {
            lastY = borderY;
        }

        ret.positionX = lastX += velocity.intensity * (velocity.direction.coeficientX * 0.01f);
        ret.positionY = lastY += velocity.intensity * (velocity.direction.coeficientY * 0.01f);

        return ret;
    }
}

