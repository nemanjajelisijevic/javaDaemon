package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.Pair;

@FunctionalInterface
public interface Existent {
    Pair<Float, Float> getLastCoordinates();
}
