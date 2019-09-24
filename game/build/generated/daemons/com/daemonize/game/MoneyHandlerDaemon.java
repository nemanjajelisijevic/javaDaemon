package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;
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
import java.util.ArrayList;
import java.util.List;

public class MoneyHandlerDaemon implements EagerDaemon<MoneyHandlerDaemon> {
  private MoneyHandler prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public MoneyHandlerDaemon(Consumer consumer, MoneyHandler prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link MoneyHandler#animateMoney} */
  public SleepSideQuest<Pair<ImageMover.PositionedImage, ImageMover.PositionedImage>> setAnimateMoneySideQuest(Consumer consumer) {
    SleepSideQuest<Pair<ImageMover.PositionedImage, ImageMover.PositionedImage>> sideQuest = new AnimateMoneySideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(15).setConsumer(consumer));
    return sideQuest;
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public MoneyHandlerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public MoneyHandlerDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public MoneyHandlerDaemon pushSprite(Image[] sprite, float velocity) throws InterruptedException {
    prototype.pushSprite(sprite, velocity);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  public Pair<ImageMover.PositionedImage, ImageMover.PositionedImage> animateMoney() throws
      InterruptedException {
    return prototype.animateMoney();
  }

  public MoneyHandlerDaemon setBorders(float x1, float x2, float y1, float y2) {
    prototype.setBorders(x1, x2, y1, y2);
    return this;
  }

  public MoneyHandlerDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public ImageMover.PositionedImage animate() throws InterruptedException {
    return prototype.animate();
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  public MoneyHandlerDaemon setVelocity(float velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public MoneyHandlerDaemon pause() {
    prototype.pause();
    return this;
  }

  public Image iterateSprite() {
    return prototype.iterateSprite();
  }

  public MoneyHandlerDaemon setOutOfBordersClosure(Runnable closure) {
    prototype.setOutOfBordersClosure(closure);
    return this;
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public MoneyHandlerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public MoneyHandlerDaemon cont() {
    prototype.cont();
    return this;
  }

  public MoneyHandlerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public MoneyHandlerDaemon setAmount(int amount) {
    prototype.setAmount(amount);
    return this;
  }

  public MoneyHandlerDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public MoneyHandlerDaemon setOutOfBordersConsumer(Consumer consumer) {
    prototype.setOutOfBordersConsumer(consumer);
    return this;
  }

  public boolean setDirectionAndMove(float x, float y, float velocityint) {
    return prototype.setDirectionAndMove(x, y, velocityint);
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public MoneyHandlerDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  public MoneyHandler getPrototype() {
    return prototype;
  }

  public MoneyHandlerDaemon setPrototype(MoneyHandler prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public MoneyHandlerDaemon start() {
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
  public MoneyHandlerDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public MoneyHandlerDaemon clear() {
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
  public MoneyHandlerDaemon setName(String name) {
    mainDaemonEngine.setName(name);
    sideDaemonEngine.setName(name + " - SIDE");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public MoneyHandlerDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    return this;
  }

  public MoneyHandlerDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public MoneyHandlerDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public MoneyHandlerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public MoneyHandlerDaemon interrupt() {
    mainDaemonEngine.interrupt();
    return this;
  }

  @Override
  public MoneyHandlerDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateMoneySideQuest extends SleepSideQuest<Pair<ImageMover.PositionedImage, ImageMover.PositionedImage>> {
    private AnimateMoneySideQuest() {
      super();
      this.description = "animateMoney";
    }

    @Override
    public final Pair<ImageMover.PositionedImage, ImageMover.PositionedImage> pursue() throws
        Exception {
      return prototype.animateMoney();
    }
  }

  private final class GoToMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocityint;

    private GoToMainQuest(float x, float y, float velocityint, Closure<Boolean> closure) {
      super(closure);
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
}
