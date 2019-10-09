package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class AwaitedReturnRunnable<T> extends ReturnRunnable<T> {

    protected ClosureWaiter closureWaiter;

    public AwaitedReturnRunnable(ClosureWaiter closureWaiter) {
        super();
        this.closureWaiter = closureWaiter;
    }

    @Override
    public void run() {
        super.run();
        closureWaiter.clear();
    }
}
