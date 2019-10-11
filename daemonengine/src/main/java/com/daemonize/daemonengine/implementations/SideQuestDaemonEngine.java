package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.SideQuestDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.InterruptibleQuest;
import com.daemonize.daemonengine.quests.InterruptibleSideQuest;
import com.daemonize.daemonengine.quests.InterruptibleSleepSideQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.BaseQuest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SideQuestDaemonEngine extends BaseDaemonEngine<SideQuestDaemonEngine> implements SideQuestDaemon<SideQuestDaemonEngine>, EagerDaemon<SideQuestDaemonEngine> {

  private SideQuest currentSideQuest;

  private Lock sideQuestLock = new ReentrantLock();
  private Condition sideQuestCondition = sideQuestLock.newCondition();

  public SideQuestDaemonEngine(){
    super();
  }

  public <T, Q extends SideQuest<T>> Q setSideQuest(Consumer consumer, final Q sideQuest) {
    setSideQuest(sideQuest.setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public void setSideQuest(SideQuest quest) {
    sideQuestLock.lock();
    currentSideQuest = quest;
    sideQuestCondition.signal();
    sideQuestLock.unlock();
  }

  @Override
  public SideQuest getSideQuest() {
    return currentSideQuest;
  }

  @Override
  protected BaseQuest getQuest() {

    sideQuestLock.lock();
    try {
      while (currentSideQuest == null) {
        setState(DaemonState.IDLE);
        sideQuestCondition.await();
      }
    } catch (InterruptedException ex) {}
    finally {
      sideQuestLock.unlock();
    }

    return currentSideQuest;
  }

  @Override
  public SideQuestDaemonEngine clear() {
    return this;
  }

  @Override
  protected boolean runQuest(BaseQuest quest) {
    setState(quest.getState());
    if(!quest.run()) {
      sideQuestLock.lock();
      try {

        currentSideQuest = null; //TODO check if nulling is neccessary
        while (currentSideQuest == null) {
          setState(DaemonState.IDLE);
          sideQuestCondition.await();
        }

      } catch (InterruptedException e) {

      } finally {
        sideQuestLock.unlock();
        return false;
      }
    }
    return true;
  }

  @Override
  protected void cleanUp() {
    sideQuestLock.lock();
    currentSideQuest = null;
    sideQuestLock.unlock();
  }

  @Override
  public void stop() {
    sideQuestLock.lock();
    sideQuestCondition.signal();
    sideQuestLock.unlock();
    super.stop();
  }

  @Override
  public SideQuestDaemonEngine interrupt() {
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
  public SideQuestDaemonEngine clearAndInterrupt() {
    sideQuestLock.lock();
    currentSideQuest = null;
    sideQuestLock.unlock();
    return interrupt();
  }
}
