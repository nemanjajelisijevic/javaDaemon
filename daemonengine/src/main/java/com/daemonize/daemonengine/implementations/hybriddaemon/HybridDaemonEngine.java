package com.daemonize.daemonengine.implementations.hybriddaemon;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.MainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemon;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.SideQuest;

public class HybridDaemonEngine extends MainQuestDaemonEngine implements SideQuestDaemon {

  private SideQuest sideQuest;

  public HybridDaemonEngine(Consumer consumer) {
    super(consumer);
  }

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
}
