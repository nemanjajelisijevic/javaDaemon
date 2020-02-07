package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.ImageTranslationMover;


public class LaserTower extends Tower {

    @FunctionalInterface
    @Daemon(eager = true)
    public interface ParalyzerEngine {
        @GenerateRunnable
        @Daemonize
        void paralyze() throws InterruptedException;
    }

    private Consumer renderer;
    private ReturnRunnable<GenericNode<Pair<PositionedImage, ImageView>>> updateRunnable = new ReturnRunnable<>();

    private volatile boolean animateFollowTarget = true;

    private ParalyzerEngineDaemon paralyzerEngineDaemon;

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
        this.targetTester = target -> target.isShootable()
                && ImageTranslationMover.absDistance(
                     target.getLastCoordinates().getFirst(),
                     target.getLastCoordinates().getSecond(),
                     getLastCoordinates().getFirst(),
                     getLastCoordinates().getSecond()
                   ) < this.range
                && target.getVelocity().intensity > 0.3F;
        this.paralyzerEngineDaemon = new ParalyzerEngineDaemon(null, null)
                .setName("Laser Tower Paralyzer Engine");
    }

    public LaserTower setParalyzerEngine(Consumer consumer, ParalyzerEngine paralyzer) {
        paralyzerEngineDaemon.stop();
        paralyzerEngineDaemon.setConsumer(consumer)
                .setPrototype(paralyzer)
                .start();
        return this;
    }

    public void paralyze(Runnable closure) {
        paralyzerEngineDaemon.paralyze(closure);
    }

    @Override
    protected void rotateTo(Target target) throws InterruptedException {
        if (!animateFollowTarget)
            super.rotateTo(target);
    }

    @Override
    public void pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        animateFollowTarget = false;
        super.pushSprite(sprite, velocity);
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
