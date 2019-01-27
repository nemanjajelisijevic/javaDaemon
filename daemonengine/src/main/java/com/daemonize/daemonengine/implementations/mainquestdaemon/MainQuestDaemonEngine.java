package com.daemonize.daemonengine.implementations.mainquestdaemon;


import com.daemonize.daemonengine.Daemon;
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

public class MainQuestDaemonEngine extends BaseDaemonEngine<MainQuestDaemonEngine> {

  protected Queue<MainQuest> mainQuestQueue = new LinkedList<>();
  protected final Lock mainQuestLock = new ReentrantLock();

  public MainQuestDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  protected boolean addMainQuest(MainQuest quest) {
    boolean ret;
    mainQuestLock.lock();
    ret = mainQuestQueue.add(quest);
    mainQuestLock.unlock();
    return ret;
  }

  public boolean pursueQuest(MainQuest quest) {
    boolean ret = addMainQuest(quest);
    if (getState().equals(DaemonState.STOPPED)) {
      start();
    }
    return ret;
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
  public MainQuestDaemonEngine queueStop() {
    addMainQuest(new StopMainQuest(this));
    return this;
  }
}
