package com.daemonize.game.appstate;

/**
 * Created by nemanja.jelisijevic on 1/15/2019.
 */

public class TransientState1 extends DaemonState<TransientState1> {

    private Integer number;


    public TransientState1(Integer number) {
        this.number = number;
    }

    @Override
    protected void onEnter() {

    }

    @Override
    public void onExit() {

    }
}
