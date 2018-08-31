package com.daemonize.daemonengine.utils;

import com.daemonize.daemonengine.consumer.Consumer;

public class DaemonLatch {


    private Consumer consumer;
    private int daemonCounter;
    private Runnable latchCallback;

    public DaemonLatch(Consumer consumer, int daemonCount) {
        if (daemonCount < 0)
            throw new IllegalArgumentException("Argument daemonCount can't be less than 0.");
        this.consumer = consumer;
        daemonCounter = daemonCount;
    }

    public int getCount() {
        return daemonCounter;
    }

    public DaemonLatch setLatchCallback(Runnable latchCallback) {
        this.latchCallback = latchCallback;
        return this;
    }

    public DaemonLatch subscribe() {
        daemonCounter++;
        return this;
    }

    public DaemonLatch unsubscribe() {

        daemonCounter--;
        if (daemonCounter == 0) {
            if (latchCallback == null) throw new IllegalStateException("Callback is null!");
            consumer.consume(latchCallback); //TODO Handle null ptr exc
        }
        return this;
    }

}
