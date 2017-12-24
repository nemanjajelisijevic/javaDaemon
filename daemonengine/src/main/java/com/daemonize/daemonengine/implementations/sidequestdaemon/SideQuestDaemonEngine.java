package com.daemonize.daemonengine.implementations.sidequestdaemon;


import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.Quest;

public class SideQuestDaemonEngine extends BaseDaemonEngine implements SideQuestDaemon {

  private SideQuest sideQuest;

  public void setSideQuest(SideQuest quest) {
    this.sideQuest = quest;
  }

  @Override
  public SideQuest getSideQuest() {
    return sideQuest;
  }

  @Override
  protected Quest getQuest() {
    return sideQuest;
  }


}
