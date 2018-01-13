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

    @Override
    public ColliderImageMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    private PositionUpdate[] othersPosition;
    private int id;

    public int getId() {
        return id;
    }

    private int proximity = 50;

    public ColliderImageMover setProximity(int proximity) {
        this.proximity = proximity;
        return this;
    }

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

    public void checkCollisionAndBounce(
            Pair<Float, Float> colliderCoordinates,
            float velocity,
            Direction direction
    ) {

        if(Math.abs(lastX - colliderCoordinates.first) < 50 && Math.abs(lastY - colliderCoordinates.second) < 50) {
            setVelocity(velocity);
            setDirection(new Direction(
                    (direction.coeficientX + currentDirX) / 2 ,
                    (direction.coeficientY + currentDirY) / 2
            ));
        }
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
            if(update == null) {
                continue;
            }
            //check for collision
            if (update.isAlive()
                    && Math.abs(this.lastX - update.getX()) < proximity
                    && Math.abs(this.lastY - update.getY()) < proximity) {
                setDirection(
                        new Direction(
                                 - (update.getDirection().coeficientX),
                                - (update.getDirection().coeficientY)
                        )
                );
                setVelocity(
                        update.getVelocity() > 0
                                ?  (update.getVelocity() + velocity) / 2
                                : velocity / 2
                );
                break;
            }
        }

        return super.move();
    }
}
