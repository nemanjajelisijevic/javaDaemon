package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.SideQuestDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.utils.DaemonSemaphore;

public class SideQuestDaemonEngine extends BaseDaemonEngine<SideQuestDaemonEngine> implements SideQuestDaemon<SideQuestDaemonEngine> {

  private SideQuest sideQuest;
  private DaemonSemaphore sideQuestSemaphore = new DaemonSemaphore();

  public SideQuestDaemonEngine(){
    super();
    sideQuestSemaphore.stop();
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
    this.sideQuest = quest;
    sideQuestSemaphore.go();
  }

  @Override
  public SideQuest getSideQuest() {
    return sideQuest;
  }

  @Override
  protected BaseQuest getQuest() {
    return sideQuest;
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
        try {
          sideQuestSemaphore.await();
        } catch (InterruptedException e) {}
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
  public synchronized SideQuestDaemonEngine start() {
    super.start();
    return this;
  }

  @Override
  public synchronized void stop() {
    sideQuestSemaphore.go();
    super.stop();
  }
}
