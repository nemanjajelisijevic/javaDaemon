package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.HybridDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class SpriteAnimatorDaemon implements Daemon<SpriteAnimatorDaemon> {
  private SpriteAnimator prototype;

  protected HybridDaemonEngine daemonEngine;

  public SpriteAnimatorDaemon(Consumer consumer, SpriteAnimator prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link SpriteAnimator#animate} */
  public SleepSideQuest<ImageMover.PositionedImage> setAnimateSideQuest(Consumer consumer) {
    SleepSideQuest<ImageMover.PositionedImage> sideQuest = new AnimateSideQuest(daemonEngine.getClosureAwaiter());
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  public SpriteAnimatorDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public ImageMover.PositionedImage animate() {
    return prototype.animate();
  }

  public SpriteAnimator getPrototype() {
    return prototype;
  }

  public SpriteAnimatorDaemon setPrototype(SpriteAnimator prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public SpriteAnimatorDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public SpriteAnimatorDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public SpriteAnimatorDaemon queueStop() {
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
  public SpriteAnimatorDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public SpriteAnimatorDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public SpriteAnimatorDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class AnimateSideQuest extends SleepSideQuest<ImageMover.PositionedImage> {
    private AnimateSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animate";
    }

    @Override
    public final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.animate();
    }
  }
}
