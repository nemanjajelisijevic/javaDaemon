package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedReturnRunnable;
import com.daemonize.daemonengine.closure.ClosureWaiter;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;

public abstract class BaseQuest<T, Q extends BaseQuest<T, Q>> implements Quest<T> {

  protected DaemonState state;
  protected String description = "";

  protected ReturnRunnable<T> returnRunnable;
  protected Consumer consumer;

  protected ClosureWaiter closureWaiter = new ClosureWaiter() {

    @Override
    public void markAwait() {}

    @Override
    public void clear() {}

    @Override
    public void awaitClosure() throws InterruptedException {}
  };

  public String getDescription() {
    return description;
  }

  public Q setDescription(String description) {
    this.description = description;
    return (Q) this;
  }

  public Q setConsumer(Consumer consumer) {
    this.consumer = consumer;
    return (Q) this;
  }

  public Consumer getConsumer() {
    return consumer;
  }

  public DaemonState getState() {
    return state;
  }

  public abstract T pursue() throws Exception;

  public boolean setResultAndUpdate(T result) {
    return consumer.consume(returnRunnable.setResult(result));
  }

  public boolean setErrorAndUpdate(Exception error) {
    return consumer.consume(returnRunnable.setError(error, description));
  }

  public boolean getIsVoid() {
    return false;
  }

  public abstract boolean run();

}
