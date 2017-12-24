package com.daemonize.daemonengine.quests;

import android.os.Handler;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;

public abstract class Quest<T> implements Runnable {

  protected DaemonState state;
  protected String description = "";
  protected Closure<T> closure;
  private Handler handler;

  public String getDescription() {
    return description;
  }

  @SuppressWarnings("unchecked")
  public <K extends Quest> K setDescription(String description) {
    this.description = description;
    return (K) this;
  }

  public Closure<T> getClosure() {
    return closure;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  public Handler getHandler() {
    return handler;
  }

  public DaemonState getState() {
    return state;
  }

  protected abstract T pursue() throws Exception;

  //************** METHODS TO UPDATE MAIN_QUEST THREAD **************************************************/

  public final void setResultAndUpdate(T result) {
    handler.post(closure.setResult(result));
  }

  public void setErrorAndUpdate(Exception error) {
    handler.post(closure.setError(error));
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
