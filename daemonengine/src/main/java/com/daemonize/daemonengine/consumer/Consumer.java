package com.daemonize.daemonengine.consumer;


public interface Consumer {
    boolean consume(Runnable runnable);
}
