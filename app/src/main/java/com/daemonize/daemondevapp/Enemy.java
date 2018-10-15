package com.daemonize.daemondevapp;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;


@Daemonize(doubleDaemonize = true, className = "EnemyDoubleDaemon")
public class Enemy extends CoordinatedImageTranslationMover {

    private ImageView view;
    private ImageView hpView;
    private int hpMax;
    private volatile int hp = 30;
    private volatile boolean shootable = true;
    private Image[] spriteHealthBarImage;

    public Enemy setHealthBarImage(Image[] healthBarImage) {
        this.spriteHealthBarImage = healthBarImage;
        return this;
    }

    @CallingThread
    public boolean isShootable() {
        return shootable;
    }

    public void setShootable(boolean shootable) {
        this.shootable = shootable;
    }

    @CallingThread
    public int getHp() {
        return hp;
    }

    @CallingThread
    public void setHp(int hp) {
        this.hp = hp;
    }

    @CallingThread
    public void setMaxHp(int maxHp) {
        this.hpMax = maxHp;
    }

    @CallingThread
    public ImageView getView() {
        return view;
    }

    public Enemy setView(ImageView view) {
        this.view = view;
        return this;
    }

    @CallingThread
    public ImageView getHpView() {
        return hpView;
    }

    public Enemy setHpView(ImageView hpView) {
        this.hpView = hpView;
        return this;
    }

    public Enemy(Image [] sprite, float velocity, int hp, Pair<Float, Float> startingPos, Pair<Float, Float> targetCoord) {
        super(sprite, velocity, startingPos, targetCoord);
        this.hp = hp;
        this.hpMax = hp;
    }

    @Override
    public boolean pushSprite(Image [] sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    @DedicatedThread
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
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

    @SideQuest(SLEEP = 25)
    public GenericNode<Pair<PositionedImage, ImageView>> animateEnemy() {
        PositionedImage enemyPosBmp = super.animate();
        GenericNode<Pair<PositionedImage, ImageView>> root = new GenericNode<>(Pair.create(enemyPosBmp, view));
        PositionedImage hBar = new PositionedImage();
        hBar.image = spriteHealthBarImage[(hp * 100 / hpMax - 1) / spriteHealthBarImage.length];
        hBar.positionX = lastX;
        hBar.positionY = lastY - 2 * hBar.image.getHeight();
        root.addChild(new GenericNode<>(Pair.create(hBar, hpView)));

        return root;
    }
}
