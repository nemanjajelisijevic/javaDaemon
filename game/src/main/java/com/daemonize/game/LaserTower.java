package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.ImageTranslationMover;


public class LaserTower extends Tower {

    private Consumer renderer;
    private ReturnRunnable<GenericNode<Pair<PositionedImage, ImageView>>> updateRunnable = new ReturnRunnable<>();

    private volatile boolean animateFollowTarget = true;

    public LaserTower(
            Consumer renderer,
            Closure<GenericNode<Pair<PositionedImage, ImageView>>> updateClosure,
            Image[] rotationSprite,
            Image[] healthBarSprite,
            Pair<Float, Float> startingPos,
            float range,
            TowerType type,
            float dXY,
            int hp
    ) {
        super(rotationSprite, healthBarSprite, startingPos, range, type, dXY, hp);
        this.renderer = renderer;
        this.updateRunnable.setClosure(updateClosure);
        this.targetTester = target -> target.isAttackable()
                && ImageTranslationMover.absDistance(
                     target.getLastCoordinates().getFirst(),
                     target.getLastCoordinates().getSecond(),
                     getLastCoordinates().getFirst(),
                     getLastCoordinates().getSecond()
                   ) < this.range
                && target.getVelocity().intensity > 0.3F;
    }

    @Override
    protected void rotateTo(Target target) throws InterruptedException {
        if (!animateFollowTarget)
            super.rotateTo(target);
    }

    @Override
    public void pushSprite(Image[] sprite) throws InterruptedException {
        animateFollowTarget = false;
        super.pushSprite(sprite);
        animateFollowTarget = true;
    }

    @Override
    public GenericNode<Pair<PositionedImage, ImageView>> animateTower() throws InterruptedException {

        if (animateFollowTarget) {

            targetLock.lock();
            try {

                while (target == null)
                    targetCondition.await();

                Target target = this.target;

                if (target != null && targetTester.test(target)) {

                    int targetAngle = (int) getAngle(
                            getLastCoordinates().getFirst(),
                            getLastCoordinates().getSecond(),
                            target.getLastCoordinates().getFirst(),
                            target.getLastCoordinates().getSecond()
                    );

                    Image[] retSprite = getRotationSprite(targetAngle);

                    if (retSprite.length == 1)
                        ret.image = retSprite[0];
                    else
                        for (Image image : retSprite) {
                            ret.image = image;
                            Thread.sleep(25);
                            genericRet.getValue().setFirst(ret).setSecond(view);
                            updateHpSprite();
                            renderer.consume(updateRunnable.setResult(genericRet));
                        }
                }

                genericRet.getValue().setFirst(ret).setSecond(view);
                updateHpSprite();

                return genericRet;
            } finally {
                targetLock.unlock();
            }

        } else {
            return super.animateTower();
        }
    }
}
