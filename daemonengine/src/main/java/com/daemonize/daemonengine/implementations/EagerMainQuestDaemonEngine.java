package com.daemonize.daemonengine.implementations;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;

import java.util.List;
import java.util.concurrent.locks.Condition;

public class EagerMainQuestDaemonEngine extends MainQuestDaemonBaseEngine<EagerMainQuestDaemonEngine> implements EagerDaemon<EagerMainQuestDaemonEngine> {

  private final Condition mainQuestAvailable = mainQuestLock.newCondition();

  public EagerMainQuestDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  @Override
  public boolean addMainQuest(MainQuest quest) {
    boolean ret;
    mainQuestLock.lock();
    ret = mainQuestQueue.add(quest);
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
  protected BaseQuest getQuest() {

    BaseQuest ret = null;
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

  @Override
  public EagerMainQuestDaemonEngine interrupt() {
    if (!state.equals(DaemonState.STOPPED) && !state.equals(DaemonState.IDLE)) {
      if (daemonThread != null
              && !Thread.currentThread().equals(daemonThread)
              && daemonThread.isAlive()) {
        daemonThread.interrupt();
      }
    }
    return this;
  }

  @Override
  public EagerMainQuestDaemonEngine clearAndInterrupt() {
      mainQuestLock.lock();
      mainQuestQueue.clear();
      mainQuestLock.unlock();
      interrupt();
      return this;
  }

  @Override
  public EagerMainQuestDaemonEngine queueStop() {
      return super.queueStop(this);
  }
}
