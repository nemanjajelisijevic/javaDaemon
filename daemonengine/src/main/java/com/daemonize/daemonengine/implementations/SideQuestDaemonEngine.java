package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.DaemonState;
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

public class SideQuestDaemonEngine extends BaseDaemonEngine<SideQuestDaemonEngine> implements SideQuestDaemon<SideQuestDaemonEngine> {

  private SideQuest currentSideQuest;

  private Lock sideQuestLock = new ReentrantLock();
  private Condition sideQuestCondition = sideQuestLock.newCondition();

  public SideQuestDaemonEngine(){
    super();
  }

  public <T> SideQuest<T> setSideQuest(Consumer consumer, final Quest<T> sideQuest) {
    setSideQuest((SideQuest) new SideQuest() {
      @Override
      public T pursue() throws Exception {
        return sideQuest.pursue();
      }
    }.setConsumer(consumer));
    return getSideQuest();
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
  protected void initThread() {
    daemonThread = new Thread(new Runnable() {
      @Override
      public void run() {
//        sideQuestLock.lock();
//        try {
//          while (currentSideQuest == null)
//            sideQuestCondition.await();
//        } catch (InterruptedException e) {
//        } finally {
//          sideQuestLock.unlock();
//        }

        loop();
      }
    });
    daemonThread.setName(getName());
    setState(DaemonState.INITIALIZING);
    if (uncaughtExceptionHandler != null)
      daemonThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
    daemonThread.start();
  }

  @Override
  protected void runQuest(BaseQuest quest) {
    setState(quest.getState());
    if(!quest.run()) {
      sideQuestLock.lock();
      try {

        currentSideQuest = null;

        if (quest instanceof InterruptibleQuest)
          ((InterruptibleQuest) quest).getOnInterruptRunnable().run();

        while (currentSideQuest == null) {
          setState(DaemonState.IDLE);
          sideQuestCondition.await();
        }

      } catch (InterruptedException e) {
      } finally {
        sideQuestLock.unlock();
      }
    }
  }

  @Override
  public synchronized SideQuestDaemonEngine start() {
    super.start();
    return this;
  }

  @Override
  public synchronized void stop() {
    sideQuestLock.lock();
    sideQuestCondition.signal();
    sideQuestLock.unlock();
    super.stop();
  }
}
