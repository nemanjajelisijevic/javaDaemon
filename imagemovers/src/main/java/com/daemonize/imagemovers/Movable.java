package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.Pair;

public interface Movable extends Existent {

    @FunctionalInterface
    public interface AnimationWaiter {
        void await() throws InterruptedException;
    }

    @FunctionalInterface
    public interface CoordinateExporter {
        void exportCoords(float x, float y);
    }

    void setVelocity(float velocity);
    ImageMover.Velocity getVelocity();
    public AnimationWaiter getAnimationWaiter();

}
