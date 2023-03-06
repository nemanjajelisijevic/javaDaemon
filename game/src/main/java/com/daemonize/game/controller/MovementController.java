package com.daemonize.game.controller;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.imagemovers.Movable;

@Daemon(implementPrototypeInterfaces = true)
public interface MovementController<M extends Movable> extends Controller {


    @FunctionalInterface
    public interface OnMovementCompleteCallback<M> {
        void onMovementComplete(M controllable);
    }

    @FunctionalInterface
    public interface DirectionToCoordinateMapper {
        Pair<Float, Float> map(Direction dir);
    }

    static enum Direction {
        UP,
        UP_LEFT,
        UP_RIGHT,
        DOWN,
        DOWN_LEFT,
        DOWN_RIGHT,
        LEFT,
        RIGHT
    }

    void setControllable(M player);
    void setMovementCallback(OnMovementCompleteCallback<M> movementCallback);
    void setDirMapper(DirectionToCoordinateMapper dirMapper);

    @SideQuest(SLEEP = 50)
    void control() throws InterruptedException;
}
