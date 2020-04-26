package com.daemonize.game;

import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;

public interface Target<T extends Target> extends Movable, Mortal<T> {
    boolean isAttackable();
    T setAttackable(boolean attackable);
    T destroy();
}
