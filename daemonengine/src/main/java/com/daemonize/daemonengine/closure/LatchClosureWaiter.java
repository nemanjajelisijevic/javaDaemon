package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonLatch;

public class LatchClosureWaiter implements com.daemonize.daemonengine.closure.ClosureWaiter {

    private DaemonLatch daemonLatch;

    public LatchClosureWaiter(DaemonLatch daemonLatch) {
        this.daemonLatch = daemonLatch;
    }

    public DaemonLatch getDaemonLatch() {
        return daemonLatch;
    }

    @Override
    public void reset() {
        daemonLatch.reset();
    }

    @Override
    public void clear() {
        daemonLatch.clear();
    }

    @Override
    public void awaitClosure() throws InterruptedException {
        daemonLatch.await();
    }
}