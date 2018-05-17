package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class MainQuest<T> extends Quest<T> {

  private Closure<T> closure;

  public MainQuest() {
    this.state = DaemonState.MAIN_QUEST;
  }

  public MainQuest(Closure<T> closure){
    this();
    this.closure = closure;
    this.returnRunnable = new ReturnRunnable<>(closure);
  }

  @Override
  public final void run() {
    try {
      T result = pursue();
      if (!Thread.currentThread().isInterrupted() && result != null) {
        if (!setResultAndUpdate(result)) {
          System.err.println(DaemonUtils.tag() + description + ": Could not enqueue result to consumer's event queue.");
        }
      }
    } catch (InterruptedException ex) {
        //System.out.println(DaemonUtils.tag() + description + " interrupted.");
    } catch (Exception ex) {
        if (getIsVoid())
            returnRunnable = new ReturnRunnable<>(new Closure<T>() {
              @Override
              public void onReturn(Return<T> ret) {
                ret.get();
              }
            });
        if (!setErrorAndUpdate(ex))
            System.err.println(DaemonUtils.tag() + description + ": Could not enqueue error to consumer's event queue.");
    }
  }
}
