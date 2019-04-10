package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.SideQuestDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.BaseQuest;

public class SideQuestDaemonEngine extends BaseDaemonEngine<SideQuestDaemonEngine> implements SideQuestDaemon<SideQuestDaemonEngine> {

  private SideQuest sideQuest;

  public SideQuestDaemonEngine(){
    super();
  }

  public <T> SideQuest<T> setSideQuest(Consumer consumer, final Quest<T> sideQuest) {
    setSideQuest((SideQuest) new SideQuest() {
      @Override
      public T pursue() throws Exception {
        return sideQuest.pursue();
      }
    }.setConsumer(consumer));
    return getSideQuest();
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
