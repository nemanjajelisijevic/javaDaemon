package com.daemonize.daemonengine.consumer;


public interface Consumer {
    void queueRunnable(Runnable runnable);
}
