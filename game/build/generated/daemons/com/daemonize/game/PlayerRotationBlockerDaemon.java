package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class PlayerRotationBlockerDaemon implements EagerDaemon<PlayerRotationBlockerDaemon> {
  private ShooterGame.PlayerRotationBlocker prototype;

  public EagerMainQuestDaemonEngine daemonEngine;

  public PlayerRotationBlockerDaemon(Consumer consumer,
      ShooterGame.PlayerRotationBlocker prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.ShooterGame.PlayerRotationBlocker#block} */
  public PlayerRotationBlockerDaemon block() {
    daemonEngine.pursueQuest(new BlockMainQuest().setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public ShooterGame.PlayerRotationBlocker getPrototype() {
    return prototype;
  }

  public PlayerRotationBlockerDaemon setPrototype(ShooterGame.PlayerRotationBlocker prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public PlayerRotationBlockerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public PlayerRotationBlockerDaemon clear() {
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
  public PlayerRotationBlockerDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public PlayerRotationBlockerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public PlayerRotationBlockerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public PlayerRotationBlockerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public PlayerRotationBlockerDaemon interrupt() {
    daemonEngine.interrupt();
    return this;
  }

  @Override
  public PlayerRotationBlockerDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    return this;
  }

  private final class BlockMainQuest extends VoidMainQuest {
    private BlockMainQuest() {
      super();
      this.description = "block";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.block();
      return null;
    }
  }
}
