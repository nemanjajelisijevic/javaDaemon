package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;


@Daemonize(doubleDaemonize = true, className = "EnemyDoubleDaemon")
public class Enemy extends CoordinatedImageTranslationMover {

    private DaemonView view;
    private volatile int hp = 20;
    private volatile boolean shootable = true;

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

    public Enemy(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos, Pair<Float, Float> targetCoord) {
        super(sprite, velocity, startingPos, targetCoord);
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

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedBitmap animate() {
        return super.animate();
    }
}
