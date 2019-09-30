package com.daemonize.game;


import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.ImageTranslationMover;
import com.daemonize.imagemovers.RotatingSpriteImageMover;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Daemon(
        doubleDaemonize = true,
        daemonizeBaseMethods = true,
        className = "BulletDoubleDaemon"
)
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

    @Override
    public ImageTranslationMover setSprite(Image[] sprite) {
        rotationMover.setRotationSprite(sprite); //TODO fixx!!!
        return super.setSprite(sprite);
    }

    @Override
    public void setVelocity(Velocity velocity) {
        super.setVelocity(velocity);
    }

    @Override
    public void setVelocity(float velocity) {
        super.setVelocity(velocity);
    }

    public void setCurrentAngle(int angle) {
        rotationMover.setCurrentAngle(angle);
    }

    public int getDamage() {
        return damage;
    }

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

    public Bullet setView3(ImageView view3) {
        this.view3 = view3;
        return this;
    }

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

    public void setLevel(int level) {
        if (level >=1 &&  level <= 3) {
            this.level = level;
        }
    }

    @GenerateRunnable
    @Daemonize
    @Override
    public void pushSprite(Image [] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        rotationMover.pushSprite(sprite, velocity);
    }

    @Override
    public void popSprite() {
        rotationMover.popSprite();
    }

    @GenerateRunnable
    @Daemonize
    public void rotate(int angle) throws InterruptedException {
        rotationMover.rotate(angle);
    }

    @Daemonize
    public boolean rotateAndGoTo(int angle, float x, float y, float velocityInt) throws InterruptedException {
        clearVelocity();
        rotationMover.rotate(angle);
        return goTo(x, y, velocityInt);
    }

    @Override
    public Image iterateSprite() {
        return rotationMover.iterateSprite();
    }

    //direction cache
    private Direction lastDirection = new Direction(0,0);
    private Direction lastOffsetDirection = new Direction(0,0);
    private Direction lastOffset2Direction = new Direction(0,0);

    //generic node cache
    private GenericNode<Pair<PositionedImage, ImageView>> bullet = new GenericNode<>(Pair.create(new PositionedImage(), null));
    private GenericNode<Pair<PositionedImage, ImageView>> bullet1 = new GenericNode<>(Pair.create(new PositionedImage(), null));
    private GenericNode<Pair<PositionedImage, ImageView>> bullet2 = new GenericNode<>(Pair.create(new PositionedImage(), null));

    {
        bullet1.addChild(bullet2);
        bullet.addChild(bullet1);
    }

    @SideQuest(SLEEP = 25)
    public GenericNode<Pair<PositionedImage, ImageView>> animateBullet() throws InterruptedException {

        PositionedImage posImage = super.animate();

        if(posImage == null)
            return null;

        Direction movingDirection = getVelocity().direction;

        switch (level) {
            case 1:
                bullet2.getValue().setFirst(posImage).setSecond(view);
                return bullet2;
            case 2:
                return calculateOffsetImage(posImage, movingDirection, spaceBetweenBullets);
            case 3:
                bullet.getValue().setFirst(posImage).setSecond(view3);
                calculateOffsetImage(posImage, movingDirection, spaceBetweenBullets);
                return bullet;
            default:
                return null;
        }

    }

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
            offsetDirPosImage2 = new Direction( -offsetDirPosImage.coeficientX, -offsetDirPosImage.coeficientY);

            lastDirection = movingDirection;
            lastOffsetDirection = offsetDirPosImage;
            lastOffset2Direction = offsetDirPosImage2;
        }

        posImage1.positionX = posImage.positionX + spaceBetweenBullet * (offsetDirPosImage.coeficientX);
        posImage1.positionY = posImage.positionY + spaceBetweenBullet * (offsetDirPosImage.coeficientY);

        posImage2.positionX = posImage.positionX + spaceBetweenBullet * (offsetDirPosImage2.coeficientX);
        posImage2.positionY = posImage.positionY + spaceBetweenBullet * (offsetDirPosImage2.coeficientY);

        bullet1.getValue().setFirst(posImage1).setSecond(view);
        bullet2.getValue().setFirst(posImage2).setSecond(view2);
        return bullet1;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

