package com.daemonize.daemondevapp.imagemovers.borders;


import android.util.Pair;

public interface Border {

    float getMinX();

    float getMaxX();

    float getMinY();

    float getMaxY();

    Pair<Pair<Boolean, Boolean>, Pair<Boolean, Boolean>> checkBorder(float objectsX, float objectsY); //TODO rethink the return type
}
