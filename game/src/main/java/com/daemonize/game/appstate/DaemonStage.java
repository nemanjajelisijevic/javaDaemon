package com.daemonize.game.appstate;

import com.daemonize.daemonengine.consumer.Consumer;


public abstract class DaemonStage<S extends DaemonStage> {

    protected Consumer consumer;

    protected DaemonStage(Consumer consumer) {
        this.consumer = consumer;
    }

    public final S enter() {
        onEnter();
        return (S) this;
    }

    protected abstract void onEnter();

    protected abstract void onExit();

    protected final void transition(DaemonStage next) {
        onExit();
        consumer.consume(new Runnable() {
            @Override
            public void run() {
                next.enter();
            }
        });
    }
}

