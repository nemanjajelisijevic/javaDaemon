package com.daemonize.imagemovers;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.imagemovers.spriteiterators.SpriteIterator;
import com.daemonize.graphics2d.images.Image;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ImageTranslationMover implements ImageMover, SpriteIterator {

    private volatile float lastX;
    private volatile float lastY;

    private Pair<Float, Float> lastCoords = Pair.create(lastX, lastY);

    private Lock coordUpdateLock = new ReentrantLock();


    private double dX = 0;
    private double dY = 0;

    private final Float dXY;

    protected SpriteIterator spriteIterator;

    public ImageTranslationMover setSpriteIterator(SpriteIterator spriteIterator) {
        this.spriteIterator = spriteIterator;
        return this;
    }

    protected volatile Velocity velocity;

    private PositionedImage ret = new PositionedImage();

    protected DaemonCountingSemaphore animateSemaphore = new DaemonCountingSemaphore().setName("Animate Semaphore");

    private AnimationWaiter animationSemaphoreWaiterWrapper = new AnimationWaiter() {
        @Override
        public void await() throws InterruptedException {
            animateSemaphore.await();
        }
    };

    @Override
    public AnimationWaiter getAnimationWaiter() {
        return animationSemaphoreWaiterWrapper;
    }

    public ImageTranslationMover(Image[] sprite, Pair<Float, Float> startingPos, float dXY) {
        this.spriteIterator = new BasicSpriteIterator(sprite);
        this.velocity = new Velocity(0, new Direction(0, 0));
        this.dXY = dXY;
        lastX = startingPos.getFirst();
        lastY = startingPos.getSecond();
        animateSemaphore.stop();
    }

    public float getdXY() {
        return dXY;
    }

    public void clearVelocity() {
        velocity.intensity = 0;
        velocity.direction.coeficientX = 0;
        velocity.direction.coeficientY = 0;
    }

    @Override
    public Image [] getSprite() {
        return spriteIterator.getSprite();
    }

    @Override
    public int getSize() {
        return spriteIterator.getSize();
    }

    @Override
    public ImageTranslationMover setSprite(Image[] sprite) {
        spriteIterator.setSprite(sprite);
        return this;
    }

    @Override
    public Pair<Float, Float> getLastCoordinates() {
        coordUpdateLock.lock();
        lastCoords.setFirst(lastX).setSecond(lastY);
        coordUpdateLock.unlock();
        return lastCoords;
    }

    @Override
    public Velocity getVelocity() {
        return velocity;
    }

    @Override
    public void setCoordinates(float lastX, float lastY) {
        this.coordUpdateLock.lock();
        this.lastX = lastX;
        this.lastY = lastY;
        this.coordUpdateLock.unlock();
    }

    @Override
    public Image iterateSprite() {
        Image ret = spriteIterator.iterateSprite();
//
//        if (ret == null)
//            throw new IllegalStateException("Sprite image can not be null!");

        return ret;
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
    public void setVelocity(float velocity) {

        if (velocity > 0) {

            if (this.getVelocity().intensity <= 0) {
                animateSemaphore.subscribe();
            }

        } else {
            velocity = 0;
            if (this.getVelocity().intensity > 0)
                animateSemaphore.unsubscribe();

        }


//        if (getVelocity().intensity  < 0.1 && velocity > 0.1)
//            animateSemaphore.subscribe();
//        else if (getVelocity().intensity  > 0.1 && velocity < 0.1) {
//            velocity = 0;
//            animateSemaphore.unsubscribe();
//        }

        this.velocity.intensity = velocity;
    }

    @Override
    public boolean setDirectionToPoint(float x, float y) {
        velocity.direction.coeficientX = 0;
        velocity.direction.coeficientY = 0;

        if (x - lastX == 0 && y - lastY == 0)
            return false;

        dX = x - lastX;
        dY = y - lastY;

        double hypotenuse = Math.sqrt(dX*dX + dY*dY);

        dX = dX / hypotenuse;
        dY = dY / hypotenuse;

        velocity.direction.coeficientX = (float) dX;
        velocity.direction.coeficientY = (float) dY;

        return true;
    }

    protected void updateCoordinates() {
        coordUpdateLock.lock();
        ret.positionX = lastX += velocity.intensity * (velocity.direction.coeficientX * dXY);
        ret.positionY = lastY += velocity.intensity * (velocity.direction.coeficientY * dXY);
        coordUpdateLock.unlock();
    }

    @Override
    public PositionedImage animate() throws InterruptedException {

        animateSemaphore.await();
        ret.image = iterateSprite();
        updateCoordinates();

        return ret;
    }

    public static double absDistance(float x1, float y1, float x2, float y2) {
        float dX = x1 - x2;
        float dY = y1 - y2;
        return Math.sqrt(dX*dX + dY*dY);
    }

    public static double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
        return absDistance(source.getFirst(), source.getSecond(), dest.getFirst(), dest.getSecond());
    }
}

