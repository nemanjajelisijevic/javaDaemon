package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemondevapp.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;


@Daemonize(doubleDaemonize = true, className = "BulletDoubleDaemon")
public class Bullet extends CoordinatedImageTranslationMover {

    private int bulletNo;

    private ImageView view;
    private volatile int damage = 2;

    public Bullet(Image [] sprite, float velocity, Pair<Float, Float> startingPos,
                  Pair<Float, Float> targetCoord, int damage) {
        super(sprite, velocity, startingPos, targetCoord);
        this.damage = damage;
    }

    @CallingThread
    public void setNo(int no) {
        this.bulletNo = no;
    }

    @CallingThread
    public int getBulletNo() {
        return bulletNo;
    }

    @CallingThread
    public void setStartingCoords(Pair<Float, Float> startingCoords) {
        lastX = startingCoords.getFirst();
        lastY = startingCoords.getSecond();
    }

    @CallingThread
    public int getDamage() {
        return damage;
    }

    @CallingThread
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

    @Override
    public boolean pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
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


    private Consumer consumer;
    private Runnable outOfBordersClosure;

    @CallingThread
    public Bullet setOutOfBordersConsumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    @CallingThread
    public Bullet setOutOfBordersClosure(Runnable outOfBordersClosure) {
        this.outOfBordersClosure = outOfBordersClosure;
        return this;
    }

    @SideQuest(SLEEP = 25)
    public PositionedImage animateBullet() {
        if (lastX <= borderX1 ||
                lastX >= borderX2 ||
                lastY <= borderY1 ||
                lastY >= borderY2) {
            consumer.consume(outOfBordersClosure);
        }
        return super.animate();
    }
}
