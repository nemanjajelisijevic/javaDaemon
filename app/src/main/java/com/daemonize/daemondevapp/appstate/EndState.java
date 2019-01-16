package com.daemonize.daemondevapp.appstate;

/**
 * Created by nemanja.jelisijevic on 1/15/2019.
 */

public class EndState extends DaemonState {

    private String number;

    public EndState(String number) {
        this.number = number;
    }

    @Override
    protected void onEnter() {

    }

    @Override
    public void enter() {
        System.out.println(this.getClass().getSimpleName() + " - Endeed up with a string: " + number);
    }

    @Override
    public void onExit() {

    }
}
