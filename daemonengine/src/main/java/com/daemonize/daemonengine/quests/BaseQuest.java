package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedReturnRunnable;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonBinarySemaphore;

public abstract class BaseQuest<T, Q extends BaseQuest<T, Q>> implements Quest<T> {

  @FunctionalInterface
  interface ClosureWaiter {
      void awaitClosure() throws InterruptedException;
  }

  protected DaemonState state;
  protected String description = "";
  protected ReturnRunnable<T> returnRunnable;
  protected Consumer consumer;

  private DaemonBinarySemaphore closureWaitingSemaphore;

  protected ClosureWaiter closureWaiter = new ClosureWaiter() {
    @Override
    public void awaitClosure() throws InterruptedException {}
  };

  public BaseQuest() {
    this.returnRunnable = new ReturnRunnable<>();
  }

  public Q setClosureWaitingSemaphore(DaemonBinarySemaphore semaphore) {
    this.returnRunnable = new AwaitedReturnRunnable<T>(semaphore);
    this.closureWaitingSemaphore = closureWaitingSemaphore;
    this.closureWaiter = new ClosureWaiter() {
      @Override
      public void awaitClosure() throws InterruptedException {
        closureWaitingSemaphore.await();
      }
    };
    return (Q) this;
  }

  public String getDescription() {
    return description;
  }

  public Q setDescription(String description) {
    this.description = description;
    return (Q) this;
  }

  public ReturnRunnable<T> getReturnRunnable() {
    return returnRunnable;
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

  private boolean isVoid = false;

  public Q setVoid() {
    this.isVoid = true;
    return (Q) this;
  }

  public boolean getIsVoid() {
    return isVoid;
  }

  public abstract boolean run();

}
