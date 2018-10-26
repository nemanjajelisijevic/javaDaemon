package com.daemonize.daemondevapp.imagemovers;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.daemondevapp.imagemovers.spriteiterators.SpriteIterator;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;


public class ImageTranslationMover implements ImageMover, SpriteIterator {

    private SpriteIterator spriteIterator;
    protected float initVelocity;

    protected volatile Velocity velocity;

    protected static DaemonCountingSemaphore semaphore = new DaemonCountingSemaphore();


    public Image [] getSprite() {
        return spriteIterator.getSprite();
    }

    @Override
    public int getSize() {
        return spriteIterator.getSize();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImageTranslationMover setSprite(Image[] sprite) { //TODO That's how it must be
        spriteIterator.setSprite(sprite);
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

    public ImageTranslationMover(Image[] sprite, float velocity, Pair<Float, Float> startingPos) {
        this.spriteIterator = new BasicSpriteIterator(sprite);
        this.initVelocity = velocity;
        this.velocity = new Velocity(velocity, new Direction(0, 0));
        lastX = startingPos.getFirst();
        lastY = startingPos.getSecond();
    }

    public Image iterateSprite() {
        return spriteIterator.iterateSprite();
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
    public void setDirectionAndMove(float x, float y, float velocityInt) {

        float dX = x - lastX;
        float dY = y - lastY;

//        float a;
//        boolean signY = dY >= 0;
//        boolean signX = dX >= 0;
        velocity.intensity = velocityInt;
        setDirection(new ImageMover.Direction(dX, dY));
        //velocity.direction = new ImageMover.Direction(dX, dY); //TODO check this shit
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


    public void pause(){
        semaphore.subscribe();
    }

    public void cont(){
        semaphore.unsubscribe();
    }

    @Override
    public PositionedImage animate() {

        try {

            semaphore.await();


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

        } catch (InterruptedException e) {
                return null;
        }

    }
}

