package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.game.images.Image;


public class LaserTower extends Tower {


    private Consumer renderer;
    private ReturnRunnable<PositionedImage> updateRunnable = new ReturnRunnable<>();

    public LaserTower(Consumer renderer, Closure<PositionedImage> updateClosure, Image[] rotationSprite, Pair<Float, Float> startingPos, float range, TowerType type, float dXY) {
        super(rotationSprite, startingPos, range, type, dXY);
        this.renderer = renderer;
        this.updateRunnable.setClosure(updateClosure);
        this.targetTester = target -> target.isShootable()
                && (Math.abs(target.getLastCoordinates().getFirst() - getLastCoordinates().getFirst()) < this.range
                && Math.abs(target.getLastCoordinates().getSecond() - getLastCoordinates().getSecond()) < this.range)
                && target.getVelocity().intensity > 0.3F;
    }

    @Override
    protected void rotateTo(EnemyDoubleDaemon target) throws InterruptedException {}

    @Override
    public PositionedImage animate() throws InterruptedException {

        targetLock.lock();
        try {

            while (targetQueue.isEmpty())
                targetCondition.await();

            EnemyDoubleDaemon target = targetQueue.peek();

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
                        renderer.consume(updateRunnable.setResult(ret));
                    }
            }

            return ret;

        } finally {
            targetLock.unlock();
        }
    }

}
