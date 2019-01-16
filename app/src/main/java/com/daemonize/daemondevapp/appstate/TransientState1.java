package com.daemonize.daemondevapp.appstate;

/**
 * Created by nemanja.jelisijevic on 1/15/2019.
 */

public class TransientState1 extends DaemonState {

    private Integer number;


    public TransientState1(Integer number) {
        this.number = number;
    }

    @Override
    protected void onEnter() {

    }

    @Override
    public void enter() {
        transition(new EndState(number.toString()));
    }

    @Override
    public void onExit() {

    }
}
