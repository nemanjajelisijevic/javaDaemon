package com.daemonize.game;

public interface Paralyzable<T extends Paralyzable> {
    boolean isParalyzed();
    T setParalyzed(boolean paralyzed);
}
