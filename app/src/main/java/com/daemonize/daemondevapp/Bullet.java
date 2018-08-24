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

@Daemonize(doubleDaemonize = true, className = "BulletDoubleDaemon")
public class Bullet extends CoordinatedImageTranslationMover {

    private DaemonView view;
    private volatile int damage = 2;

    public Bullet(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos,
                  Pair<Float, Float> targetCoord, int damage) {
        super(sprite, velocity, startingPos, targetCoord);
        this.damage = damage;
    }

    public void setStartingCoords(Pair<Float, Float> startingCoords) {
        lastX = startingCoords.first;
        lastY = startingCoords.second;
    }

    @CallingThread
    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @CallingThread
    public DaemonView getView() {
        return view;
    }

    public Bullet setView(DaemonView view) {
        this.view = view;
        return this;
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
