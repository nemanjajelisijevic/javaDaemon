package com.daemonize.game.imagemovers;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingLatch;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.game.Pair;
import com.daemonize.game.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.game.imagemovers.spriteiterators.SpriteIterator;
import com.daemonize.game.images.Image;


public class ImageTranslationMover implements ImageMover, SpriteIterator {

    private volatile float lastX;
    private volatile float lastY;

    private double dX = 0;
    private double dY = 0;

    private float dXY;

    public float getdXY() {
        return dXY;
    }

    protected SpriteIterator spriteIterator;
    protected float initVelocity;

    protected volatile Velocity velocity;

    protected DaemonCountingSemaphore animateSemaphore = new DaemonCountingSemaphore();
    private DaemonSemaphore pauseSemaphore = new DaemonSemaphore();

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
        return Pair.create(lastX, lastY);
    }

    @Override
    public Velocity getVelocity() {
        return velocity;
    }

    @Override
    public synchronized void setCoordinates(float lastX, float lastY) {
        this.lastX = lastX;
        this.lastY = lastY;
    }

    protected float borderX1;
    protected float borderX2;

    protected float borderY1;
    protected float borderY2;

    public ImageTranslationMover(Image[] sprite, float velocity, Pair<Float, Float> startingPos, float dXY) {
        this.spriteIterator = new BasicSpriteIterator(sprite);
        this.initVelocity = velocity;
        this.velocity = new Velocity(velocity, new Direction(0, 0));
        this.dXY = dXY;
        lastX = startingPos.getFirst();
        lastY = startingPos.getSecond();
        animateSemaphore.stop();
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
    public boolean setDirectionAndMove(float x, float y, float velocityInt) {

        if (x - lastX == 0 || y - lastY == 0)
            return false;

        synchronized (this) {
            dX = x - lastX;
            dY = y - lastY;
        }

        double hypotenuse = Math.sqrt(dX*dX + dY*dY);

        dX = dX / hypotenuse;
        dY = dY / hypotenuse;

        if (Double.valueOf(dX).isNaN() || Double.valueOf(dY).isNaN()) {//TODO DEBUG
            throw new IllegalStateException("SET DIRECTION COORDINATES NaN Value! INPUT X: " + x + ", Y: " + y + ", LAST X: " + lastX + ", LAST Y: " + lastY);
        }

        velocity.intensity = velocityInt;
        velocity.direction.coeficientX = (float) dX;
        velocity.direction.coeficientY = (float) dY;

        return true;
    }

    @SuppressWarnings("unchecked")
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
        pauseSemaphore.stop();
    }

    public void cont(){
        pauseSemaphore.go();
    }

    private Consumer consumer;
    private Runnable outOfBordersClosure;

    public void setOutOfBordersConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public void setOutOfBordersClosure(Runnable closure) {
        this.outOfBordersClosure = closure;
    }

    private PositionedImage ret = new PositionedImage();

    @Override
    public PositionedImage animate() throws InterruptedException {

        pauseSemaphore.await();
        animateSemaphore.await();

        ret.image = iterateSprite();

        if (lastX <= (borderX1 + velocity.intensity) ||
                lastX >= (borderX2 - velocity.intensity)||
                lastY <= (borderY1 + velocity.intensity) ||
                lastY >= (borderY2 - velocity.intensity)) {
            consumer.consume(outOfBordersClosure);
            animateSemaphore.stop();
            return null;
        }

        synchronized (this) {
            ret.positionX = lastX += velocity.intensity * (velocity.direction.coeficientX * dXY);
            ret.positionY = lastY += velocity.intensity * (velocity.direction.coeficientY * dXY);
        }

        return ret;
    }
}

