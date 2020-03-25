package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.Pair;

public interface Movable {

    @FunctionalInterface
    public interface AnimationWaiter {
        void await() throws InterruptedException;
    }

    Pair<Float, Float> getLastCoordinates();
    void setVelocity(float velocity);
    ImageMover.Velocity getVelocity();
    public AnimationWaiter getAnimationWaiter();

}
