package com.daemonize.game.interactables.ammo;

import com.daemonize.game.Projectile;
import com.daemonize.game.ProjectileDaemon;
import com.daemonize.game.SimpleBullet;
import com.daemonize.graphics2d.images.Image;

public class BulletClip implements AmmoClip<SimpleBullet> {

    private volatile int size;
    private Image bulletImage;

    private ProjectileLoader<SimpleBullet> defaultBulletLoader;

    public BulletClip(Image bulletImage) {
        this.size = 10;
        this.bulletImage = bulletImage;
    }

    @Override
    public int packSize() {
        return 10;
    }

    @Override
    public int currentSize() {
        return size;
    }

    @Override
    public BulletClip setDefaultProjectileLoader(ProjectileLoader<SimpleBullet> defaultrojectileLoader) {
        this.defaultBulletLoader = defaultrojectileLoader;
        return this;
    }

    @Override
    public ProjectileDaemon load(ProjectileLoader<SimpleBullet> projectileLoader) {
        return null;
    }

    @Override
    public ProjectileDaemon load() {

//        ProjectileDaemon bullet = new ProjectileDaemon(
//                gameConsumer,
//                new SimpleBullet(null, bulletImage, sourceCoords, dXY)
//        ).setName("Simple Bullet " + ++bulletCnt);
//
//        ((SimpleBullet) bullet.getPrototype()).setTargetFinder(currentCoords -> {
//
//            Field<FieldContent> currentField = grid.getField(currentCoords);
//
//            if (currentField != null) {
//
//                Set<Target> targets = currentField.getObject().getTargets();
//
//                for (Target currentTarget : targets) {
//                    if (ImageTranslationMover.absDistance(currentCoords, currentTarget.getLastCoordinates()) < 70) {
//
//                        final ZombieDaemon zombieTarget = ((ZombieDaemon) currentTarget);
//
//                        if(((QueuedSpriteAnimator) bulletHitAnimator.getPrototype()).getQueueSize() < 1)
//                            bulletHitAnimator.setCoords(
//                                    zombieTarget.getLastCoordinates().getFirst(),
//                                    zombieTarget.getLastCoordinates().getSecond()
//                            ).setSprite(miniExplosionSprite);
//
//                        int targetHp = currentTarget.getHp();
//                        int newTargetHp = targetHp - bulletDamage;
//
//                        if (newTargetHp < 1) {
//                            zombieTarget.clearAndInterrupt().animateDirectionalSprite(zombieFallAnimation, () -> {
//                                zombieTarget.stop();
//                                zombieTarget.getPrototype().currentField.getObject().unsubscribe(zombieTarget);
//                                System.err.println(DaemonUtils.timedTag() + bullet.getName() + " -  Target: " + zombieTarget.getName() + " DESTROYED!!!!!!!!!");
//                            });
//                        } else {
//                            zombieTarget.setHp(newTargetHp);
//                            System.out.println(DaemonUtils.timedTag() +  bullet.getName() + " - Target: " + zombieTarget.getName() + ", HIT Hp: " + zombieTarget.getHp() + "!!!!!!!!!");
//                        }
//
//                        return currentTarget;
//                    }
//                }
//            }
//
//            return null;
//        }).setCoordinateValidator(currentCoords -> {
//
//            Field<FieldContent> currentField = grid.getField(currentCoords);
//
//            if (currentField == null ||  !currentField.isWalkable()
//                    || (currentCoords.getFirst() < 0 || currentCoords.getFirst() > borderX || currentCoords.getSecond() < 0 || currentCoords.getSecond() > borderY))
//                return false;
//
//            return true;
//        });
//
//        ImageView bulletView = bulletViews.poll();
//
//        renderer.consume(() -> {
//            bulletView.setAbsoluteX(sourceCoords.getFirst())
//                    .setAbsoluteY(sourceCoords.getSecond())
//                    .show();
//        });
//
//        bullet.setAnimateProjectileSideQuest(renderer).setClosure(ret -> {
//            // create view
//            ImageMover.PositionedImage[] posImg = ret.runtimeCheckAndGet();
//            bulletView.setAbsoluteX(posImg[0].positionX)
//                    .setAbsoluteY(posImg[0].positionY);
//        });
//
//        bullet.updateTarget(new Runnable() {
//            @Override
//            public void run() {
//                bullet.updateTarget(this::run);
//            }
//        });
//
//        bullet.start()
//


        size--;
        return null;
    }
}
