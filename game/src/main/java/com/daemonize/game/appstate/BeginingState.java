package com.daemonize.game.appstate;

import com.daemonize.daemonprocessor.annotations.Daemon;

public class BeginingState extends DaemonState<BeginingState> {

    private Float number;

    private TransientStatePreparerDaemon transientStatePreparer;

    public BeginingState(Float number) {
        this.number = number;
    }

    @Daemon
    public static class TransientStatePreparer {
        public TransientState1 prepareTransientState(){
            return new TransientState1((int) 3.14F);
        }
    }

    @Override
    protected void onEnter() {
        transientStatePreparer = new TransientStatePreparerDaemon(consumer, new TransientStatePreparer());

        //TODO Daemon to be used here...

        transientStatePreparer.prepareTransientState(ret->transition(ret.get()));
    }

    @Override
    protected void onExit() {
        transientStatePreparer.stop();
    }
}
