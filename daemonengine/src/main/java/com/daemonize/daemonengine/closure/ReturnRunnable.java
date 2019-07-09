package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class ReturnRunnable<T> implements Runnable {

  private Closure<T> closure;
  private Return<T> ret;

  public ReturnRunnable() {
    this.ret = new Return<>();
  }

  public ReturnRunnable(Closure<T> closure) {
    this();
    this.closure = closure;
  }

  public ReturnRunnable(Closure<T> closure, T result) {
    this.closure = closure;
    this.ret = new Return<>(result);
  }

  public ReturnRunnable<T> setClosure(Closure<T> closure) {
    this.closure = closure;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <K extends ReturnRunnable<T>> K setResult(T result) {
    ret.setResult(result);
    return (K) this;
  }

  @SuppressWarnings("unchecked")
  public <K extends ReturnRunnable> K setError(Exception error, String methodName) {
    ret.setError(
            error,
            "\nDaemon: "
                    + DaemonUtils.tag()
                    + "method '" + methodName + "' threw an exception:\n"
                    + error.getClass().getCanonicalName()
                    + ": "
                    + error.getMessage()
    );
    return (K) this;
  }

  @Override
  public void run() {
    closure.onReturn(ret);
  }

  public ReturnRunnable<T> setInterrupted() {
      ret.setInterrupted();
      return this;
  }

}
