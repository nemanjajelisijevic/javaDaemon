package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class DummyStatePreparerDaemon implements Daemon<DummyStatePreparerDaemon> {
  private InitState.DummyStatePreparer prototype;

  protected MainQuestDaemonEngine daemonEngine;

  public DummyStatePreparerDaemon(Consumer consumer, InitState.DummyStatePreparer prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.InitState.DummyStatePreparer#prepareSummyScene} */
  public DummyStatePreparerDaemon prepareSummyScene(Runnable retRun) {
    daemonEngine.pursueQuest(new PrepareSummySceneMainQuest(retRun).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public InitState.DummyStatePreparer getPrototype() {
    return prototype;
  }

  public DummyStatePreparerDaemon setPrototype(InitState.DummyStatePreparer prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public DummyStatePreparerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public DummyStatePreparerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public DummyStatePreparerDaemon queueStop() {
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
  public DummyStatePreparerDaemon setName(String name) {
    daemonEngine.setName(name);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public DummyStatePreparerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  private final class PrepareSummySceneMainQuest extends VoidMainQuest {
    private PrepareSummySceneMainQuest(Runnable retRun) {
      super(retRun);
      this.description = "prepareSummyScene";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.prepareSummyScene();
      return null;
    }
  }
}
