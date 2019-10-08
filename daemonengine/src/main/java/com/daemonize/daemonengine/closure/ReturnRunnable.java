package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class ReturnRunnable<T> implements Runnable {

  private Closure<T> closure;
  private Return<T> ret;

  public ReturnRunnable() {
    this.ret = new Return<>();
  }

  public ReturnRunnable<T> setClosure(Closure<T> closure) {
    this.closure = closure;
    return this;
  }

  public ReturnRunnable<T> setResult(T result) {
    ret.setResult(result);
    return this;
  }

  public ReturnRunnable<T> setError(Exception error, String methodName) {
    ret.setError(
            error,
            "\nDaemon: "
                    + DaemonUtils.tag()
                    + "method '" + methodName + "' threw an exception:\n"
                    + error.getClass().getCanonicalName()
                    + ": "
                    + error.getMessage()
    );
    return this;
  }

  @Override
  public void run() {
    closure.onReturn(ret);
  }
}
