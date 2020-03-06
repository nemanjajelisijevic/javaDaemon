package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.graphics2d.images.Image;


public class ImmortalEnemy extends Enemy {

    public ImmortalEnemy(Image[] sprite, float velocity, int hp, Pair<Float, Float> startingPos, float dXY) {
        super(sprite, velocity, hp, startingPos, dXY);
    }

    @Override
    public Enemy setHp(int hp) {
        return this;
    }

}
