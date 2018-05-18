package com.daemonize.daemonengine.implementations.mainquestdaemon;


import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.StopMainQuest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainQuestDaemonEngine extends BaseDaemonEngine {

  protected Queue<MainQuest> mainQuestQueue = new LinkedList<>();
  protected final Lock mainQuestLock = new ReentrantLock();

  public MainQuestDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  protected void addMainQuest(MainQuest quest) {
    mainQuestLock.lock();
    mainQuestQueue.add(quest);
    mainQuestLock.unlock();
  }

  public void pursueQuest(MainQuest quest) {
    addMainQuest(quest);
    if (getState().equals(DaemonState.STOPPED)) {
      start();
    }
  }

  //returns null
  @Override
  protected Quest getQuest() {
    Quest ret = null;
    mainQuestLock.lock();
    if (!mainQuestQueue.isEmpty()) {
      ret = mainQuestQueue.poll();
    }
    mainQuestLock.unlock();
    return ret;
  }

  @Override
  public void queueStop() {
    addMainQuest(new StopMainQuest(this));
  }
}
