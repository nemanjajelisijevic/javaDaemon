package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class MainQuest<T> extends Quest<T> {

  public MainQuest() {
    this.state = DaemonState.MAIN_QUEST;
  }

  public MainQuest(ReturnRunnable<T> returnRunnable){
    this();
    this.returnRunnable = returnRunnable;
  }

  @Override
  public final void run() {
    try {
      T result = pursue();
      if (!Thread.currentThread().isInterrupted() && result != null) {
        setResultAndUpdate(result);
      }
    } catch (Exception ex) {

      if (ex instanceof InterruptedException) {
        System.out.println(DaemonUtils.tag() + description + " interrupted.");
      } else if (!getIsVoid()) {
        setErrorAndUpdate(ex);
      } else {
        System.out.println(DaemonUtils.tag() + "Error in void returning method: " + description + ":");
        ex.printStackTrace();
      }

    }
  }
}
