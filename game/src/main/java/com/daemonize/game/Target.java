package com.daemonize.game;

import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.imagemovers.Movable;

public interface Target<T extends Target> extends Movable {
    boolean isShootable();
    T setShootable(boolean shootable);
    int getHp();
    T setHp(int hp);
    T setMaxHp(int maxHp);
    int getMaxHp();
    boolean isParalyzed();
    T setParalyzed(boolean paralyzed);
    void setVelocity(float velocity);
    ImageMover.Velocity getVelocity();
}
