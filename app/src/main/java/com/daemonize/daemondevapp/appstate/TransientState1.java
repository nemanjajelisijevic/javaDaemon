package com.daemonize.daemondevapp.appstate;

import com.daemonize.daemondevapp.appstate.DaemonState;
import com.daemonize.daemondevapp.appstate.EndState;

/**
 * Created by nemanja.jelisijevic on 1/15/2019.
 */

public class TransientState1 extends DaemonState {

    private Integer number;


    public TransientState1(Integer number) {
        this.number = number;
    }

    @Override
    public void enter() {
        transit(new EndState(number.toString()));
    }

    @Override
    public void onExit() {

    }
}
