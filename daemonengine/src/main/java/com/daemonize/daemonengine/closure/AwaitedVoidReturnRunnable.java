package com.daemonize.daemonengine.closure;

public class AwaitedVoidReturnRunnable extends VoidReturnRunnable {

    private ClosureExecutionWaiter closureExecutionWaiter;

    public AwaitedVoidReturnRunnable(ClosureExecutionWaiter closureExecutionWaiter, Runnable retRun) {
        super(retRun);
        this.closureExecutionWaiter = closureExecutionWaiter;
    }

    @Override
    public void run() {
        super.run();
        closureExecutionWaiter.endClosureWait();
    }
}
