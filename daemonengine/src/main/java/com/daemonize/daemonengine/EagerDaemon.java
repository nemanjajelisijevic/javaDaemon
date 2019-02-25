package com.daemonize.daemonengine;

public interface EagerDaemon<D extends Daemon> extends Daemon<D> {
    D interrupt();
    D clearAndInterrupt();
}
