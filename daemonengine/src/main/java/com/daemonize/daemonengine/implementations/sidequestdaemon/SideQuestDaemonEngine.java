package com.daemonize.daemonengine.implementations.sidequestdaemon;


import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.BaseQuest;

public class SideQuestDaemonEngine extends BaseDaemonEngine<SideQuestDaemonEngine> implements SideQuestDaemon {

  private SideQuest sideQuest;

  public SideQuestDaemonEngine(){
    super();
  }

  public void setSideQuest(SideQuest quest) {
    this.sideQuest = quest;
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
}
