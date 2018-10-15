package com.daemonize.daemondevapp;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;

@Daemonize(doubleDaemonize = true, className = "BulletDoubleDaemon")
public class Bullet extends CoordinatedImageTranslationMover {

    private ImageView view;
    private volatile int damage = 2;

    public Bullet(Image [] sprite, float velocity, Pair<Float, Float> startingPos,
                  Pair<Float, Float> targetCoord, int damage) {
        super(sprite, velocity, startingPos, targetCoord);
        this.damage = damage;
    }

    public void setStartingCoords(Pair<Float, Float> startingCoords) {
        lastX = startingCoords.getFirst();
        lastY = startingCoords.getSecond();
    }

    @CallingThread
    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @CallingThread
    public ImageView getView() {
        return view;
    }

    public Bullet setView(ImageView view) {
        this.view = view;
        return this;
    }

    @DedicatedThread
    @Override
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {
        return super.goTo(x, y, velocityInt);
    }

    @SideQuest(SLEEP = 25)
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

    @SideQuest(SLEEP = 30)
    @Override
    public PositionedImage animate() {
        return super.animate();
    }
}
