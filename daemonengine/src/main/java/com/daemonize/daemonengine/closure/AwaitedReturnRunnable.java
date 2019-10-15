package com.daemonize.daemonengine.closure;

public class AwaitedReturnRunnable<T> extends ReturnRunnable<T> {

    private ClosureExecutionWaiter closureExecutionWaiter;

    public AwaitedReturnRunnable(ClosureExecutionWaiter closureExecutionWaiter) {
        super();
        this.closureExecutionWaiter = closureExecutionWaiter;
    }

    @Override
    public void run() {
        super.run();
        closureExecutionWaiter.endClosureWait();
    }
}
