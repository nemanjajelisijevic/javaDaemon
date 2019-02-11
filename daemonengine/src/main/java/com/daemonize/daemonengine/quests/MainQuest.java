package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class MainQuest<T> extends Quest<T, MainQuest<T>> {

  protected Closure<T> closure;

  public MainQuest() {
    super();
    this.state = DaemonState.MAIN_QUEST;
  }

  public MainQuest(Closure<T> closure){
    this();
    this.closure = closure;
    this.returnRunnable.setClosure(closure);
  }

  @Override
  public void run() {
    try {
      T result = pursue();
      if (!Thread.currentThread().isInterrupted() && result != null) {
        setResultAndUpdate(result);
      }
    } catch (InterruptedException ex) {
        System.out.println(DaemonUtils.tag() + description + " interrupted.");
    } catch (Exception ex) {
        if (getIsVoid())
            returnRunnable = new ReturnRunnable<>(new Closure<T>() {
              @Override
              public void onReturn(Return<T> ret) {
                ret.get();
              }
            });
        setErrorAndUpdate(ex);
    }
  }
}
