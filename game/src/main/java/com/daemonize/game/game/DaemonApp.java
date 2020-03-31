package com.daemonize.game.game;

public interface DaemonApp<T extends DaemonApp> {
    T run();
}
