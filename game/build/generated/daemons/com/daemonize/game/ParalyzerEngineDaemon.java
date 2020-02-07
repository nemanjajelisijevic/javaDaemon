package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.ReturnVoidMainQuest;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class ParalyzerEngineDaemon implements EagerDaemon<ParalyzerEngineDaemon> {
  private LaserTower.ParalyzerEngine prototype;

  public EagerMainQuestDaemonEngine daemonEngine;

  public ParalyzerEngineDaemon(Consumer consumer, LaserTower.ParalyzerEngine prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.LaserTower.ParalyzerEngine#paralyze} */
  public ParalyzerEngineDaemon paralyze(Runnable retRun) {
    daemonEngine.pursueQuest(new ParalyzeMainQuest(retRun, null).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public LaserTower.ParalyzerEngine getPrototype() {
    return prototype;
  }

  public ParalyzerEngineDaemon setPrototype(LaserTower.ParalyzerEngine prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public ParalyzerEngineDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public ParalyzerEngineDaemon clear() {
    daemonEngine.clear();
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
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public ParalyzerEngineDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public ParalyzerEngineDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public ParalyzerEngineDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public ParalyzerEngineDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public ParalyzerEngineDaemon interrupt() {
    daemonEngine.interrupt();
    return this;
  }

  @Override
  public ParalyzerEngineDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    return this;
  }

  private final class ParalyzeMainQuest extends ReturnVoidMainQuest {
    private ParalyzeMainQuest(Runnable retRun, ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.description = "paralyze";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.paralyze();
      return null;
    }
  }
}
