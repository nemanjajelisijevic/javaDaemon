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

public class DirectionControllerDaemon<M extends Movable> implements Daemon<DirectionControllerDaemon> {
  private DirectionController<M> prototype;

  protected HybridDaemonEngine daemonEngine;

  public DirectionControllerDaemon(Consumer consumer, DirectionController<M> prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link DirectionController#control} */
  public SleepSideQuest<Void> setControlSideQuest() {
    SleepSideQuest<Void> sideQuest = new ControlSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(50).setConsumer(null));
    return sideQuest;
  }

  public DirectionControllerDaemon control() throws InterruptedException {
    prototype.control();
    return this;
  }

  public DirectionControllerDaemon pressDirection(DirectionController.Direction dir) {
    prototype.pressDirection(dir);
    return this;
  }

  public DirectionControllerDaemon setControllable(M player) {
    prototype.setControllable(player);
    return this;
  }

  public DirectionControllerDaemon releaseDirection(DirectionController.Direction dir) {
    prototype.releaseDirection(dir);
    return this;
  }

  public DirectionControllerDaemon speedUp() {
    prototype.speedUp();
    return this;
  }

  public DirectionControllerDaemon speedDown() {
    prototype.speedDown();
    return this;
  }

  public DirectionController<M> getPrototype() {
    return prototype;
  }

  public DirectionControllerDaemon setPrototype(DirectionController<M> prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public DirectionControllerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public DirectionControllerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public DirectionControllerDaemon queueStop() {
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
  public DirectionControllerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public DirectionControllerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public DirectionControllerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
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
