package com.daemonize.game.imagemovers;

import com.daemonize.game.Pair;

import java.util.ArrayList;
import java.util.List;

public class MassiveImageTranslationMover implements MassiveImageMover {

    private List<ImageMover> imageMovers;

    public MassiveImageTranslationMover(List<ImageMover> imageMovers) {
        this.imageMovers = imageMovers;
    }

    @Override
    public void addMover(ImageMover mover) {
        imageMovers.add(mover);
    }

    @Override
    public void setDirectionAndMove(float x, float y, float velocityInt) {
        for(ImageMover mover : imageMovers) {
            mover.setDirectionAndMove(x, y, velocityInt);
        }
    }

    @Override
    public void breakFormation(float velocityInt) {

        float angle = 0;
        float angleStep = 360 / imageMovers.size();
        for(ImageMover mover : imageMovers) {

            float angleF = angle * 0.0174533F;
            mover.setVelocity(
                    new ImageMover.Velocity(
                            velocityInt,
                            new ImageMover.Direction(
                                    (float) Math.cos(angleF) * 100,
                                    -(float) Math.sin(angleF) * 100)
                    )
            );

            angle += angleStep;
        }
    }

    @Override
    public List<Pair<Float, Float>> getLastCoordinates() {
        int size = imageMovers.size();
        List<Pair<Float, Float>> ret = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            ret.add(imageMovers.get(i).getLastCoordinates());
        }
        return ret;
    }

    @Override
    public List<ImageMover.Velocity> getVelocities() {
        int size = imageMovers.size();
        List<ImageMover.Velocity> ret = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            ret.add(imageMovers.get(i).getVelocity());
        }
        return ret;
    }

    @Override
    public List<ImageMover.PositionedImage> move() throws InterruptedException {
        List<ImageMover.PositionedImage> ret = new ArrayList<>(imageMovers.size());
        for (ImageMover imageMover : imageMovers) {
            ImageMover.PositionedImage moverRet = imageMover.animate();
            if (moverRet != null)
                ret.add(moverRet);
        }

        if (ret.size() == 0)
            return null;

        return ret;
    }
}
