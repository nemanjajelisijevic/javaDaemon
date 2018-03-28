package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;

public abstract class Quest<T> implements Runnable {

  protected DaemonState state;
  protected String description = "";
  protected ReturnRunnable<T> returnRunnable;
  private Consumer consumer;

  public String getDescription() {
    return description;
  }

  @SuppressWarnings("unchecked")
  public <K extends Quest> K setDescription(String description) {
    this.description = description;
    return (K) this;
  }

  public ReturnRunnable<T> getReturnRunnable() {
    return returnRunnable;
  }

  public Quest<T> setConsumer(Consumer consumer) {
    this.consumer = consumer;
    return this;
  }

  public Consumer getConsumer() {
    return consumer;
  }

  public DaemonState getState() {
    return state;
  }

  protected abstract T pursue() throws Exception;

  //************** METHODS TO UPDATE MAIN THREAD **************************************************/

  public final void setResultAndUpdate(T result) {
    consumer.enqueue(returnRunnable.setResult(result));
  }

  public void setErrorAndUpdate(Exception error) {
    consumer.enqueue(returnRunnable.setError(error));
  }

  //************************** Return type should be void *****************************************/

  private boolean isVoid = false;

  @SuppressWarnings("unchecked")
  public <K extends Quest> K setVoid() {
    this.isVoid = true;
    return (K) this;
  }

  public boolean getIsVoid() {
    return isVoid;
  }

  @Override
  public abstract void run();

}
