package com.daemonize.daemonengine.closure;

public interface ClosureWaiter {
    void clear();
    void awaitClosure(Runnable action) throws InterruptedException;
}