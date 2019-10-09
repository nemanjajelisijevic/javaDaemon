package com.daemonize.daemonengine.closure;

public interface ClosureWaiter {
    void reset();
    void clear();
    void awaitClosure() throws InterruptedException;
}