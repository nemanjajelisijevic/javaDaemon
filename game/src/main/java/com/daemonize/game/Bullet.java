package com.daemonize.game;


import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.game.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.game.imagemovers.ImageTranslationMover;
import com.daemonize.game.imagemovers.RotatingSpriteImageMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.view.ImageView;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@Daemonize(doubleDaemonize = true, className = "BulletDoubleDaemon")
public class Bullet extends CoordinatedImageTranslationMover {

    private ImageView view;
    private ImageView view2;
    private ImageView view3;

    private int level = 1;
    private volatile int damage;
    private int spaceBetweenBullets;

    private RotatingSpriteImageMover rotationMover;

    public Bullet(Image [] sprite, float velocity, Pair<Float, Float> startingPos, int damage, int spaceBetweenBullets, float dXY) {
        super(Arrays.copyOf(sprite, 1), velocity, startingPos, dXY);
        this.damage = damage;
        this.spaceBetweenBullets = spaceBetweenBullets;
        this.rotationMover = new RotatingSpriteImageMover(sprite, animateSemaphore, velocity, startingPos, dXY);
    }

    @CallingThread
    @Override
    public ImageTranslationMover setSprite(Image[] sprite) {
        rotationMover.setRotationSprite(sprite); //TODO fixx!!!
        return super.setSprite(sprite);
    }

    @CallingThread
    @Override
    public void clearVelocity() {
        super.clearVelocity();
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
    @Override
    public Pair<Float, Float> getTargetCoordinates() {
        return super.getTargetCoordinates();
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
    public Bullet setView3(ImageView view3) {
        this.view3 = view3;
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
    public void setLevel(int level) {
        if (level >=1 &&  level <= 3) {
            this.level = level;
        }
    }

    @GenerateRunnable
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @GenerateRunnable
    @Override
    public void pushSprite(Image [] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        rotationMover.pushSprite(sprite, velocity);
    }

    @CallingThread
    @Override
    public void popSprite() {
        rotationMover.popSprite();
    }

    @GenerateRunnable
    public void rotate(int angle) throws InterruptedException {
        rotationMover.rotate(angle);
    }

    public boolean rotateAndGoTo(int angle, float x, float y, float velocityInt) throws InterruptedException {
        clearVelocity();
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

        Pair<Float, Float> lastCoord = getLastCoordinates();

        PositionedImage posImage = super.animate();

        if(posImage == null)
            return null;

        Direction movingDirection = getVelocity().direction;

        switch (level){
            case 1:
                return new GenericNode<>(Pair.create(posImage, view));
            case 2:
                return calculateOffsetImage(posImage, movingDirection, spaceBetweenBullets);
            case 3:
                GenericNode<Pair<PositionedImage,ImageView>> root = calculateOffsetImage(posImage, movingDirection, spaceBetweenBullets);
                root.addChild(new GenericNode<>(Pair.create(posImage, view3)));
                return root;
            default:
                return null;
        }

    }

    //direction cache
    private Direction lastDirection = new Direction(0,0);
    private Direction lastOffsetDirection = new Direction(0,0);
    private Direction lastOffset2Direction = new Direction(0,0);

    private GenericNode<Pair<PositionedImage, ImageView>> calculateOffsetImage(PositionedImage posImage, Direction movingDirection, int spaceBetweenBullet){

        PositionedImage posImage1 = posImage.clone();
        PositionedImage posImage2 = posImage.clone();

        Direction offsetDirPosImage;
        Direction offsetDirPosImage2;

        if (movingDirection.coeficientX == lastDirection.coeficientX && movingDirection.coeficientY == lastDirection.coeficientY) {

            offsetDirPosImage = lastOffsetDirection;
            offsetDirPosImage2 = lastOffset2Direction;

        } else {

            double directionAngle = RotatingSpriteImageMover.getAngle(0, 0, movingDirection.coeficientX, movingDirection.coeficientY);

            double ortAngle1;
            double ortAngle2;

            //-90 deg
            ortAngle1 = directionAngle >= 90 && directionAngle <= 360 ? directionAngle - 90 : 360 + (directionAngle - 90);
            offsetDirPosImage = new Direction((float) Math.cos(ortAngle1), (float) (-Math.sin(ortAngle1)));

            //+90 deg
            offsetDirPosImage2 = new Direction( - offsetDirPosImage.coeficientX, - offsetDirPosImage.coeficientY);

            lastDirection = movingDirection;
            lastOffsetDirection = offsetDirPosImage;
            lastOffset2Direction = offsetDirPosImage2;

        }

        posImage1.positionX = posImage.positionX + spaceBetweenBullet * (offsetDirPosImage.coeficientX);
        posImage1.positionY = posImage.positionY + spaceBetweenBullet * (offsetDirPosImage.coeficientY);

        posImage2.positionX = posImage.positionX + spaceBetweenBullet * (offsetDirPosImage2.coeficientX);
        posImage2.positionY = posImage.positionY + spaceBetweenBullet * (offsetDirPosImage2.coeficientY);


        GenericNode<Pair<PositionedImage, ImageView>> root = new GenericNode<>(Pair.create(posImage1, view));
        root.addChild(new GenericNode<>(Pair.create(posImage2,view2)));
        return root;

    }

    @CallingThread
    @Override
    public void setOutOfBordersConsumer(Consumer consumer) {
        super.setOutOfBordersConsumer(consumer);
    }

    @CallingThread
    @Override
    public void setOutOfBordersClosure(Runnable outOfBordersClosure) {
        super.setOutOfBordersClosure(outOfBordersClosure);
    }
}
