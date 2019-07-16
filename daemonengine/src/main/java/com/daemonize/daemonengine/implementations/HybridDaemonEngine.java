package com.daemonize.daemonengine.implementations;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.SideQuestDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;

public class HybridDaemonEngine extends MainQuestDaemonBaseEngine<HybridDaemonEngine> implements SideQuestDaemon<HybridDaemonEngine> {

  private SideQuest sideQuest;

  public HybridDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  public <T, Q extends SideQuest<T>> Q setSideQuest(Consumer consumer, final Q sideQuest) {
    setSideQuest(sideQuest.setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public void setSideQuest(SideQuest quest) {
    this.sideQuest = quest;
  }

  @Override
  public boolean pursueQuest(MainQuest quest) {
    boolean ret = addMainQuest(quest);
/*    if (getState().equals(DaemonState.STOPPED)) {//TODO check dis
      start();
    } else */
    if (getState().equals(DaemonState.SIDE_QUEST) && !daemonThread.isInterrupted()) {
      daemonThread.interrupt();
    }
    return ret;
  }

  @Override
  public HybridDaemonEngine setName(String name) {
    super.setName(name);
    return this;
  }

  @Override
  public SideQuest getSideQuest() {
    return sideQuest;
  }

  @Override
  protected BaseQuest getQuest() {
    BaseQuest ret = super.getQuest();
    if (ret == null)
      ret = sideQuest;
    return ret;
  }

  @Override
  public HybridDaemonEngine clear() {
    return super.clear();
  }

  @Override
  public HybridDaemonEngine setConsumer(Consumer consumer) {
    return super.setConsumer(consumer);
  }

  @Override
  public HybridDaemonEngine start() {
    return super.start();
  }

  @Override
  public HybridDaemonEngine queueStop() {
    return super.queueStop();
  }
}
