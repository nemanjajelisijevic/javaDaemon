package com.daemonize.game.controller;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemon
public interface MovableController {

    static enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    void pressDirection(Direction dir);
    void releaseDirection(Direction dir);

    @SideQuest(SLEEP = 50)
    void control() throws InterruptedException;
}
