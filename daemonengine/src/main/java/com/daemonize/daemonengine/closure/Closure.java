package com.daemonize.daemonengine.closure;

public abstract class Closure<T> implements Runnable {

  protected volatile T result;
  protected volatile Exception error;

  @SuppressWarnings("unchecked")
  public <K extends Closure<T>> K setResult(T result) {
    this.result = result;
    return (K) this;
  }

  @SuppressWarnings("unchecked")
  public <K extends Closure> K setError(Exception error) {
    this.error = error;
    return (K) this;
  }

  @Override
  public void run() {
    onReturn();
  }

  public abstract void onReturn();

}
