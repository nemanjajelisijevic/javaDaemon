package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Void;

public class DoubleExampleDaemon implements Daemon {
  private DoubleExample prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public DoubleExampleDaemon(Consumer mainConsumer, Consumer sideConsumer,
      DoubleExample prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - MAIN");
    this.sideDaemonEngine = new SideQuestDaemonEngine(sideConsumer).setName(this.getClass().getSimpleName() + " - SIDE");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link DoubleExample#logAndReturn} */
  public SideQuest<Integer> setLogAndReturnSideQuest() {
    SideQuest<Integer> sideQuest = new LogAndReturnSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(1000));
    return sideQuest;
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype mapped method {@link DoubleExample#testVoid} */
  public void testVoid(long sleepmillis, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new TestVoidMainQuest(sleepmillis, retRun));
  }

  /**
   * Prototype mapped method {@link DoubleExample#increment} */
  public void increment(Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new IncrementMainQuest(closure));
  }

  /**
   * Prototype mapped method {@link DoubleExample#logAndReturn} */
  public void logAndReturn(Closure<Integer> closure) {
    mainDaemonEngine.pursueQuest(new LogAndReturnMainQuest(closure));
  }

  public DoubleExample getPrototype() {
    return prototype;
  }

  public DoubleExampleDaemon setPrototype(DoubleExample prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public void start() {
    sideDaemonEngine.start();
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
  }

  @Override
  public void queueStop() {
    mainDaemonEngine.queueStop();
    sideDaemonEngine.stop();
  }

  @Override
  public DaemonState getState() {
    return sideDaemonEngine.getState();
  }

  @Override
  public DoubleExampleDaemon setName(String name) {
    mainDaemonEngine.setName(name + " - MAIN");
    sideDaemonEngine.setName(name + " - SIDE");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public DoubleExampleDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    return this;
  }

  public DoubleExampleDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public DoubleExampleDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  private final class LogAndReturnSideQuest extends SideQuest<Integer> {
    private LogAndReturnSideQuest() {
      this.description = "logAndReturn";
    }

    @Override
    protected final Integer pursue() throws Exception {
      return prototype.logAndReturn();
    }
  }

  private final class TestVoidMainQuest extends VoidMainQuest<Void> {
    private long sleepmillis;

    private TestVoidMainQuest(long sleepmillis, Runnable retRun) {
      super(retRun);
      this.sleepmillis = sleepmillis;
      this.description = "testVoid";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.testVoid(sleepmillis);
      return null;
    }
  }

  private final class IncrementMainQuest extends MainQuest<Boolean> {
    private IncrementMainQuest(Closure<Boolean> closure) {
      super(closure);
      this.description = "increment";
    }

    @Override
    protected final Boolean pursue() throws Exception {
      return prototype.increment();
    }
  }

  private final class LogAndReturnMainQuest extends MainQuest<Integer> {
    private LogAndReturnMainQuest(Closure<Integer> closure) {
      super(closure);
      this.description = "logAndReturn";
    }

    @Override
    protected final Integer pursue() throws Exception {
      return prototype.logAndReturn();
    }
  }
}
