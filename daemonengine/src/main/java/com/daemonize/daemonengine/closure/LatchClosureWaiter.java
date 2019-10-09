package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.utils.DaemonLatch;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class LatchClosureWaiter implements ClosureWaiter{

    private DaemonLatch closureWaiterLatch;

    public LatchClosureWaiter(DaemonLatch closureWaiterLatch) {
        this.closureWaiterLatch = closureWaiterLatch;
    }

    @Override
    public void reset() {
        closureWaiterLatch.reset();
    }

    @Override
    public void clear() {
        closureWaiterLatch.clear();
    }

    @Override
    public void awaitClosure() throws InterruptedException {
        closureWaiterLatch.await();
    }
}