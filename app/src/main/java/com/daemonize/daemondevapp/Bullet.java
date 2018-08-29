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

@Daemonize(doubleDaemonize = true, className = "BulletDoubleDaemon")
public class Bullet extends CoordinatedImageTranslationMover {

    private DaemonView view;
    private volatile int damage = 2;

    public Bullet(List<Image> sprite, float velocity, Pair<Float, Float> startingPos,
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

    @SideQuest(SLEEP = 30)
    @Override
    public PositionedImage animate() {

//        if (Math.abs(lastX) < velocity.intensity
//                || Math.abs(lastX - borderX) < velocity.intensity
//                || Math.abs(lastY) < velocity.intensity
//                || Math.abs(lastY - borderY) < velocity.intensity)
//            throw new IllegalStateException("Bullet out of borders!");

        return super.animate();
    }
}
