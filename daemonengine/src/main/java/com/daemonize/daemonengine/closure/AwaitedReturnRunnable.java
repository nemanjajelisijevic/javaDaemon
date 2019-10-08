package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonBinarySemaphore;

public class AwaitedReturnRunnable<T> extends ReturnRunnable<T> {

    private DaemonBinarySemaphore semaphore;

    public AwaitedReturnRunnable(DaemonBinarySemaphore semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        super.run();
        semaphore.go();
    }
}
