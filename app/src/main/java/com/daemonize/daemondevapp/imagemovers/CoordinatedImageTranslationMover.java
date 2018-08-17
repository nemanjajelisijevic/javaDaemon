package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Daemonize(doubleDaemonize = true, className = "EnemyDoubleDaemon")
public class CoordinatedImageTranslationMover extends CachedSpriteImageTranslationMover {

    private Lock coordinateLock = new ReentrantLock();
    private Condition coordinateReachedCondition = coordinateLock.newCondition();
    private volatile boolean coordinatesReached = false;

    private volatile float targetX;
    private volatile float targetY;

    private DaemonView view;

    private volatile int hp = 20;

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

    public CoordinatedImageTranslationMover setView(DaemonView view) {
        this.view = view;
        return this;
    }

    public CoordinatedImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos, Pair<Float, Float> targetCoord) {
        super(sprite, velocity, startingPos);
        this.targetX = targetCoord.first;
        this.targetY = targetCoord.second;
    }

    @Override
    public boolean pushSprite(List<Bitmap> sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    @DedicatedThread
    public boolean goTo(float x, float y, float velocityInt) throws InterruptedException {

        super.setDirectionAndMove(x, y, velocityInt);
        coordinateLock.lock();

        targetX = x;
        targetY = y;

        try {
            while (!coordinatesReached) {
                coordinateReachedCondition.await();
            }
        } finally {
            coordinatesReached = false;
            coordinateLock.unlock();
        }

        return true;
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedBitmap move() {

        if (Math.abs(lastX - targetX)  < 10 && Math.abs(lastY - targetY)  < 10) {//TODO unhardcode this
            coordinateLock.lock();
            coordinatesReached = true;
            coordinateReachedCondition.signal();
            coordinateLock.unlock();
        }

        return super.move();
    }
}
