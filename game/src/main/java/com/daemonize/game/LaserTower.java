package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.images.Image;
import com.daemonize.game.scene.views.ImageView;


public class LaserTower extends Tower {

    private Consumer renderer;
    private ReturnRunnable<GenericNode<Pair<PositionedImage, ImageView>>> updateRunnable = new ReturnRunnable<>();

    public LaserTower(Consumer renderer, Closure<GenericNode<Pair<PositionedImage, ImageView>>> updateClosure, Image[] rotationSprite, Image[] healthBarSprite, Pair<Float, Float> startingPos, float range, TowerType type, float dXY, int hp) {
        super(rotationSprite, healthBarSprite, startingPos, range, type, dXY, hp);
        this.renderer = renderer;
        this.updateRunnable.setClosure(updateClosure);
        this.targetTester = target -> target.isShootable()
                && (Math.abs(target.getLastCoordinates().getFirst() - getLastCoordinates().getFirst()) < this.range
                && Math.abs(target.getLastCoordinates().getSecond() - getLastCoordinates().getSecond()) < this.range)
                && target.getVelocity().intensity > 0.3F;
    }

    @Override
    protected void rotateTo(Target target) throws InterruptedException {}

    @Override
    public void pushSprite(Image[] sprite, float velocity) throws InterruptedException {}//TODO FIX this

    @Override
    public GenericNode<Pair<PositionedImage, ImageView>> animateTower() throws InterruptedException {

        targetLock.lock();
        try {

            //while (targetQueue.isEmpty())
            while (target == null)
                targetCondition.await();

            //Target target = targetQueue.peek();
            Target target = this.target;

            if(target != null && targetTester.test(target)) {

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
                    for(Image image : retSprite) {
                        ret.image = image;
                        Thread.sleep(25);
                        renderer.consume(updateRunnable.setResult(updateHpSprite(new GenericNode<>(Pair.create(ret, view)))));
                    }
            }

            return updateHpSprite(new GenericNode<Pair<PositionedImage, ImageView>>(Pair.create(ret, view)));

        } finally {
            targetLock.unlock();
        }
    }
}
