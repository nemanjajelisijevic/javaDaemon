package com.daemonize.daemondevapp;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;
import com.daemonize.daemondevapp.imagemovers.RotatingSpriteImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.scene.view.ImageView;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Daemonize(doubleDaemonize = true, className = "BulletDoubleDaemon", returnDaemonInstance = true)
public class Bullet extends CoordinatedImageTranslationMover {

    private ImageView view;
    private ImageView view2;
    private ImageView view3;

    private int level = 1;
    private volatile int damage = 2;

    private RotatingSpriteImageMover rotationMover;

    public Bullet(Image [] sprite, float velocity, Pair<Float, Float> startingPos, int damage) {
        super(Arrays.copyOf(sprite, 1), velocity, startingPos);
        this.damage = damage;
        this.rotationMover = new RotatingSpriteImageMover(sprite, velocity, startingPos);
    }

    @CallingThread
    @Override
    public ImageTranslationMover setSprite(Image[] sprite) {
        rotationMover.setRotationSprite(sprite); //TODO fixx!!!
        return super.setSprite(sprite);
    }

    @CallingThread
    @Override
    public void setCoordinates(float lastX, float lastY) {
        super.setCoordinates(lastX, lastY);
    }

    @CallingThread
    @Override
    public void setVelocity(Velocity velocity) {
        super.setVelocity(velocity);
    }

    @CallingThread
    @Override
    public void setVelocity(float velocity) {
        super.setVelocity(velocity);
    }

    @CallingThread
    @Override
    public Velocity getVelocity() {
        return super.getVelocity();
    }

    @CallingThread
    @Override
    public Pair<Float, Float> getLastCoordinates() {
        return super.getLastCoordinates();
    }

    @CallingThread
    public void setCurrentAngle(int angle) {
        rotationMover.setCurrentAngle(angle);
    }

    @CallingThread
    public int getDamage() {
        return damage;
    }

    @CallingThread
    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Bullet setView(ImageView view) {
        this.view = view;
        return this;
    }

    public Bullet setView2(ImageView view2) {
        this.view2 = view2;
        return this;
    }


    @CallingThread
    public List<ImageView> getViews() {
        List<ImageView> lst = new ArrayList<>(level);
        switch (level){
            case 1 :
                lst.add(view);
                break;
            case 2:
                lst.add(view);
                lst.add(view2);
                break;
            case 3:
                lst.add(view);
                lst.add(view2);
                lst.add(view3);
                break;
        }
        return lst;
    }

    @CallingThread
    public Bullet setLevel(int level) {
        if (level >=1 &&  level <= 3){
            this.level = level;
        }
        return this;
    }

    @CallingThread
    public Bullet setView3(ImageView view3) {
        this.view3 = view3;
        return this;
    }

    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @Override
    public boolean pushSprite(Image [] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        return rotationMover.pushSprite(sprite, velocity);
    }

    public boolean rotate(int angle) throws InterruptedException {
//        rotationMover.setCurrentAngle(currentAngle);
        rotationMover.rotate(angle);
        return true;
    }

    public boolean rotateAndGoTo(int angle, float x, float y, float velocityInt) throws InterruptedException {
        setVelocity(0);
        rotationMover.rotate(angle);
        return goTo(x, y, velocityInt);
    }

    @CallingThread
    @Override
    public void pause() {
        super.pause();
    }

    @CallingThread
    @Override
    public void cont() {
        super.cont();
    }


    private Consumer consumer;
    private Runnable outOfBordersClosure;

    @CallingThread
    public Bullet setOutOfBordersConsumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    @CallingThread
    public Bullet setOutOfBordersClosure(Runnable outOfBordersClosure) {
        this.outOfBordersClosure = outOfBordersClosure;
        return this;
    }

    @Override
    public Image iterateSprite() {
        return rotationMover.iterateSprite();
    }

    @Override
    public PositionedImage animate() throws InterruptedException {
        return super.animate();
    }

    @SideQuest(SLEEP = 25)
    public GenericNode<Pair<PositionedImage, ImageView>> animateBullet() throws InterruptedException {

        if (lastX <= (borderX1 + velocity.intensity) ||
                lastX >= (borderX2 - velocity.intensity)||
                lastY <= (borderY1 + velocity.intensity) ||
                lastY >= (borderY2 - velocity.intensity)) {
            consumer.consume(outOfBordersClosure);
        }

        PositionedImage posImage = super.animate();
        Direction movingDirection = getVelocity().direction;

        switch (level){
            case 1:
                return  new GenericNode<>(Pair.create(posImage, view));
            case 2:
                return calculateOffsetImage(posImage,movingDirection,40);
            case 3:
                GenericNode<Pair<PositionedImage,ImageView>> root = calculateOffsetImage(posImage,movingDirection,40);
                root.addChild(new GenericNode<>(Pair.create(posImage,view3)));
                return root;
            default:
                return null;
        }

    }

    private GenericNode<Pair<PositionedImage, ImageView>> calculateOffsetImage(PositionedImage posImage, Direction movingDirection, int spaceBetweenBullet){

        double directionAngle = RotatingSpriteImageMover.getAngle(0, 0, movingDirection.coeficientX, movingDirection.coeficientY);

        double ortAngle1;
        double ortAngle2;

        //-90 deg
        ortAngle1 = directionAngle >= 90 && directionAngle <= 360 ? directionAngle - 90 : 360 + (directionAngle - 90);
        Direction offsetDirPosImage = new Direction((float)Math.cos(ortAngle1), (float)(-Math.sin(ortAngle1)));

        //+90 deg
        ortAngle2 = directionAngle >= 0 && directionAngle <= 270 ? directionAngle + 90 : (directionAngle + 90) - 360;
        Direction offsetDirPosImage2 = new Direction((float)Math.cos(ortAngle2), (float)(-Math.sin(ortAngle2)));

        PositionedImage posImage1 = posImage.clone();
        PositionedImage posImage2 = posImage.clone();

        posImage1.positionX = posImage.positionX + spaceBetweenBullet*(offsetDirPosImage.coeficientX);
        posImage1.positionY = posImage.positionY + spaceBetweenBullet*(offsetDirPosImage.coeficientY);

        posImage2.positionX = posImage.positionX + spaceBetweenBullet*(offsetDirPosImage2.coeficientX);
        posImage2.positionY = posImage.positionY + spaceBetweenBullet*(offsetDirPosImage2.coeficientY);

        GenericNode<Pair<PositionedImage, ImageView>> root = new GenericNode<>(Pair.create(posImage1, view));
//        GenericNode<PositionedImage,ImageView> ret = new GenericNode<PositionedImage>(posImage1,view);
        root.addChild(new GenericNode<>(Pair.create(posImage2,view2)));
       return root;

    }
}
