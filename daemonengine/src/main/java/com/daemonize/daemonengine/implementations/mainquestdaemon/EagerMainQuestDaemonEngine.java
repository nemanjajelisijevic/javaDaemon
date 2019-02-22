package com.daemonize.daemonengine.implementations.mainquestdaemon;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.Quest;

import java.util.concurrent.locks.Condition;

public final class EagerMainQuestDaemonEngine extends MainQuestDaemonEngine {

  private final Condition mainQuestAvailable = mainQuestLock.newCondition();

  public EagerMainQuestDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  @Override
  protected boolean addMainQuest(MainQuest quest) {
    boolean ret;
    mainQuestLock.lock();
    ret = mainQuestQueue.add(quest);//TODO check ret of this expression
    mainQuestAvailable.signal();
    mainQuestLock.unlock();
    return ret;
  }

  @Override
  public EagerMainQuestDaemonEngine setName(String name) {
    super.setName(name);
    return this;
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
      //System.out.println(DaemonUtils.tag() + " Waiting on a quest interrupted");
    } finally {
      mainQuestLock.unlock();
    }
    return ret;
  }
}
