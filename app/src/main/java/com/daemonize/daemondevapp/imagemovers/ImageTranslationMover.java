package com.daemonize.daemondevapp.imagemovers;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.daemondevapp.imagemovers.spriteiterators.SpriteIterator;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;


public class ImageTranslationMover implements ImageMover, SpriteIterator {

    protected volatile float lastX;
    protected volatile float lastY;

    protected SpriteIterator spriteIterator;
    protected float initVelocity;

    protected volatile Velocity velocity;

    protected static DaemonCountingSemaphore pauseSemaphore = new DaemonCountingSemaphore();


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

    protected float borderX1;
    protected float borderX2;

    protected float borderY1;
    protected float borderY2;

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

        double dX = x - lastX;
        double dY = y - lastY;
        double hypotenuse = Math.sqrt(dX*dX + dY*dY);

        dX = (dX / hypotenuse);
        dY = (dY / hypotenuse);

        velocity.intensity = velocityInt;
        velocity.direction.coeficientX = (float) dX;
        velocity.direction.coeficientY = (float) dY;

    }

    @Override
    public ImageTranslationMover setBorders(float x1, float x2, float y1, float y2) {
        this.borderX1 = x1;
        this.borderX2 = x2;
        this.borderY1 = y1;
        this.borderY2 = y2;
        return this;
    }

    @Override
    public void setVelocity(float velocity) {
        this.velocity.intensity = velocity;
    }

    public void pause(){
        pauseSemaphore.subscribe();
    }

    public void cont(){
        pauseSemaphore.unsubscribe();
    }


    private PositionedImage ret = new PositionedImage();

    @Override
    public PositionedImage animate() {

        try {

            pauseSemaphore.await();
            ret.image = iterateSprite();

            //check borders and recalculate
            if (lastX <= borderX1) {
                lastX = borderX1;
            } else if (lastX >= borderX2) {
                lastX = borderX2;
            }

            if(lastY <= borderY1) {
                lastY = borderY1;
            } else if( lastY >= borderY2) {
                lastY = borderY2;
            }

            ret.positionX = lastX += velocity.intensity * (velocity.direction.coeficientX);
            ret.positionY = lastY += velocity.intensity * (velocity.direction.coeficientY);

            return ret;

        } catch (InterruptedException e) {
            return null;
        }

    }
}

