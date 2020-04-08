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
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class ZombieDaemon implements EagerDaemon<ZombieDaemon>, Mortal<ZombieDaemon>, Movable {
  private Zombie prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine rotateDaemonEngine;

  public ZombieDaemon(Consumer consumer, Zombie prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.rotateDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - rotateDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Zombie#animateZombie} */
  public SleepSideQuest<ImageMover.PositionedImage[]> setAnimateZombieSideQuest(Consumer consumer) {
    SleepSideQuest<ImageMover.PositionedImage[]> sideQuest = new AnimateZombieSideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  public boolean redirect(float x, float y) {
    return prototype.redirect(x, y);
  }

  @Override
  public ZombieDaemon setHp(int hp) {
    prototype.setHp(hp);
    return this;
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public ZombieDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public ZombieDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  @Override
  public int getMaxHp() {
    return prototype.getMaxHp();
  }

  @Override
  public int getHp() {
    return prototype.getHp();
  }

  public ZombieDaemon pushSprite(Image[] sprite) throws InterruptedException {
    prototype.pushSprite(sprite);
    return this;
  }

  public ZombieDaemon setSpriteIterator(SpriteIterator spriteiterator) {
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

  @Override
  public ZombieDaemon setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
    return this;
  }

  public ZombieDaemon setDirection(ImageMover.Direction direction) {
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

  public ImageMover.PositionedImage[] animateZombie() throws InterruptedException {
    return prototype.animateZombie();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public ZombieDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
    return prototype.absDistance(source, dest);
  }

  public ZombieDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public ZombieDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.Zombie#attack} */
  public ZombieDaemon attack(long ms, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new AttackMainQuest(ms, retRun, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public ZombieDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Zombie#rotateTowards} */
  public ZombieDaemon rotateTowards(float x, float y) {
    rotateDaemonEngine.pursueQuest(new RotateTowardsMainQuest(x, y).setConsumer(rotateDaemonEngine.getConsumer()));
    return this;
  }

  public Zombie getPrototype() {
    return prototype;
  }

  public ZombieDaemon setPrototype(Zombie prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public ZombieDaemon start() {
    mainDaemonEngine.start();
    rotateDaemonEngine.start();
    sideDaemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
    rotateDaemonEngine.stop();
  }

  @Override
  public ZombieDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public ZombieDaemon clear() {
    mainDaemonEngine.clear();
    rotateDaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(mainDaemonEngine.getState());
    ret.add(rotateDaemonEngine.getState());
    ret.add(sideDaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(mainDaemonEngine.queueSize());
    ret.add(rotateDaemonEngine.queueSize());
    return ret;
  }

  @Override
  public ZombieDaemon setName(String engineName) {
    mainDaemonEngine.setName(engineName);
    sideDaemonEngine.setName(engineName + " - SIDE");
    rotateDaemonEngine.setName(engineName + " - rotateDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public ZombieDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    rotateDaemonEngine.setConsumer(consumer);
    return this;
  }

  public ZombieDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public ZombieDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public ZombieDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    rotateDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public ZombieDaemon interrupt() {
    mainDaemonEngine.interrupt();
    rotateDaemonEngine.interrupt();
    return this;
  }

  @Override
  public ZombieDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    rotateDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateZombieSideQuest extends SleepSideQuest<ImageMover.PositionedImage[]> {
    private AnimateZombieSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animateZombie";
    }

    @Override
    public final ImageMover.PositionedImage[] pursue() throws Exception {
      return prototype.animateZombie();
    }
  }

  private final class AttackMainQuest extends ReturnVoidMainQuest {
    private long ms;

    private AttackMainQuest(long ms, Runnable retRun, ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.ms = ms;
      this.description = "attack";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.attack(ms);
      return null;
    }
  }

  private final class GoToMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocityint;

    private GoToMainQuest(float x, float y, float velocityint, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "goTo";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.goTo(x, y, velocityint);
    }
  }

  private final class RotateTowardsMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private RotateTowardsMainQuest(float x, float y) {
      super();
      this.x = x;
      this.y = y;
      this.description = "rotateTowards";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotateTowards(x, y);
      return null;
    }
  }
}
