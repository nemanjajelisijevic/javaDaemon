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
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.imagemovers.ImageTranslationMover;
import com.daemonize.game.images.Image;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
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

  public MoneyHandlerDaemon setVelocity(float velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public MoneyHandlerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public MoneyHandlerDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public MoneyHandlerDaemon setOutOfBordersConsumer(Consumer consumer) {
    prototype.setOutOfBordersConsumer(consumer);
    return this;
  }

  public MoneyHandlerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public MoneyHandlerDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public MoneyHandlerDaemon pause() {
    prototype.pause();
    return this;
  }

  public MoneyHandlerDaemon cont() {
    prototype.cont();
    return this;
  }

  public MoneyHandlerDaemon setOutOfBordersClosure(Runnable closure) {
    prototype.setOutOfBordersClosure(closure);
    return this;
  }

  public MoneyHandlerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#setBorders} */
  public MoneyHandlerDaemon setBorders(float x1, float x2, float y1, float y2,
      Closure<ImageTranslationMover> closure) {
    mainDaemonEngine.pursueQuest(new SetBordersMainQuest(x1, x2, y1, y2, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.CachedArraySpriteImageMover#pushSprite} */
  public MoneyHandlerDaemon pushSprite(Image[] sprite, float velocity) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.MoneyHandler#setCoordinates} */
  public MoneyHandlerDaemon setCoordinates(float lastx, float lasty) {
    mainDaemonEngine.pursueQuest(new SetCoordinatesMainQuest(lastx, lasty).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.CoordinatedImageTranslationMover#animate} */
  public MoneyHandlerDaemon animate(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public MoneyHandlerDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#setDirectionAndMove} */
  public MoneyHandlerDaemon setDirectionAndMove(float x, float y, float velocityint,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new SetDirectionAndMoveMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.MoneyHandler#setAmount} */
  public MoneyHandlerDaemon setAmount(int amount) {
    mainDaemonEngine.pursueQuest(new SetAmountMainQuest(amount).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.MoneyHandler#animateMoney} */
  public MoneyHandlerDaemon animateMoney(Closure<Pair<ImageMover.PositionedImage, ImageMover.PositionedImage>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMoneyMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.MoneyHandler#iterateSprite} */
  public MoneyHandlerDaemon iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
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

  private final class SetBordersMainQuest extends MainQuest<ImageTranslationMover> {
    private float x1;

    private float x2;

    private float y1;

    private float y2;

    private SetBordersMainQuest(float x1, float x2, float y1, float y2,
        Closure<ImageTranslationMover> closure) {
      super(closure);
      this.x1 = x1;
      this.x2 = x2;
      this.y1 = y1;
      this.y2 = y2;
      this.description = "setBorders";
    }

    @Override
    public final ImageTranslationMover pursue() throws Exception {
      return prototype.setBorders(x1, x2, y1, y2);
    }
  }

  private final class PushSpriteMainQuest extends VoidMainQuest {
    private Image[] sprite;

    private float velocity;

    private PushSpriteMainQuest(Image[] sprite, float velocity) {
      super();
      setVoid();
      this.sprite = sprite;
      this.velocity = velocity;
      this.description = "pushSprite";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.pushSprite(sprite, velocity);
      return null;
    }
  }

  private final class SetCoordinatesMainQuest extends VoidMainQuest {
    private float lastx;

    private float lasty;

    private SetCoordinatesMainQuest(float lastx, float lasty) {
      super();
      setVoid();
      this.lastx = lastx;
      this.lasty = lasty;
      this.description = "setCoordinates";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.setCoordinates(lastx, lasty);
      return null;
    }
  }

  private final class AnimateMainQuest extends MainQuest<ImageMover.PositionedImage> {
    private AnimateMainQuest(Closure<ImageMover.PositionedImage> closure) {
      super(closure);
      this.description = "animate";
    }

    @Override
    public final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.animate();
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

  private final class SetDirectionAndMoveMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocityint;

    private SetDirectionAndMoveMainQuest(float x, float y, float velocityint,
        Closure<Boolean> closure) {
      super(closure);
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "setDirectionAndMove";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.setDirectionAndMove(x, y, velocityint);
    }
  }

  private final class SetAmountMainQuest extends VoidMainQuest {
    private int amount;

    private SetAmountMainQuest(int amount) {
      super();
      setVoid();
      this.amount = amount;
      this.description = "setAmount";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.setAmount(amount);
      return null;
    }
  }

  private final class AnimateMoneyMainQuest extends MainQuest<Pair<ImageMover.PositionedImage, ImageMover.PositionedImage>> {
    private AnimateMoneyMainQuest(Closure<Pair<ImageMover.PositionedImage, ImageMover.PositionedImage>> closure) {
      super(closure);
      this.description = "animateMoney";
    }

    @Override
    public final Pair<ImageMover.PositionedImage, ImageMover.PositionedImage> pursue() throws
        Exception {
      return prototype.animateMoney();
    }
  }

  private final class IterateSpriteMainQuest extends MainQuest<Image> {
    private IterateSpriteMainQuest(Closure<Image> closure) {
      super(closure);
      this.description = "iterateSprite";
    }

    @Override
    public final Image pursue() throws Exception {
      return prototype.iterateSprite();
    }
  }
}
