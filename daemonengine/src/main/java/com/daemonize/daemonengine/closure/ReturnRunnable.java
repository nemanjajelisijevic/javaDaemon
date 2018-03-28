package com.daemonize.daemonengine.closure;

public class ReturnRunnable<T> implements Runnable {

  private Closure<T> closure;
  private Return<T> ret;

  public ReturnRunnable(Closure<T> closure) {
    this.closure = closure;
    this.ret = new Return<>();
  }

  @SuppressWarnings("unchecked")
  public <K extends ReturnRunnable<T>> K setResult(T result) {
    ret.setResult(result);
    return (K) this;
  }

  @SuppressWarnings("unchecked")
  public <K extends ReturnRunnable> K setError(Exception error) {
    ret.setError(error);
    return (K) this;
  }

  @Override
  public void run() {
    closure.onReturn(ret);
  }

}
