package com.daemonize.daemonengine.implementations.mainquestdaemon;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.StopMainQuest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainQuestDaemonEngine extends BaseDaemonEngine<MainQuestDaemonEngine> {

  protected final Queue<MainQuest> mainQuestQueue = new LinkedList<>();
  protected final Lock mainQuestLock = new ReentrantLock();

  public MainQuestDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  public boolean addMainQuest(MainQuest quest) {
    boolean ret;
    mainQuestLock.lock();
    ret = mainQuestQueue.add(quest);
    mainQuestLock.unlock();
    return ret;
  }

  public boolean pursueQuest(MainQuest quest) {
    return addMainQuest(quest);
  }

  //returns null
  @Override
  protected BaseQuest getQuest() {
    BaseQuest ret = null;
    mainQuestLock.lock();
    if (!mainQuestQueue.isEmpty()) {
      ret = mainQuestQueue.poll();
    }
    mainQuestLock.unlock();
    return ret;
  }

  //@Override
  public MainQuestDaemonEngine queueStop(Daemon daemon) {
    addMainQuest(new StopMainQuest(daemon));
    return this;
  }

  @Override
  public void stop() {
    clear();
    super.stop();
  }

  public int queueSize() {
    return mainQuestQueue.size();
  }


  @Override
  public MainQuestDaemonEngine clear() {
    mainQuestLock.lock();
    mainQuestQueue.clear();
    mainQuestLock.unlock();
    return this;
  }
}
