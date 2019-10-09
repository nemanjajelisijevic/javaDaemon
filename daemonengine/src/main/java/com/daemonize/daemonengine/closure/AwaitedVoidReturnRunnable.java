package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class AwaitedVoidReturnRunnable extends AwaitedReturnRunnable<Void> {

    private Runnable retRun;

    public AwaitedVoidReturnRunnable(ClosureWaiter closureWaiter) {
        super(closureWaiter);
    }

    public AwaitedVoidReturnRunnable setRetRun(Runnable retRun) {
        this.retRun = retRun;
        return this;
    }

    @Override
    public void run() {
        retRun.run();
        closureWaiter.clear();
    }
}
