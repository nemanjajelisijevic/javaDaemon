package com.daemonize.game.app;

public interface DaemonApp<T extends DaemonApp> {
    T run();
}
