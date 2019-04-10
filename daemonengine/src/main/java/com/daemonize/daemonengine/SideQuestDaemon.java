package com.daemonize.daemonengine;

import com.daemonize.daemonengine.quests.SideQuest;

public interface SideQuestDaemon<D extends SideQuestDaemon> extends Daemon<D> {
  void setSideQuest(SideQuest quest);
  SideQuest getSideQuest();
}
