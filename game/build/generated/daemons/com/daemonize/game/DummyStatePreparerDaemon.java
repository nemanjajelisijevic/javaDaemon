package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class DummyStatePreparerDaemon implements Daemon<DummyStatePreparerDaemon> {
  private InitStage.DummyStatePreparer prototype;

  public MainQuestDaemonEngine daemonEngine;

  public DummyStatePreparerDaemon(Consumer consumer, InitStage.DummyStatePreparer prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public DummyStatePreparerDaemon prepareSummyScene() throws InterruptedException {
    prototype.prepareSummyScene();
    return this;
  }

  public InitStage.DummyStatePreparer getPrototype() {
    return prototype;
  }

  public DummyStatePreparerDaemon setPrototype(InitStage.DummyStatePreparer prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public DummyStatePreparerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public DummyStatePreparerDaemon clear() {
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
  public DummyStatePreparerDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public DummyStatePreparerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
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

  @Override
  public DummyStatePreparerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }
}
