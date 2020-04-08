package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.AngleToImageArray;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.Movable;
import com.daemonize.imagemovers.RotatingSpriteImageMover;

import javafx.geometry.Pos;

@Daemon(doubleDaemonize = true, implementPrototypeInterfaces = true, daemonizeBaseMethods = true)
public class Zombie extends CoordinatedImageTranslationMover implements Mortal<Zombie>, Movable {

    private volatile int hpMax;
    private volatile int hp;
    private volatile boolean shootable = true;

    private PositionedImage[] ret = new PositionedImage[1];
    private RotatingSpriteImageMover rotationMover;

    public Zombie(Image startImage, AngleToImageArray animation, Pair<Float, Float> startingPos, float dXY) {
        super(startImage, startingPos, dXY);
        this.rotationMover = new RotatingSpriteImageMover(animation, animateSemaphore, startImage, startingPos, dXY).setRotaterName("ZombieRotater");
    }

    @Daemonize
    @GenerateRunnable
    public void attack(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    @Daemonize
    @DedicatedThread(engineName = "rotate")
    public void rotateTowards(float x, float y) throws InterruptedException {
        rotationMover.rotateTowards(getLastCoordinates().getFirst(), getLastCoordinates().getSecond(), x, y);
    }

    @Override
    public int getHp() {
        return hp;
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
    public Image iterateSprite() {
        return rotationMover.iterateSprite();
    }

    @SideQuest(SLEEP = 25, blockingClosure = true)
    public PositionedImage[] animateZombie() throws InterruptedException {
        ret[0] = super.animate();
        return ret;
    }
}
