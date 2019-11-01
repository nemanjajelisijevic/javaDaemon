package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedReturnRunnable;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class MainQuest<T> extends BaseQuest<T, MainQuest<T>> {

  private T result;
  private Runnable setResultAction = new Runnable() {
      @Override
      public void run() {
        setResultAndUpdate(result);
      }
  };

  MainQuest() {
    this.returnRunnable = new ReturnRunnable<T>();
  }

  public MainQuest(Closure<T> closure, ClosureExecutionWaiter closureExecutionWaiter){
    if (closureExecutionWaiter != null) {
        this.closureExecutionWaiter = closureExecutionWaiter;
        this.returnRunnable = new AwaitedReturnRunnable<T>(closureExecutionWaiter).setClosure(closure);
    } else {
        this.returnRunnable = new ReturnRunnable<T>().setClosure(closure);
    }
  }

  @Override
  public boolean run() {
    try {
        daemonStateSetter.setState(DaemonState.MAIN_QUEST);
        result = pursue();
        if (!Thread.currentThread().isInterrupted() && result != null) {
            daemonStateSetter.setState(DaemonState.AWAITING_CLOSURE);
            closureExecutionWaiter.awaitClosureExecution(setResultAction);
            daemonStateSetter.setState(DaemonState.MAIN_QUEST);
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
