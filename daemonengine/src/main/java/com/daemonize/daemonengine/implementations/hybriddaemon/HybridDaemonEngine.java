package com.daemonize.daemonengine.implementations.hybriddaemon;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.implementations.mainquestdaemon.MainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemon;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;

public class HybridDaemonEngine extends MainQuestDaemonEngine implements SideQuestDaemon {

  private SideQuest sideQuest;

  public <T> void setSideQuest(SideQuest<T> quest) {
    this.sideQuest = quest;
  }

  @Override
  public void pursueQuest(MainQuest quest) {
    addMainQuest(quest);
    if (getState().equals(DaemonState.STOPPED)) {
      start();
    } else if (getState().equals(DaemonState.SIDE_QUEST) && !daemonThread.isInterrupted()) {
      daemonThread.interrupt();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public<T> SideQuest<T> getSideQuest() {
    return sideQuest;
  }

  @Override
  protected Quest getQuest() {
    Quest ret = super.getQuest();
    if (ret == null) {
      ret = sideQuest;
    }
    return ret;
  }
}
