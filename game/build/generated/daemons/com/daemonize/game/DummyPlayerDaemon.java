package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;
import com.daemonize.imagemovers.spriteiterators.SpriteIterator;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class DummyPlayerDaemon implements EagerDaemon<DummyPlayerDaemon>, Movable {
  private DummyPlayer prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public DummyPlayerDaemon(Consumer consumer, DummyPlayer prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link DummyPlayer#animateDummyPlayer} */
  public SleepSideQuest<ImageMover.PositionedImage> setAnimateDummyPlayerSideQuest(Consumer consumer) {
    SleepSideQuest<ImageMover.PositionedImage> sideQuest = new AnimateDummyPlayerSideQuest(null);
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(30).setConsumer(consumer));
    return sideQuest;
  }

  public boolean redirect(float x, float y) {
    return prototype.redirect(x, y);
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public DummyPlayerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public DummyPlayerDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  public ImageMover.PositionedImage animateDummyPlayer() throws InterruptedException {
    return prototype.animateDummyPlayer();
  }

  public DummyPlayerDaemon pushSprite(Image[] sprite) throws InterruptedException {
    prototype.pushSprite(sprite);
    return this;
  }

  public DummyPlayerDaemon setSpriteIterator(SpriteIterator spriteiterator) {
    prototype.setSpriteIterator(spriteiterator);
    return this;
  }

  @Override
  public Movable.AnimationWaiter getAnimationWaiter() {
    return prototype.getAnimationWaiter();
  }

  public boolean setDirectionToPoint(float x, float y) {
    return prototype.setDirectionToPoint(x, y);
  }

  public DummyPlayerDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public ImageMover.PositionedImage animate() throws InterruptedException {
    return prototype.animate();
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  @Override
  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  public double absDistance(float x1, float y1, float x2, float y2) {
    return prototype.absDistance(x1, y1, x2, y2);
  }

  public Image iterateSprite() {
    return prototype.iterateSprite();
  }

  @Override
  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public DummyPlayerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
    return prototype.absDistance(source, dest);
  }

  public DummyPlayerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public DummyPlayerDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.DummyPlayer#go} */
  public DummyPlayerDaemon go(Pair<Float, Float> coords, float velocity) {
    mainDaemonEngine.pursueQuest(new GoMainQuest(coords, velocity).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.DummyPlayer#go} */
  public DummyPlayerDaemon go(float x, float y, float velocity) {
    mainDaemonEngine.pursueQuest(new GoIMainQuest(x, y, velocity).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.DummyPlayer#goTo} */
  public DummyPlayerDaemon goTo(float x, float y, float velocity, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.DummyPlayer#goTo} */
  public DummyPlayerDaemon goTo(Pair<Float, Float> coords, float velocity,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToIMainQuest(coords, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  public DummyPlayer getPrototype() {
    return prototype;
  }

  public DummyPlayerDaemon setPrototype(DummyPlayer prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public DummyPlayerDaemon start() {
    mainDaemonEngine.start();
    sideDaemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
  }

  @Override
  public DummyPlayerDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public DummyPlayerDaemon clear() {
    mainDaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(mainDaemonEngine.getState());
    ret.add(sideDaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(mainDaemonEngine.queueSize());
    return ret;
  }

  @Override
  public DummyPlayerDaemon setName(String engineName) {
    mainDaemonEngine.setName(engineName);
    sideDaemonEngine.setName(engineName + " - SIDE");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public DummyPlayerDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    return this;
  }

  public DummyPlayerDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public DummyPlayerDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public DummyPlayerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public DummyPlayerDaemon interrupt() {
    mainDaemonEngine.interrupt();
    return this;
  }

  @Override
  public DummyPlayerDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateDummyPlayerSideQuest extends SleepSideQuest<ImageMover.PositionedImage> {
    private AnimateDummyPlayerSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animateDummyPlayer";
    }

    @Override
    public final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.animateDummyPlayer();
    }
  }

  private final class GoMainQuest extends VoidMainQuest {
    private Pair<Float, Float> coords;

    private float velocity;

    private GoMainQuest(Pair<Float, Float> coords, float velocity) {
      super();
      this.coords = coords;
      this.velocity = velocity;
      this.description = "go";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.go(coords, velocity);
      return null;
    }
  }

  private final class GoIMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private float velocity;

    private GoIMainQuest(float x, float y, float velocity) {
      super();
      this.x = x;
      this.y = y;
      this.velocity = velocity;
      this.description = "go";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.go(x, y, velocity);
      return null;
    }
  }

  private final class GoToMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocity;

    private GoToMainQuest(float x, float y, float velocity, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.x = x;
      this.y = y;
      this.velocity = velocity;
      this.description = "goTo";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.goTo(x, y, velocity);
    }
  }

  private final class GoToIMainQuest extends MainQuest<Boolean> {
    private Pair<Float, Float> coords;

    private float velocity;

    private GoToIMainQuest(Pair<Float, Float> coords, float velocity, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.coords = coords;
      this.velocity = velocity;
      this.description = "goTo";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.goTo(coords, velocity);
    }
  }
}
