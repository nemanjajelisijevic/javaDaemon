package com.daemonize.daemondevapp;


import android.util.Log;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Daemonize(doubleDaemonize = true)
public class LaserBullet extends Bullet {

    protected List<ImageView> views;
    protected EnemyDoubleDaemon target;
    private volatile List<PositionedImage> photonList;
    private float[] coefficients;

    private volatile boolean fire = false;
    private DaemonCountingSemaphore phaseLock;

    @CallingThread
    public void setViews(List<ImageView> views) {
        this.views = views;
        this.photonList = new ArrayList<>(views.size());
        this.coefficients = new float[views.size()];
        float dc = 1F / coefficients.length;
        for (int i = 0; i < coefficients.length; ++i)  {
            coefficients[i] = (i + 1) * dc;
            PositionedImage dummy = new PositionedImage();
//            dummy.positionX = 0;
//            dummy.positionY = 0;
//            dummy.image = spriteIterator.getSprite()[0];
//            photonList.add(dummy);
        }
    }

    @CallingThread
    @Override
    public int getDamage() {
        return super.getDamage();
    }

    @CallingThread
    @Override
    public void setStartingCoords(Pair<Float, Float> startingCoords) {
        super.setStartingCoords(startingCoords);
    }

    @Override
    public Image iterateSprite() {
        return spriteIterator.iterateSprite();
    }


    public LaserBullet(
            Image[] sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            Pair<Float, Float> targetCoord,
            int damage
    ) {
        super(sprite, velocity, startingPos, targetCoord, damage);
        this.phaseLock = new DaemonCountingSemaphore();
        this.phaseLock.subscribe();
    }

    public List<ImageView> desintegrateTarget(
            Pair<Float, Float> sourceCoord,
            EnemyDoubleDaemon target,
            long duration,
            Consumer drawConsumer
    ) throws InterruptedException {

        if (!target.isShootable())
            return null;

        for (ImageView view : views)
            drawConsumer.consume(()->view.hide());

        this.target = target;

        photonList.clear();

        float dX = (target.getLastCoordinates().getFirst() - sourceCoord.getFirst()) / views.size();
        float dY = (target.getLastCoordinates().getSecond() - sourceCoord.getSecond()) / views.size();

        PositionedImage first = new PositionedImage();

        first.positionX = sourceCoord.getFirst() + dX;
        first.positionY = sourceCoord.getSecond() + dY;
        first.image = iterateSprite();
        //photonList.set(0, first);
        photonList.add(first);

        PositionedImage current = first.clone();

        for (int i = 1; i < views.size(); ++i) {
            current.positionX += dX;
            current.positionY += dY;
            //photonList.set(i, current);
            photonList.add(current);
            current = photonList.get(photonList.size() - 1).clone();
            ImageView currView = views.get(i);
            drawConsumer.consume(()->currView.show());
        }

        fire = true;
        phaseLock.unsubscribe();

        try {
            Thread.currentThread().sleep(duration);
        } finally {
            fire = false;
            drawConsumer.consume(()->{
                for (ImageView view : views)
                    view.hide();
            });
            phaseLock.subscribe();
        }

        return views;
    }

    @SideQuest(SLEEP = 25)
    public List<Pair<ImageView, PositionedImage>> animateLaser() throws InterruptedException {

        phaseLock.await();

        if (!fire || !target.isShootable())
            return null;

        if (target == null)
            throw new IllegalArgumentException("Target is null!");

        Velocity velocity = target.getVelocity();
        List<Pair<ImageView, PositionedImage>> ret = new LinkedList<>();

        for (int j = 0; j < photonList.size(); ++j) {
            photonList.get(j).positionX = photonList.get(j).positionX + velocity.intensity * coefficients[j] * velocity.direction.coeficientX;
            photonList.get(j).positionY = photonList.get(j).positionY + velocity.intensity * coefficients[j] * velocity.direction.coeficientY;
            ret.add(Pair.create(views.get(j), photonList.get(j)));
        }

        return ret;
    }

}
