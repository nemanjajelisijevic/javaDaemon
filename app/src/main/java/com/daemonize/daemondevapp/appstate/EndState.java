package com.daemonize.daemondevapp.appstate;

/**
 * Created by nemanja.jelisijevic on 1/15/2019.
 */

public class EndState extends DaemonState<EndState> {

    private String number;

    public EndState(String number) {
        this.number = number;
    }

    @Override
    protected void onEnter() {

    }

    @Override
    public void onExit() {

    }
}
