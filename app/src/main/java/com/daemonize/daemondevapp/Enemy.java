package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.LinkedList;
import java.util.List;


@Daemonize(doubleDaemonize = true, className = "EnemyDoubleDaemon")
public class Enemy extends CoordinatedImageTranslationMover {

    private DaemonView view;
    private volatile int hp = 30;
    private volatile boolean shootable = true;
    private Bitmap healthBarImage;//TODO should be sprite!!!!!!!!!!!!!!!!!!!!!!!

    public Enemy setHealthBarImage(Bitmap healthBarImage) {
        this.healthBarImage = healthBarImage;
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

    @CallingThread
    public DaemonView getView() {
        return view;
    }

    public Enemy setView(DaemonView view) {
        this.view = view;
        return this;
    }

    public Enemy(List<Bitmap> sprite, float velocity, int hp, Pair<Float, Float> startingPos, Pair<Float, Float> targetCoord) {
        super(sprite, velocity, startingPos, targetCoord);
        this.hp = hp;
    }

    @Override
    public boolean pushSprite(List<Bitmap> sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    @DedicatedThread
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @SideQuest(SLEEP = 30)
    @Override
    public PositionedBitmap animate() {

        PositionedBitmap hBar = new PositionedBitmap();
        hBar.positionX = lastX;
        hBar.positionY = lastY - 60;
        hBar.image = healthBarImage;


        PositionedBitmap ret = super.animate();
        ret.children = new LinkedList<>();
        ret.children.add(hBar);
        return ret;
    }
}
