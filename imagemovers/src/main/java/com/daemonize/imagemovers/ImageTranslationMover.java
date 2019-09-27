package com.daemonize.imagemovers;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.imagemovers.spriteiterators.SpriteIterator;
import com.daemonize.graphics2d.images.Image;


public class ImageTranslationMover implements ImageMover, SpriteIterator {

    private volatile float lastX;
    private volatile float lastY;

    private double dX = 0;
    private double dY = 0;

    private float dXY;

    protected float borderX1;
    protected float borderX2;

    protected float borderY1;
    protected float borderY2;

    protected SpriteIterator spriteIterator;
    protected float initVelocity;
    protected volatile Velocity velocity;

    private Consumer outOfBordersConsumer;
    private Runnable outOfBordersClosure;
    private PositionedImage ret = new PositionedImage();

    protected DaemonCountingSemaphore animateSemaphore = new DaemonCountingSemaphore().setName("Animate Semaphore");
    private DaemonSemaphore pauseSemaphore = new DaemonSemaphore().setName("Global pause semaphore");

    public ImageTranslationMover(Image[] sprite, float velocity, Pair<Float, Float> startingPos, float dXY) {
        this.spriteIterator = new BasicSpriteIterator(sprite);
        this.initVelocity = velocity;
        this.velocity = new Velocity(velocity, new Direction(0, 0));
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

    @Override
    public Image iterateSprite() {
        Image ret = spriteIterator.iterateSprite();

        if (ret == null)
            throw new IllegalStateException("Sprite image can not be null!");

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
        this.velocity.intensity = velocity;
    }

    @Override
    public boolean setDirectionToPoint(float x, float y) {
        velocity.direction.coeficientX = 0;
        velocity.direction.coeficientY = 0;

        if (x - lastX == 0 || y - lastY == 0)
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

    @Override
    public boolean setDirectionAndMove(float x, float y, float velocityInt) {
        boolean ret = false;
        velocity.intensity = 0;
        ret = setDirectionToPoint(x, y);
        velocity.intensity = velocityInt;
        return ret;
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

    public void pause(){
        pauseSemaphore.stop();
    }

    public void cont(){
        pauseSemaphore.go();
    }

    public void setOutOfBordersConsumer(Consumer consumer) {
        this.outOfBordersConsumer = consumer;
    }

    public void setOutOfBordersClosure(Runnable closure) {
        this.outOfBordersClosure = closure;
    }

    @Override
    public PositionedImage animate() throws InterruptedException {

        pauseSemaphore.await();
        animateSemaphore.await();

        ret.image = iterateSprite();

        synchronized (this) {
            ret.positionX = lastX += velocity.intensity * (velocity.direction.coeficientX * dXY);
            ret.positionY = lastY += velocity.intensity * (velocity.direction.coeficientY * dXY);
        }

        return ret;
    }
}

