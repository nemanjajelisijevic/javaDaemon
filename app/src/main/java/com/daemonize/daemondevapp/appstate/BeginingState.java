package com.daemonize.daemondevapp.appstate;

import com.daemonize.daemonprocessor.annotations.Daemonize;

public class BeginingState extends DaemonState<BeginingState> {

    private Float number;

    private TransientStatePreparerDaemon transientStatePreparer;

    public BeginingState(Float number) {
        this.number = number;
    }

    @Daemonize
    public static class TransientStatePreparer {
        public TransientState1 prepareTransientState(){
            return new TransientState1((int) 3.14F);
        }
    }

    @Override
    protected void onEnter() {
        transientStatePreparer = new TransientStatePreparerDaemon(consumer, new TransientStatePreparer());
    }

    @Override
    public void enter() {



        transientStatePreparer.prepareTransientState(ret-> transition(ret.get()));

    }

    @Override
    protected void onExit() {
        transientStatePreparer.stop();
    }
}
