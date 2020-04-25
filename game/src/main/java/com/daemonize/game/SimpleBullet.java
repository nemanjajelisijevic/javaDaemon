package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.Movable;

public class SimpleBullet extends CoordinatedImageTranslationMover implements Projectile, Movable {

    @FunctionalInterface
    public interface TargetFinder {
        Target getNearestTarget(Pair<Float, Float> latestCoords);
    }

    @FunctionalInterface
    public interface CoordinateValidator {
        boolean validateCoords(Pair<Float, Float> latestCoords);
    }

    private volatile Target nearestTarget;
    private TargetFinder targetFinder;

    private CoordinateValidator coordinateValidator;

    //TODO fire explosions
    private Image[] explodeOnImpactSprite;
    private PositionedImage[] ret;

    public SimpleBullet(Image[] explodeOnImpactSprite, Image startImage, Pair<Float, Float> startingPos, float dXY) {
        super(startImage, startingPos, dXY);
        this.explodeOnImpactSprite = explodeOnImpactSprite;
        this.ret = new PositionedImage[1];
        ret[0] = new PositionedImage();
        ret[0].positionX = startingPos.getFirst();
        ret[0].positionY = startingPos.getSecond();
        ret[0].image = startImage;
    }

    public SimpleBullet setTargetFinder(TargetFinder targetFinder) {
        this.targetFinder = targetFinder;
        return this;
    }

    public SimpleBullet setCoordinateValidator(CoordinateValidator coordinateValidator) {
        this.coordinateValidator = coordinateValidator;
        return this;
    }

    @Override
    public boolean shoot(float x, float y, float velocity) throws InterruptedException {

        boolean ret = goTo(x, y, velocity);
//
//        if (ret)
//            pushSprite(explodeOnImpactSprite);

        nearestTarget = null;

        return ret;
    }

    @Override
    public void updateTarget() throws InterruptedException {

        Thread.sleep(100);

        this.animateSemaphore.await();

        Pair<Float, Float> latestCoords = getLastCoordinates();

        this.nearestTarget = targetFinder.getNearestTarget(latestCoords);

        if (nearestTarget != null) {
            redirect(nearestTarget.getLastCoordinates().getFirst(), nearestTarget.getLastCoordinates().getSecond());
        } else if (!coordinateValidator.validateCoords(latestCoords)){
            redirect(latestCoords.getFirst() + 1, latestCoords.getSecond() + 1);
        }
    }

    @Override
    public PositionedImage[] animateProjectile() throws InterruptedException {
        ret[0] = super.animate();
        return this.ret;
    }
}
