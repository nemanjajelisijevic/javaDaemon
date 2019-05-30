package com.daemonize.daemonengine;

public interface DaemonService<D extends DaemonService> {
    D start();
    void stop();
}
