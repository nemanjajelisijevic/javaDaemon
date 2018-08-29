package com.daemonize.daemondevapp;

import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;


@Daemonize(doubleDaemonize = true, className = "EnemyDoubleDaemon")
public class Enemy extends CoordinatedImageTranslationMover {

    private DaemonView view;
    private DaemonView hpView;
    private  int hpMax;
    private volatile int hp = 30;
    private volatile boolean shootable = true;
    private List<Image> spriteHealthBarImage;

    public Enemy setHealthBarImage(List<Image> healthBarImage) {
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

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setMaxHp(int maxHp) {
        this.hpMax = maxHp;
    }

    @CallingThread
    public DaemonView getView() {
        return view;
    }

    public Enemy setView(DaemonView view) {
        this.view = view;
        return this;
    }

    @CallingThread
    public DaemonView getHpView() {
        return hpView;
    }

    public Enemy setHpView(DaemonView hpView) {
        this.hpView = hpView;
        return this;
    }

    public Enemy(List<Image> sprite, float velocity, int hp, Pair<Float, Float> startingPos, Pair<Float, Float> targetCoord) {
        super(sprite, velocity, startingPos, targetCoord);
        this.hp = hp;
        this.hpMax = hp;
    }

    @Override
    public boolean pushSprite(List<Image> sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    @DedicatedThread
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @SideQuest(SLEEP = 30)
    public GenericNode<Pair<PositionedImage, DaemonView>> animateEnemy() {
        PositionedImage enemyPosBmp = super.animate();
        GenericNode<Pair<PositionedImage, DaemonView>> root = new GenericNode<>(Pair.create(enemyPosBmp, view));
        PositionedImage hBar = new PositionedImage();
        hBar.image = spriteHealthBarImage.get((hp * 100 / hpMax - 1) / spriteHealthBarImage.size());
        hBar.positionX = lastX - enemyPosBmp.image.getWidth() / 3;
        hBar.positionY = lastY - enemyPosBmp.image.getHeight() / 2 - hBar.image.getHeight();
        root.addChild(new GenericNode<>(Pair.create(hBar, hpView)));
        return root;
    }
}
