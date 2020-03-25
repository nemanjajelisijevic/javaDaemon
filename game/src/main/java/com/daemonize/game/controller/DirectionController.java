package com.daemonize.game.controller;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemon
public interface DirectionController {

    static enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    void pressDirection(Direction dir);
    void releaseDirection(Direction dir);

    void speedUp();
    void speedDown();

    @SideQuest(SLEEP = 50)
    void control() throws InterruptedException;
}
