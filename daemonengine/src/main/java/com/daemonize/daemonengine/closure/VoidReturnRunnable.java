package com.daemonize.daemonengine.closure;

public class VoidReturnRunnable extends ReturnRunnable<Void> {

    protected Runnable retRun;

    public VoidReturnRunnable(Runnable retRun) {
        this.retRun = retRun;
    }

    @Override
    public ReturnRunnable<Void> setClosure(Closure<Void> closure) {
        throw new IllegalStateException("Method not supported.");
    }

    @Override
    public Closure<Void> getClosure() {
        throw new IllegalStateException("Method not supported.");
    }

    @Override
    public ReturnRunnable<Void> setResult(Void result) {
        throw new IllegalStateException("Method not supported.");
    }

    @Override
    public ReturnRunnable<Void> setError(Exception error, String methodName) {
        throw new IllegalStateException("Method not supported.");
    }

    @Override
    public void run() {
        retRun.run();
    }
}
