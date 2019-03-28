package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.utils.DaemonUtils;


public abstract class SideQuest<T> extends BaseQuest<T, SideQuest<T>> {

  public SideQuest() {
    super();
    this.state = DaemonState.SIDE_QUEST;
  }

  @SuppressWarnings("unchecked")
  public SideQuest<T> setClosure(Closure<T> closure) {
    this.returnRunnable.setClosure(closure);
    return this;
  }

  @Override
  public void run(){
    try {

      T result = pursue();
      if (result != null) {
        setResultAndUpdate(result);
      }

//      if (sleepInterval > 0) {
//        Thread.sleep(sleepInterval);
//      }

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
