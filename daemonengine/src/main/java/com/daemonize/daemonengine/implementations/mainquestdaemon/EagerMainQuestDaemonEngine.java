package com.daemonize.daemonengine.implementations.mainquestdaemon;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.AnonMainQuest;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.daemonengine.quests.VoidQuest;

import java.util.List;
import java.util.concurrent.locks.Condition;

public class EagerMainQuestDaemonEngine extends MainQuestDaemonEngine implements EagerDaemon {

  private final Condition mainQuestAvailable = mainQuestLock.newCondition();

  public EagerMainQuestDaemonEngine(Consumer consumer) {
    super(consumer);
  }

  public <T> EagerMainQuestDaemonEngine daemonize(Quest<T> quest, Closure<T> closure) {
    addMainQuest((AnonMainQuest<T>)new AnonMainQuest(quest, closure).setConsumer(getConsumer())); //TODO check ret
    return this;
  }

  public EagerMainQuestDaemonEngine daemonize(final VoidQuest quest, Runnable closure) {
    addMainQuest((VoidMainQuest)new VoidMainQuest(closure) {
      @Override
      public Void pursue() throws Exception {
        quest.pursue();
        return null;
      }
    }.setConsumer(getConsumer()));
    return this;
  }

  @Override
  public boolean addMainQuest(MainQuest quest) {
    boolean ret;
    mainQuestLock.lock();
    ret = mainQuestQueue.add(quest);//TODO check ret of this expression
    mainQuestAvailable.signal();
    mainQuestLock.unlock();
    return ret;
  }

  @Override
  public EagerMainQuestDaemonEngine setName(String name) {
    super.setName(name);
    return this;
  }

  @Override
  protected BaseQuest getQuest() {

    BaseQuest ret = null;
    try {
      mainQuestLock.lock();
      while (mainQuestQueue.isEmpty()) {
        setState(DaemonState.IDLE);
        mainQuestAvailable.await();
      }
      ret = mainQuestQueue.poll();
    } catch (InterruptedException ex) {
      //System.out.println(DaemonUtils.tag() + " Waiting on a quest interrupted");
    } finally {
      mainQuestLock.unlock();
    }
    return ret;
  }

  @Override
  public EagerMainQuestDaemonEngine interrupt() {
    if (!state.equals(DaemonState.STOPPED) && !state.equals(DaemonState.IDLE)) {
      if (daemonThread != null
              && !Thread.currentThread().equals(daemonThread)
              && daemonThread.isAlive()) {
        daemonThread.interrupt();
      }
    }
    return this;
  }

  @Override
  public EagerMainQuestDaemonEngine clearAndInterrupt() {
      mainQuestLock.lock();
      mainQuestQueue.clear();
      mainQuestLock.unlock();
      interrupt();
      return this;
  }

  @Override
  public EagerMainQuestDaemonEngine clear() {
      return (EagerMainQuestDaemonEngine) super.clear();
  }

  @Override
  public EagerMainQuestDaemonEngine setConsumer(Consumer consumer) {
      return (EagerMainQuestDaemonEngine) super.setConsumer(consumer);
  }

  @Override
  public EagerMainQuestDaemonEngine start() {
      return (EagerMainQuestDaemonEngine) super.start();
  }

  @Override
  public EagerMainQuestDaemonEngine queueStop() {
      return (EagerMainQuestDaemonEngine) super.queueStop(this);
  }

  @Override
  public List<DaemonState> getEnginesState() {
      return super.getEnginesState();
  }
}
