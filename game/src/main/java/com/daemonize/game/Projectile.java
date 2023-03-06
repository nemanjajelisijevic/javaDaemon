package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;

@Daemon(doubleDaemonize = true, implementPrototypeInterfaces = true)
public interface Projectile  extends Movable {
    //boolean shoot(Target target);


    @Override
    void setVelocity(float velocity);

    @Override
    ImageMover.Velocity getVelocity();

    @Override
    AnimationWaiter getAnimationWaiter();

    @Override
    Pair<Float, Float> getLastCoordinates();

    @Daemonize
    public boolean shoot(float x, float y, float velocity) throws InterruptedException;

    @Daemonize
    @GenerateRunnable
    @DedicatedThread(engineName = "targetUpdater")
    void updateTarget() throws InterruptedException;

    @SideQuest(SLEEP = 25, blockingClosure = true)
    public ImageMover.PositionedImage[] animateProjectile() throws InterruptedException;
}
