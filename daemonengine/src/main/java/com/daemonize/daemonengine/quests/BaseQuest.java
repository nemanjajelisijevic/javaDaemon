package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;

public abstract class BaseQuest<T, Q extends BaseQuest<T, Q>> implements Quest<T> {

  @FunctionalInterface
  public interface DaemonStateSetter {
    void setState(DaemonState state);
  }

  protected DaemonStateSetter daemonStateSetter;
  protected String description = "";

  protected ReturnRunnable<T> returnRunnable;
  protected Consumer consumer;

  protected ClosureExecutionWaiter closureExecutionWaiter = new ClosureExecutionWaiter() {

    @Override
    public void endClosureWait() {}

    @Override
    public void awaitClosureExecution(Runnable updateConsumerAction) throws InterruptedException {
      updateConsumerAction.run();
    }
  };

  public Q setDaemonStateSetter(DaemonStateSetter daemonStateSetter) {
    this.daemonStateSetter = daemonStateSetter;
    return (Q) this;
  }

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

  public abstract T pursue() throws Exception;

  public boolean setResultAndUpdate(T result) {
    return consumer.consume(returnRunnable.setResult(result));
  }

  public boolean setErrorAndUpdate(Exception error) {
    error.printStackTrace();//TODO DEV
    return consumer.consume(returnRunnable.setError(error, description));
  }

  public boolean getIsVoid() {
    return false;
  }

  public abstract boolean run();

}
