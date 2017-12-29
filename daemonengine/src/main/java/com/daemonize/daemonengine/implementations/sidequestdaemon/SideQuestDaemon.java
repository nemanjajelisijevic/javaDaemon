package com.daemonize.daemonengine.implementations.sidequestdaemon;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.quests.SideQuest;

public interface SideQuestDaemon extends Daemon {
  <T> void setSideQuest(SideQuest<T> quest);
  <T> SideQuest<T> getSideQuest();
}
