package com.daemonize.daemonengine.consumer;


public interface Consumer {
    void enqueue(Runnable runnable);
}
