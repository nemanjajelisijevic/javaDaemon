package com.daemonize.game.controller;

public abstract class TouchController {
    public void touch(float x, float y) {
        this.onTouch(x, y);
    }

    public abstract void onTouch(float x, float y);
}
