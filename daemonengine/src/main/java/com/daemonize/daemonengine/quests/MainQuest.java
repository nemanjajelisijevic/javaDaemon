package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedReturnRunnable;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureWaiter;
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
    this.state = DaemonState.MAIN_QUEST;
    this.returnRunnable = new ReturnRunnable<T>();
  }

  public MainQuest(Closure<T> closure, ClosureWaiter closureWaiter){
    this.state = DaemonState.MAIN_QUEST;
    if (closureWaiter != null) {
        this.closureWaiter = closureWaiter;
        this.returnRunnable = new AwaitedReturnRunnable<T>(closureWaiter).setClosure(closure);
    } else {
        this.returnRunnable = new ReturnRunnable<T>().setClosure(closure);
    }
  }

  @Override
  public boolean run() {
    try {
        result = pursue();
        if (!Thread.currentThread().isInterrupted() && result != null) {
            //closureWaiter.markAwait();
            //setResultAndUpdate(result);
            closureWaiter.awaitClosure(setResultAction);
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
