package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.game.grid.Field;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.AngleToImageArray;
import com.daemonize.imagemovers.AngleToSingleImageArray;
import com.daemonize.imagemovers.AngleToSpriteArray;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.Movable;
import com.daemonize.imagemovers.RotatingSpriteImageMover;

@Daemon(doubleDaemonize = true, implementPrototypeInterfaces = true, daemonizeBaseMethods = true)
public class Zombie extends CoordinatedImageTranslationMover implements Mortal<Zombie>, Movable, Target<Zombie> {

    public Field<ShooterGame.FieldContent> currentField;
    public final float recommendedVelocity;

    private volatile int hpMax;
    private volatile int hp;
    private volatile boolean attackable = true;

    private PositionedImage[] ret = new PositionedImage[1];
    private RotatingSpriteImageMover rotationMover;

    private AngleToImageArray walkAnimation;
    private AngleToSpriteArray attackAnimation;

    public Zombie(Image startImage, AngleToImageArray walkAnimation, AngleToSpriteArray attackAnimation, float recommendedVelocity, Pair<Float, Float> startingPos, float dXY) {
        super(startImage, startingPos, dXY);
        this.rotationMover = new RotatingSpriteImageMover(walkAnimation, animateSemaphore, startImage, startingPos, dXY).setRotaterName("ZombieRotater");
        this.recommendedVelocity = recommendedVelocity;
        this.walkAnimation = walkAnimation;
        this.attackAnimation = attackAnimation;
    }

    public Field<ShooterGame.FieldContent> getCurrentField() {
        return currentField;
    }

    public Zombie setCurrentField(Field<ShooterGame.FieldContent> currentField) {
        this.currentField = currentField;
        return this;
    }

    @Override
    public boolean isAttackable() {
        return attackable;
    }

    @Override
    public Zombie setAttackable(boolean attackable) {
        this.attackable = attackable;
        return this;
    }

    @Daemonize
    public void sleep(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    @Daemonize
    @GenerateRunnable
    public void sleepAndRet(long ms) throws InterruptedException {
        this.sleep(ms);
    }

    @Daemonize
    @GenerateRunnable
    public void attack() throws InterruptedException {
        rotationMover.pushSprite(attackAnimation.getSpriteByAngle(walkAnimation.getCurrentAngle()));
    }

    @Daemonize
    @GenerateRunnable
    public void animateDirectionalSprite(AngleToSpriteArray animation) throws InterruptedException {
        rotationMover.pushSprite(animation.getSpriteByAngle(walkAnimation.getCurrentAngle()));
    }

    @Daemonize
    @GenerateRunnable
    @Override
    public void pushSprite(Image[] sprite) throws InterruptedException {
        rotationMover.pushSprite(sprite);
    }

    @Daemonize
    public void rotateTowards(float x, float y) throws InterruptedException {
        rotationMover.rotateTowards(getLastCoordinates().getFirst(), getLastCoordinates().getSecond(), x, y);
    }

    @Daemonize
    public void rotateTowards(Pair<Float, Float> coords) throws InterruptedException {
        rotateTowards(coords.getFirst(), coords.getSecond());
    }

    @Override
    public Zombie setHp(int hp) {
        if (hp <= hpMax)
            this.hp = hp;
        return this;
    }

    @Override
    public Zombie setMaxHp(int maxHp) {
        this.hpMax = maxHp;
        return this;
    }

    @Override
    public int getMaxHp() {
        return hpMax;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public Image iterateSprite() {
        return rotationMover.iterateSprite();
    }

    @SideQuest(SLEEP = 25, blockingClosure = true)
    public PositionedImage[] animateZombie() throws InterruptedException {
        ret[0] = super.animate();
        return ret;
    }
}
