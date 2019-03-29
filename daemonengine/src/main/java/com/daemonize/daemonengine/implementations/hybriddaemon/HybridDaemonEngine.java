package com.daemonize.daemonengine.implementations.hybriddaemon;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.MainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemon;
import com.daemonize.daemonengine.quests.AnonMainQuest;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.daemonengine.quests.VoidQuest;

public class HybridDaemonEngine extends MainQuestDaemonEngine implements SideQuestDaemon {

  private SideQuest sideQuest;

  public HybridDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  public <T> HybridDaemonEngine daemonize(Quest<T> quest, Closure<T> closure) {
    addMainQuest((AnonMainQuest<T>)new AnonMainQuest(quest, closure).setConsumer(getConsumer())); //TODO check ret
    return this;
  }

  public HybridDaemonEngine daemonize(final VoidQuest quest, Runnable closure) {
    addMainQuest((VoidMainQuest)new VoidMainQuest(closure) {
      @Override
      public Void pursue() throws Exception {
        quest.pursue();
        return null;
      }
    }.setConsumer(getConsumer()));
    return this;
  }

  public <T> SideQuest<T> setSideQuest(Consumer consumer, final Quest<T> sideQuest) {
    this.sideQuest = (SideQuest) new SideQuest() {
      @Override
      public T pursue() throws Exception {
        return sideQuest.pursue();
      }
    }.setConsumer(consumer);
    return this.sideQuest;
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

  @Override
  public HybridDaemonEngine queueStop(Daemon daemon) {
    return (HybridDaemonEngine) super.queueStop(daemon);
  }

  @Override
  public HybridDaemonEngine clear() {
    return (HybridDaemonEngine) super.clear();
  }

  @Override
  public HybridDaemonEngine setConsumer(Consumer consumer) {
    return (HybridDaemonEngine) super.setConsumer(consumer);
  }

  @Override
  public HybridDaemonEngine start() {
    return (HybridDaemonEngine)super.start();
  }

  @Override
  public HybridDaemonEngine queueStop() {
    return (HybridDaemonEngine) super.queueStop();
  }
}
