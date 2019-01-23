package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.MainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Void;

public class DummyStatePreparerDaemon implements Daemon {
  private InitState.DummyStatePreparer prototype;

  protected MainQuestDaemonEngine daemonEngine;

  public DummyStatePreparerDaemon(Consumer consumer, InitState.DummyStatePreparer prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype mapped method {@link com.daemonize.game.InitState.DummyStatePreparer#prepareSummyScene} */
  public void prepareSummyScene(Runnable retRun) {
    daemonEngine.pursueQuest(new PrepareSummySceneMainQuest(retRun));
  }

  public InitState.DummyStatePreparer getPrototype() {
    return prototype;
  }

  public DummyStatePreparerDaemon setPrototype(InitState.DummyStatePreparer prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public void start() {
    daemonEngine.start();
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public void queueStop() {
    daemonEngine.queueStop();
  }

  @Override
  public DaemonState getState() {
    return daemonEngine.getState();
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

  private final class PrepareSummySceneMainQuest extends VoidMainQuest<Void> {
    private PrepareSummySceneMainQuest(Runnable retRun) {
      super(retRun);
      this.description = "prepareSummyScene";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.prepareSummyScene();
      return null;
    }
  }
}
