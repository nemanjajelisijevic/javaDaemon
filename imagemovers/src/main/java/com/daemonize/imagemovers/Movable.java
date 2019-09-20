package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.Pair;

public interface Movable {
    Pair<Float, Float> getLastCoordinates();
    void setVelocity(float velocity);
    ImageMover.Velocity getVelocity();
}
