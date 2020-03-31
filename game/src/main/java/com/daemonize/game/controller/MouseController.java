package com.daemonize.game.controller;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemon(implementPrototypeInterfaces = true)
public interface MouseController extends Controller {

    public enum MouseButton {
        LEFT,
        RIGHT,
        WHEEL_UP,
        WHEEL_DOWN
    }

    @FunctionalInterface
    public interface ClickCoordinateClosure {
        void onClick(float x, float y, MouseButton mouseButton);
    }

    @FunctionalInterface
    public interface HooverCoordinateClosure {
        void onHoover(float x, float y);
    }

    void setOnClick(ClickCoordinateClosure clickCoordinateClosure);
    void setOnHoover(HooverCoordinateClosure hooverCoordinateClosure);

    void onClick(MouseButton mouseButton, float x, float y);
    void onRelease(MouseButton mouseButton);

    void onMove(float x, float y);

    @SideQuest(SLEEP = 50)
    void control() throws InterruptedException;
}
