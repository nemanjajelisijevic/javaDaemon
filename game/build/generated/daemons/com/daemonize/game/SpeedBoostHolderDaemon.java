package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
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

public class SpeedBoostHolderDaemon implements Daemon<SpeedBoostHolderDaemon> {
  private KeyBoardMovementControllerImpl.SpeedBoostHolder prototype;

  public MainQuestDaemonEngine daemonEngine;

  public SpeedBoostHolderDaemon(Consumer consumer,
      KeyBoardMovementControllerImpl.SpeedBoostHolder prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.KeyBoardMovementControllerImpl.SpeedBoostHolder#holdBoost} */
  public SpeedBoostHolderDaemon holdBoost(Runnable retRun) {
    daemonEngine.pursueQuest(new HoldBoostMainQuest(retRun, null).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public KeyBoardMovementControllerImpl.SpeedBoostHolder getPrototype() {
    return prototype;
  }

  public SpeedBoostHolderDaemon setPrototype(KeyBoardMovementControllerImpl.SpeedBoostHolder prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public SpeedBoostHolderDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public SpeedBoostHolderDaemon clear() {
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
  public SpeedBoostHolderDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public SpeedBoostHolderDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public SpeedBoostHolderDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public SpeedBoostHolderDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class HoldBoostMainQuest extends ReturnVoidMainQuest {
    private HoldBoostMainQuest(Runnable retRun, ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.description = "holdBoost";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.holdBoost();
      return null;
    }
  }
}
