package com.daemonize.daemonengine.closure;

@FunctionalInterface
public interface BareClosure<T> {
    void onReturn(T ret);
}
