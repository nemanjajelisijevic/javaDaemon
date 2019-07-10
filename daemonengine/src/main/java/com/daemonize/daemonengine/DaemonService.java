package com.daemonize.daemonengine;

public interface DaemonService<D extends DaemonService> {
    D start();
    void stop();
    D setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);
}
