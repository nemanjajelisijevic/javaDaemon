package com.daemonize.daemondevapp.appstate;

import com.daemonize.daemonengine.consumer.Consumer;


public abstract class DaemonState<T extends DaemonState> {

    protected Consumer consumer;

    @SuppressWarnings("unchecked")
    public final T setConsumer(Consumer consumer){
        this.consumer = consumer;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T enter() {
        onEnter();
        return (T) this;
    }

    protected abstract void onEnter();

    protected abstract void onExit();

    protected final void transition(DaemonState next) {
        onExit();
        next.setConsumer(consumer);
        consumer.consume(new Runnable() {
            @Override
            public void run() {
                next.enter();
            }
        });
    }
}

