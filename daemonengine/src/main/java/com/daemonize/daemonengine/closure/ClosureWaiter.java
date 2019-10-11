package com.daemonize.daemonengine.closure;

public interface ClosureWaiter {
    void markAwait();
    void clear();
    void awaitClosure() throws InterruptedException;
}