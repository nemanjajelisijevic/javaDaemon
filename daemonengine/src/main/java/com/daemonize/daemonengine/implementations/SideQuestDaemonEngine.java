package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.SideQuestDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.BaseQuest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SideQuestDaemonEngine extends BaseDaemonEngine<SideQuestDaemonEngine> implements SideQuestDaemon<SideQuestDaemonEngine>, EagerDaemon<SideQuestDaemonEngine> {

  private volatile SideQuest currentSideQuest;
  private final Lock sideQuestLock = new ReentrantLock();
  private final Condition sideQuestCondition = sideQuestLock.newCondition();

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

    if (getState().equals(DaemonState.SIDE_QUEST))
      clearAndInterrupt();
    else {
      if (currentSideQuest == null)
        sideQuestCondition.signal();
    }

    currentSideQuest = quest;
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
        setDaemonState(DaemonState.IDLE);
        sideQuestCondition.await();
      }
    } catch (InterruptedException ex) {

    } finally {
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
    if(!quest.run()) {
      sideQuestLock.lock();
      currentSideQuest = null;
      sideQuestLock.unlock();
    }
    return true;
  }

  //  @Override
//  protected void cleanUp() {
//    sideQuestLock.lock();
//    currentSideQuest = null;
//    sideQuestLock.unlock();
//  }

  @Override
  public void stop() {
    sideQuestLock.lock();
    if (currentSideQuest == null)
        sideQuestCondition.signal();
    sideQuestLock.unlock();
    super.stop();
  }

  @Override
  public SideQuestDaemonEngine interrupt() {
    if (!daemonState.equals(DaemonState.STOPPED) && !daemonState.equals(DaemonState.IDLE)) {
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
