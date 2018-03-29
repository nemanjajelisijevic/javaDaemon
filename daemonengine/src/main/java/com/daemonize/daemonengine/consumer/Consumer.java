package com.daemonize.daemonengine.consumer;


public interface Consumer {
    boolean enqueue(Runnable runnable);
}
