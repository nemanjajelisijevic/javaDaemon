package com.daemonize.daemonengine.closure;

public interface ClosureExecutionWaiter {
    void endClosureWait();
    void awaitClosureExecution(Runnable updateConsumerAction) throws InterruptedException;
}