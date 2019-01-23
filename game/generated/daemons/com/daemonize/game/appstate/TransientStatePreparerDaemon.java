package com.daemonize.game.appstate;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.MainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;

public class TransientStatePreparerDaemon implements Daemon {
  private BeginingState.TransientStatePreparer prototype;

  protected MainQuestDaemonEngine daemonEngine;

  public TransientStatePreparerDaemon(Consumer consumer,
      BeginingState.TransientStatePreparer prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype mapped method {@link com.daemonize.game.appstate.BeginingState.TransientStatePreparer#prepareTransientState} */
  public void prepareTransientState(Closure<TransientState1> closure) {
    daemonEngine.pursueQuest(new PrepareTransientStateMainQuest(closure));
  }

  public BeginingState.TransientStatePreparer getPrototype() {
    return prototype;
  }

  public TransientStatePreparerDaemon setPrototype(BeginingState.TransientStatePreparer prototype) {
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

  private final class PrepareTransientStateMainQuest extends MainQuest<TransientState1> {
    private PrepareTransientStateMainQuest(Closure<TransientState1> closure) {
      super(closure);
      this.description = "prepareTransientState";
    }

    @Override
    protected final TransientState1 pursue() throws Exception {
      return prototype.prepareTransientState();
    }
  }
}
