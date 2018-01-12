package com.daemonize.daemondevapp.imagemovers.collider;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemondevapp.imagemovers.ImageTranslationMover;

import java.util.ArrayList;
import java.util.List;

public class ColliderImageMover extends ImageTranslationMover {

    private List<ImageMoverDaemon> others;

    public void addOther(ImageMoverDaemon other) {
        if (!other.getPrototype().equals(this)) {
            others.add(other);
        }
    }

    private PositionUpdate[] othersPosition;
    private int id;

    public ColliderImageMover(
            List<Bitmap> sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            int id,
            int othersSize
    ) {
        super(sprite, velocity, startingPos);
        this.id = id;
        this.others = new ArrayList<>(othersSize);
        this.othersPosition = new PositionUpdate[othersSize];
    }

    @Override
    public void updatePosition(int id, PositionUpdate update) {
        othersPosition[id] = update;
    }

    @Override
    public PositionedBitmap move() {

        //update others
        for (ImageMoverDaemon other : others) {
            other.updatePosition(id, new PositionUpdate(
                        true,
                        lastX,
                        lastY,
                        velocity,
                        new Direction(this.currentDirX, this.currentDirY)
                    )
            );
        }

        //check collisions
        for (PositionUpdate update : othersPosition) {
            //check for collision
            if (update.isAlive()
                    && Math.abs(this.lastX - update.getX()) < 10
                    && Math.abs(this.lastY - update.getY()) < 10) {
                setDirection(update.getDirection());
                setVelocity(update.getVelocity() / 2);
                break;
            }
        }

        return super.move();
    }
}
