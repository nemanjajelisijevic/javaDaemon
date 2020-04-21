package com.daemonize.game.controller;

import com.daemonize.imagemovers.Movable;

public interface KeyboardMovementController<M extends Movable> extends MovementController<M> {
    void pressDirection(Direction dir);
    void releaseDirection(Direction dir);

    void speedUp();
    void speedDown();
}
