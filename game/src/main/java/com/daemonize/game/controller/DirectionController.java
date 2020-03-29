package com.daemonize.game.controller;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.imagemovers.Movable;

@Daemon
public interface DirectionController<M extends Movable> {

    static enum Direction {
        UP,
        UP_LEFT,
        UP_RIGHT,
        DOWN,
        DWN_LEFT,
        DOWN_RIGHT,
        LEFT,
        RIGHT
    }

    void setControllable(M player);

    void pressDirection(Direction dir);
    void releaseDirection(Direction dir);

    void speedUp();
    void speedDown();

    @SideQuest(SLEEP = 50)
    void control() throws InterruptedException;
}
