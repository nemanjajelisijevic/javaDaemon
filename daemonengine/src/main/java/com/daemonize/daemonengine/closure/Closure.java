package com.daemonize.daemonengine.closure;


@FunctionalInterface
public interface Closure<T> {
    void onReturn(Return<T> ret);
}
