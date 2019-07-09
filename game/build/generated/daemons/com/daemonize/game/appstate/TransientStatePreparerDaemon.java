package com.daemonize.game.appstate;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class TransientStatePreparerDaemon implements Daemon<TransientStatePreparerDaemon> {
  private BeginingState.TransientStatePreparer prototype;

  protected MainQuestDaemonEngine daemonEngine;

  public TransientStatePreparerDaemon(Consumer consumer,
      BeginingState.TransientStatePreparer prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.appstate.BeginingState.TransientStatePreparer#prepareTransientState} */
  public TransientStatePreparerDaemon prepareTransientState(Closure<TransientState1> closure) {
    daemonEngine.pursueQuest(new PrepareTransientStateMainQuest(closure).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public BeginingState.TransientStatePreparer getPrototype() {
    return prototype;
  }

  public TransientStatePreparerDaemon setPrototype(BeginingState.TransientStatePreparer prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public TransientStatePreparerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public TransientStatePreparerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public TransientStatePreparerDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(daemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(daemonEngine.queueSize());
    return ret;
  }

  @Override
  public TransientStatePreparerDaemon setName(String name) {
    daemonEngine.setName(name);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public TransientStatePreparerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public TransientStatePreparerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class PrepareTransientStateMainQuest extends MainQuest<TransientState1> {
    private PrepareTransientStateMainQuest(Closure<TransientState1> closure) {
      super(closure);
      this.description = "prepareTransientState";
    }

    @Override
    public final TransientState1 pursue() throws Exception {
      return prototype.prepareTransientState();
    }
  }
}
