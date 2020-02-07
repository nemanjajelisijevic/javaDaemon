package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.graphics2d.images.Image;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class GlobeRotaterDaemon implements Daemon<GlobeRotaterDaemon> {
  private Game.GlobeRotater prototype;

  protected SideQuestDaemonEngine daemonEngine;

  public GlobeRotaterDaemon(Game.GlobeRotater prototype) {
    this.daemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link GlobeRotater#rotateGlobe} */
  public SleepSideQuest<Image> setRotateGlobeSideQuest(Consumer consumer) {
    SleepSideQuest<Image> sideQuest = new RotateGlobeSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(35).setConsumer(consumer));
    return sideQuest;
  }

  public Game.GlobeRotater getPrototype() {
    return prototype;
  }

  public GlobeRotaterDaemon setPrototype(Game.GlobeRotater prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public GlobeRotaterDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public GlobeRotaterDaemon clear() {
    return this;
  }

  @Override
  public GlobeRotaterDaemon queueStop() {
    stop();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(daemonEngine.getState());
    return ret;
  }

  @Override
  public GlobeRotaterDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public GlobeRotaterDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public GlobeRotaterDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class RotateGlobeSideQuest extends SleepSideQuest<Image> {
    private RotateGlobeSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "rotateGlobe";
    }

    @Override
    public final Image pursue() throws Exception {
      return prototype.rotateGlobe();
    }
  }
}
