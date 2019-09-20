package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;

public abstract class BaseQuest<T, Q extends BaseQuest<T, Q>> implements Quest<T> {

  protected DaemonState state;
  protected String description = "";
  protected ReturnRunnable<T> returnRunnable;
  protected Consumer consumer;

  public BaseQuest() {
    this.returnRunnable = new ReturnRunnable<>();
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

  //************** METHODS TO UPDATE THE CONSUMER **************************************************/

  public boolean setResultAndUpdate(T result) {
    return consumer.consume(returnRunnable.setResult(result));
  }

  public boolean setErrorAndUpdate(Exception error) {
    return consumer.consume(returnRunnable.setError(error, description));
  }

  //************************** Return type should be void *****************************************/

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
