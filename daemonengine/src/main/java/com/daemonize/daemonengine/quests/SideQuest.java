package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.utils.DaemonUtils;


public abstract class SideQuest<T> extends Quest<T> {

  private int sleepInterval;

  @SuppressWarnings("unchecked")
  public <K extends SideQuest> K setSleepInterval(int milliseconds) {
    this.sleepInterval = milliseconds;
    return (K) this;
  }

  public SideQuest() {
    this.state = DaemonState.SIDE_QUEST;
  }

  @SuppressWarnings("unchecked")
  public SideQuest<T> setClosure(Closure<T> closure) {
    this.closure = closure;
    return this;
  }

  @Override
  public final void run(){
    try {

      T result = pursue();
      if (!Thread.currentThread().isInterrupted() && result != null) {
        setResultAndUpdate(result);
      }

      if (sleepInterval > 0) {
        Thread.sleep(sleepInterval);
      }

    } catch (InterruptedException ex) {
      //System.out.println(DaemonUtils.tag() + description + " interrupted.");
    } catch (Exception ex) {
      if (!getIsVoid()) {
        setErrorAndUpdate(ex);
      } else {
        System.out.println(DaemonUtils.tag() + "Error in void returning method: " + description + ":");
        ex.printStackTrace();
      }
    }
  }
}
