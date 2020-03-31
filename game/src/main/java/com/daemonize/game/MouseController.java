package com.daemonize.game;

import com.daemonize.game.controller.Controller;

public interface MouseController extends Controller {

    public enum BUTTON {
        LEFT,
        RIGHT,
        WHEEL_UP,
        WHEEL_DOWN
    }

    void onClick(BUTTON mouseButton);
    void onRelease(BUTTON mouseButton);

    void onMove(float x, float y);


    void control();
}
