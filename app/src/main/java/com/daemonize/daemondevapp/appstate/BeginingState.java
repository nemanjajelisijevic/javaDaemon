package com.daemonize.daemondevapp.appstate;

import com.daemonize.daemonprocessor.annotations.Daemonize;

public class BeginingState extends DaemonState {

    private Float number;

    public BeginingState(Float number) {
        this.number = number;
    }

    //@Daemonize
    public static class TransientStatePreparer {
        public TransientState1 prepareTransientState(){
            return new TransientState1(Integer.valueOf((int) 3.14F));
        }
    }

    @Override
    public void enter() {

        //com.daemonize.daemondevapp.appstate.TransientStatePreparerDaemon transientStatePreparer = new com.daemonize.daemondevapp.appstate.TransientStatePreparerDaemon(consumer, new TransientStatePreparer());

        //transientStatePreparer.prepareTransientState(ret->transit(ret.get()));

        //transit();
    }

    @Override
    protected void onExit() {

    }
}
