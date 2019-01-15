package com.daemonize.daemondevapp.appstate;

import com.daemonize.daemonengine.consumer.Consumer;


public abstract class DaemonState {

    protected Consumer consumer;

    public final DaemonState setConsumer(Consumer consumer){
        this.consumer = consumer;
        return this;
    }

    public abstract void enter();

    protected abstract void onExit();

    protected final void transit(DaemonState next) {
        onExit();
        next.setConsumer(consumer);
        consumer.consume(()->next.enter());
    }
}

