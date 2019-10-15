package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.HybridDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class DoubleExampleDaemon implements Daemon<DoubleExampleDaemon> {
  private DoubleExample prototype;

  protected HybridDaemonEngine daemonEngine;

  public DoubleExampleDaemon(Consumer consumer, DoubleExample prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link DoubleExample#logAndReturn} */
  public SideQuest<Integer> setLogAndReturnSideQuest(Consumer consumer) {
    SideQuest<Integer> sideQuest = new LogAndReturnSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setConsumer(consumer));
    return sideQuest;
  }

  public int logAndReturn() {
    return prototype.logAndReturn();
  }

  /**
   * Prototype method {@link com.daemonize.game.DoubleExample#increment} */
  public DoubleExampleDaemon increment(Consumer consumer, Closure<Boolean> closure) {
    daemonEngine.pursueQuest(new IncrementMainQuest(closure, null).setConsumer(consumer));
    return this;
  }

  public DoubleExample getPrototype() {
    return prototype;
  }

  public DoubleExampleDaemon setPrototype(DoubleExample prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public DoubleExampleDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public DoubleExampleDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public DoubleExampleDaemon queueStop() {
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
  public DoubleExampleDaemon setName(String name) {
    daemonEngine.setName(name);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public DoubleExampleDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public DoubleExampleDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class LogAndReturnSideQuest extends SideQuest<Integer> {
    private LogAndReturnSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "logAndReturn";
    }

    @Override
    public final Integer pursue() throws Exception {
      return prototype.logAndReturn();
    }
  }

  private final class IncrementMainQuest extends MainQuest<Boolean> {
    private IncrementMainQuest(Closure<Boolean> closure, ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.description = "increment";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.increment();
    }
  }
}
