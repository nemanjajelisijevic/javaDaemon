package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedReturnRunnable;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.utils.DaemonUtils;


public abstract class SideQuest<T> extends BaseQuest<T, SideQuest<T>> {

  protected T result;
  protected Runnable resultRunnable = new Runnable() {
     @Override
     public void run() {
         setResultAndUpdate(result);
     }
  };

  public SideQuest() {
    this(null);
  }

  public SideQuest(ClosureExecutionWaiter closureExecutionWaiter) {
    if (closureExecutionWaiter != null) {
      this.closureExecutionWaiter = closureExecutionWaiter;
      this.returnRunnable = new AwaitedReturnRunnable<T>(closureExecutionWaiter);
    } else {
      this.returnRunnable = new ReturnRunnable<T>();
    }
    //this.state = DaemonState.SIDE_QUEST;
  }

  public SideQuest<T> setClosure(Closure<T> closure) {
    this.returnRunnable.setClosure(closure);
    return this;
  }

  @Override
  public boolean run(){
    try {
      daemonStateSetter.setState(DaemonState.SIDE_QUEST);
      result = pursue();
      if (result != null) {
        daemonStateSetter.setState(DaemonState.AWAITING_CLOSURE);
        closureExecutionWaiter.awaitClosureExecution(resultRunnable);
        daemonStateSetter.setState(DaemonState.SIDE_QUEST);
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
