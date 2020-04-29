package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.ReturnVoidMainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class ProjectileDaemon implements EagerDaemon<ProjectileDaemon>, Movable {
  private Projectile prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine targetUpdaterDaemonEngine;

  public ProjectileDaemon(Consumer consumer, Projectile prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.targetUpdaterDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - targetUpdaterDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Projectile#animateProjectile} */
  public SleepSideQuest<ImageMover.PositionedImage[]> setAnimateProjectileSideQuest(Consumer consumer) {
    SleepSideQuest<ImageMover.PositionedImage[]> sideQuest = new AnimateProjectileSideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  @Override
  public Movable.AnimationWaiter getAnimationWaiter() {
    return prototype.getAnimationWaiter();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public ImageMover.PositionedImage[] animateProjectile() throws InterruptedException {
    return prototype.animateProjectile();
  }

  @Override
  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.Projectile#shoot} */
  public ProjectileDaemon shoot(float x, float y, float velocity, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new ShootMainQuest(x, y, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Projectile#updateTarget} */
  public ProjectileDaemon updateTarget(Runnable retRun) {
    targetUpdaterDaemonEngine.pursueQuest(new UpdateTargetMainQuest(retRun, null).setConsumer(targetUpdaterDaemonEngine.getConsumer()));
    return this;
  }

  public Projectile getPrototype() {
    return prototype;
  }

  public ProjectileDaemon setPrototype(Projectile prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public ProjectileDaemon start() {
    mainDaemonEngine.start();
    targetUpdaterDaemonEngine.start();
    sideDaemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
    targetUpdaterDaemonEngine.stop();
  }

  @Override
  public ProjectileDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public ProjectileDaemon clear() {
    mainDaemonEngine.clear();
    targetUpdaterDaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(mainDaemonEngine.getState());
    ret.add(targetUpdaterDaemonEngine.getState());
    ret.add(sideDaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(mainDaemonEngine.queueSize());
    ret.add(targetUpdaterDaemonEngine.queueSize());
    return ret;
  }

  @Override
  public ProjectileDaemon setName(String engineName) {
    mainDaemonEngine.setName(engineName);
    sideDaemonEngine.setName(engineName + " - SIDE");
    targetUpdaterDaemonEngine.setName(engineName + " - targetUpdaterDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public ProjectileDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    targetUpdaterDaemonEngine.setConsumer(consumer);
    return this;
  }

  public ProjectileDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public ProjectileDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public ProjectileDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    targetUpdaterDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public ProjectileDaemon interrupt() {
    mainDaemonEngine.interrupt();
    targetUpdaterDaemonEngine.interrupt();
    return this;
  }

  @Override
  public ProjectileDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    targetUpdaterDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateProjectileSideQuest extends SleepSideQuest<ImageMover.PositionedImage[]> {
    private AnimateProjectileSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animateProjectile";
    }

    @Override
    public final ImageMover.PositionedImage[] pursue() throws Exception {
      return prototype.animateProjectile();
    }
  }

  private final class ShootMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocity;

    private ShootMainQuest(float x, float y, float velocity, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.x = x;
      this.y = y;
      this.velocity = velocity;
      this.description = "shoot";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.shoot(x, y, velocity);
    }
  }

  private final class UpdateTargetMainQuest extends ReturnVoidMainQuest {
    private UpdateTargetMainQuest(Runnable retRun, ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.description = "updateTarget";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.updateTarget();
      return null;
    }
  }
}
