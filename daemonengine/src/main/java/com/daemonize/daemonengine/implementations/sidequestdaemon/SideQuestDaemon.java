package com.daemonize.daemonengine.implementations.sidequestdaemon;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.quests.SideQuest;

public interface SideQuestDaemon extends Daemon {
  <K extends SideQuestDaemon> K setSideQuest(SideQuest quest);
  SideQuest getSideQuest();
}
