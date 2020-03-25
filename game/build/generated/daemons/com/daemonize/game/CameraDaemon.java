package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.HybridDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.views.ImageView;
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

public class CameraDaemon implements Daemon<CameraDaemon> {
  private Camera prototype;

  protected HybridDaemonEngine daemonEngine;

  public CameraDaemon(Consumer consumer, Camera prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link Camera#follow} */
  public SleepSideQuest<Void> setFollowSideQuest() {
    SleepSideQuest<Void> sideQuest = new FollowSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(null));
    return sideQuest;
  }

  public CameraDaemon setTarget(Movable target) {
    prototype.setTarget(target);
    return this;
  }

  public CameraDaemon addStaticView(ImageView view) {
    prototype.addStaticView(view);
    return this;
  }

  public int getY() {
    return prototype.getY();
  }

  public int getX() {
    return prototype.getX();
  }

  public CameraDaemon follow() throws InterruptedException {
    prototype.follow();
    return this;
  }

  public CameraDaemon setRenderer(Renderer2D renderer) {
    prototype.setRenderer(renderer);
    return this;
  }

  public Camera getPrototype() {
    return prototype;
  }

  public CameraDaemon setPrototype(Camera prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public CameraDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public CameraDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public CameraDaemon queueStop() {
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
  public CameraDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public CameraDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public CameraDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class FollowSideQuest extends SleepSideQuest<Void> {
    private FollowSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "follow";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.follow();
      return null;
    }
  }
}
