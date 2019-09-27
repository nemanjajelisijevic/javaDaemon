package com.daemonize.daemonengine.closure;


@FunctionalInterface
public interface Closure<T> extends BareClosure<Return<T>> {
    void onReturn(Return<T> ret);
}
