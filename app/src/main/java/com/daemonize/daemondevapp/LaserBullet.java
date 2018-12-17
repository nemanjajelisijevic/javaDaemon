package com.daemonize.daemondevapp;


import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

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
    private List<PositionedImage> photonList;

    private Lock phaseLock = new ReentrantLock();
    private Condition phaseCondition = phaseLock.newCondition();
    private volatile boolean fire = false;

    @CallingThread
    public void setViews(List<ImageView> views) {
        this.views = views;
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

    public List<ImageView> desintegrateTarget(
            Pair<Float, Float> sourceCoord,
            EnemyDoubleDaemon target,
            long duration
    ) throws InterruptedException {

        for (ImageView view : views)
            view.hide();

        this.target = target;

        float dX = (target.getLastCoordinates().getFirst() - sourceCoord.getFirst()) / views.size();
        float dY = (target.getLastCoordinates().getSecond() - sourceCoord.getSecond()) / views.size();

        PositionedImage first = new PositionedImage();

        first.positionX = sourceCoord.getFirst() + dX;
        first.positionY = sourceCoord.getSecond() + dY;
        first.image = iterateSprite();
        photonList.add(first);

        PositionedImage current = first.clone();

        for (int i = 1; i < views.size(); ++i) {
            current.positionX += dX;
            current.positionY += dY;
            photonList.add(current);
            current = photonList.get(photonList.size() - 1).clone();
        }

        phaseLock.lock();
        fire = true;
        phaseCondition.signal();
        phaseLock.unlock();

        try {
            Thread.currentThread().sleep(duration);
        } finally {
            phaseLock.lock();
            fire = false;
            photonList.clear();
            for (ImageView view : views)
                view.hide();
            phaseLock.unlock();
        }

        return views;
    }


    public LaserBullet(
            Image[] sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            Pair<Float, Float> targetCoord,
            int damage
    ) {
        super(sprite, velocity, startingPos, targetCoord, damage);
        this.photonList = new LinkedList<>();
    }

    @SideQuest(SLEEP = 25)
    public List<Pair<ImageView, PositionedImage>> animateLaser() throws InterruptedException {

        phaseLock.lock();
        try {
            while (!fire)
                phaseCondition.await();
        } finally {
            phaseLock.unlock();
        }

        if (!fire)
            return null;

        if (target == null)
            throw new IllegalArgumentException("Target is null!");

        Velocity velocity = target.getVelocity();
        List<Pair<ImageView, PositionedImage>> ret = new LinkedList<>();
        Iterator<ImageView> viewIt = views.iterator();

        float[] coefficients = new float[photonList.size()];

        float dc = 1F / coefficients.length;

        for (int i = 0; i < coefficients.length; ++i)  {
            coefficients[i] = (i + 1) * dc;
        }

        for (int j = 0; j < photonList.size(); ++j) {
            photonList.get(j).positionX = photonList.get(j).positionX + velocity.intensity * coefficients[j] * velocity.direction.coeficientX;
            photonList.get(j).positionY = photonList.get(j).positionY + velocity.intensity * coefficients[j] * velocity.direction.coeficientY;
            ret.add(Pair.create(viewIt.next().show(), photonList.get(j)));
        }

        return ret;
    }

}
