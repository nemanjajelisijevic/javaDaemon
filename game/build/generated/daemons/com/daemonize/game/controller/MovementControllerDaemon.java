package com.daemonize.game.controller;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.HybridDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.imagemovers.Movable;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class MovementControllerDaemon<M extends Movable> implements Daemon<MovementControllerDaemon> {
  private MovementController<M> prototype;

  protected HybridDaemonEngine daemonEngine;

  public MovementControllerDaemon(Consumer consumer, MovementController<M> prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link MovementController#control} */
  public SleepSideQuest<Void> setControlSideQuest() {
    SleepSideQuest<Void> sideQuest = new ControlSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(50).setConsumer(null));
    return sideQuest;
  }

  public MovementControllerDaemon pressDirection(MovementController.Direction dir) {
    prototype.pressDirection(dir);
    return this;
  }

  public MovementControllerDaemon control() throws InterruptedException {
    prototype.control();
    return this;
  }

  public MovementControllerDaemon setControllable(M player) {
    prototype.setControllable(player);
    return this;
  }

  public MovementControllerDaemon releaseDirection(MovementController.Direction dir) {
    prototype.releaseDirection(dir);
    return this;
  }

  public MovementControllerDaemon speedUp() {
    prototype.speedUp();
    return this;
  }

  public MovementControllerDaemon speedDown() {
    prototype.speedDown();
    return this;
  }

  public MovementController<M> getPrototype() {
    return prototype;
  }

  public MovementControllerDaemon setPrototype(MovementController<M> prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public MovementControllerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public MovementControllerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public MovementControllerDaemon queueStop() {
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
  public MovementControllerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public MovementControllerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public MovementControllerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class ControlSideQuest extends SleepSideQuest<Void> {
    private ControlSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "control";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.control();
      return null;
    }
  }
}
