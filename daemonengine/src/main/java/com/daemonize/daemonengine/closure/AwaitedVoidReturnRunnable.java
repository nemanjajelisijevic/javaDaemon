package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class AwaitedVoidReturnRunnable extends VoidReturnRunnable {

    private ClosureWaiter closureWaiter;

    public AwaitedVoidReturnRunnable(ClosureWaiter closureWaiter, Runnable retRun) {
        super(retRun);
        this.closureWaiter = closureWaiter;
    }

    @Override
    public void run() {
        super.run();
        closureWaiter.clear();
    }
}
