package com.daemonize.game.controller;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.HybridDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class MouseControllerDaemon implements Daemon<MouseControllerDaemon>, Controller {
  private MouseController prototype;

  protected HybridDaemonEngine daemonEngine;

  public MouseControllerDaemon(Consumer consumer, MouseController prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link MouseController#control} */
  public SleepSideQuest<Void> setControlSideQuest() {
    SleepSideQuest<Void> sideQuest = new ControlSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(50).setConsumer(null));
    return sideQuest;
  }

  @Override
  public void control() throws InterruptedException {
    prototype.control();
  }

  public MouseControllerDaemon onClick(MouseController.MouseButton mousebutton, float x, float y) {
    prototype.onClick(mousebutton, x, y);
    return this;
  }

  public MouseControllerDaemon setOnClick(MouseController.ClickCoordinateClosure clickcoordinateclosure) {
    prototype.setOnClick(clickcoordinateclosure);
    return this;
  }

  public MouseControllerDaemon setOnHoover(MouseController.HooverCoordinateClosure hoovercoordinateclosure) {
    prototype.setOnHoover(hoovercoordinateclosure);
    return this;
  }

  public MouseControllerDaemon onMove(float x, float y) {
    prototype.onMove(x, y);
    return this;
  }

  public MouseControllerDaemon onRelease(MouseController.MouseButton mousebutton) {
    prototype.onRelease(mousebutton);
    return this;
  }

  public MouseController getPrototype() {
    return prototype;
  }

  public MouseControllerDaemon setPrototype(MouseController prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public MouseControllerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public MouseControllerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public MouseControllerDaemon queueStop() {
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
  public MouseControllerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public MouseControllerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public MouseControllerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
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
