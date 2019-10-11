package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedReturnRunnable;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureWaiter;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.utils.DaemonUtils;


public abstract class SideQuest<T> extends BaseQuest<T, SideQuest<T>> {

  protected ClosureWaiter closureWaiter;

  public SideQuest() {
    this(null);
  }

  public SideQuest(ClosureWaiter closureWaiter) {
    if (closureWaiter != null) {
      this.closureWaiter = closureWaiter;
      this.returnRunnable = new AwaitedReturnRunnable<T>(closureWaiter);
    } else {
      this.returnRunnable = new ReturnRunnable<T>();
    }
    this.state = DaemonState.SIDE_QUEST;
  }

  public SideQuest<T> setClosure(Closure<T> closure) {
    this.returnRunnable.setClosure(closure);
    return this;
  }

  @Override
  public boolean run(){
    try {
      T result = pursue();
      if (result != null) {
        closureWaiter.markAwait();
        setResultAndUpdate(result);
        closureWaiter.awaitClosure();
      }
      return true;
    } catch (InterruptedException ex) {
      System.out.println(DaemonUtils.tag() + description + " interrupted.");
      return true;
    } catch (Exception ex) {
      setErrorAndUpdate(ex);
      return false;
    }
  }
}
