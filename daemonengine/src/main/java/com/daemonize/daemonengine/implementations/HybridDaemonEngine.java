package com.daemonize.daemonengine.implementations;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.SideQuestDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;

public class HybridDaemonEngine extends MainQuestDaemonBaseEngine<HybridDaemonEngine> implements SideQuestDaemon<HybridDaemonEngine> {

  private SideQuest sideQuest;

  public HybridDaemonEngine(Consumer consumer) {
    super(consumer);
  }

//  public <T> HybridDaemonEngine daemonize(Quest<T> quest, Closure<T> closure) {
//    return daemonize(getConsumer(), quest, closure);
//  }
//
//  public HybridDaemonEngine daemonize(final VoidQuest quest, Runnable closure) {
//    return daemonize(getConsumer(), quest, closure);
//  }
//
//  public HybridDaemonEngine daemonize(final VoidQuest quest) {
//      return daemonize(quest, null);
//  }
//
//  public <T> HybridDaemonEngine daemonize(Consumer consumer, Quest<T> quest, Closure<T> closure) {
//      addMainQuest((AnonMainQuest<T>)new AnonMainQuest(quest, closure).setConsumer(consumer)); //TODO check ret
//      return this;
//  }
//
//  public HybridDaemonEngine daemonize(Consumer consumer, final VoidQuest quest, Runnable closure) {
//     addMainQuest(new VoidMainQuest(closure) {
//         @Override
//         public Void pursue() throws Exception {
//             quest.pursue();
//             return null;
//         }
//     }.setConsumer(consumer));
//     return this;
//  }

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

//  @Override
//  public HybridDaemonEngine queueStop(Daemon daemon) {
//    return super.queueStop(daemon);
//  }

  @Override
  public HybridDaemonEngine clear() {
    return super.clear();
  }

  @Override
  public HybridDaemonEngine setConsumer(Consumer consumer) {
    return super.setConsumer(consumer);
  }

  @Override
  public HybridDaemonEngine start() {
    return super.start();
  }

  @Override
  public HybridDaemonEngine queueStop() {
    return super.queueStop();
  }
}
