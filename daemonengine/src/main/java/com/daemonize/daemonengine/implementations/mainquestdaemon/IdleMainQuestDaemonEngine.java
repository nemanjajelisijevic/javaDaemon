package com.daemonize.daemonengine.implementations.mainquestdaemon;

import android.util.Log;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.Quest;

import java.util.concurrent.locks.Condition;

public final class IdleMainQuestDaemonEngine extends MainQuestDaemonEngine {

  private Condition mainQuestAvailable = mainQuestLock.newCondition();

  @Override
  protected void addMainQuest(MainQuest quest) {
    mainQuestLock.lock();
    mainQuestQueue.add(quest);//TODO check ret of this expression
    mainQuestAvailable.signal();
    mainQuestLock.unlock();
  }

  @Override
  protected Quest getQuest() {

    Quest ret = null;
    try {
      mainQuestLock.lock();
      while (mainQuestQueue.isEmpty()) {
        setState(DaemonState.IDLE);
        mainQuestAvailable.await();
      }
      ret = mainQuestQueue.poll();
    } catch (InterruptedException ex) {
      Log.i(Thread.currentThread().getName(), "Waiting on a quest interrupted");
    } finally {
      mainQuestLock.unlock();
    }
    return ret;
  }
}
