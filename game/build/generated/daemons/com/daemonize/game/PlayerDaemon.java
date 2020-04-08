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

public class PlayerDaemon implements EagerDaemon<PlayerDaemon>, Target<PlayerDaemon> {
  private Player prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine rotateDaemonEngine;

  protected EagerMainQuestDaemonEngine interactDaemonEngine;

  public PlayerDaemon(Consumer consumer, Player prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.rotateDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - rotateDaemonEngine");
    this.interactDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - interactDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Player#animatePlayer} */
  public SleepSideQuest<ImageMover.PositionedImage[]> setAnimatePlayerSideQuest(Consumer consumer) {
    SleepSideQuest<ImageMover.PositionedImage[]> sideQuest = new AnimatePlayerSideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  public boolean redirect(float x, float y) {
    return prototype.redirect(x, y);
  }

  @Override
  public PlayerDaemon setHp(int hp) {
    prototype.setHp(hp);
    return this;
  }

  @Override
  public PlayerDaemon setParalyzed(boolean paralyzed) {
    prototype.setParalyzed(paralyzed);
    return this;
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public PlayerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public PlayerDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  @Override
  public PlayerDaemon setShootable(boolean shootable) {
    prototype.setShootable(shootable);
    return this;
  }

  @Override
  public int getMaxHp() {
    return prototype.getMaxHp();
  }

  @Override
  public int getHp() {
    return prototype.getHp();
  }

  public PlayerDaemon setSpriteIterator(SpriteIterator spriteiterator) {
    prototype.setSpriteIterator(spriteiterator);
    return this;
  }

  @Override
  public boolean isShootable() {
    return prototype.isShootable();
  }

  @Override
  public Movable.AnimationWaiter getAnimationWaiter() {
    return prototype.getAnimationWaiter();
  }

  public boolean setDirectionToPoint(float x, float y) {
    return prototype.setDirectionToPoint(x, y);
  }

  @Override
  public PlayerDaemon setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
    return this;
  }

  public PlayerDaemon setDirection(ImageMover.Direction direction) {
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

  @Override
  public boolean isParalyzed() {
    return prototype.isParalyzed();
  }

  public ImageMover.PositionedImage[] animatePlayer() throws InterruptedException {
    return prototype.animatePlayer();
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

  public PlayerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
    return prototype.absDistance(source, dest);
  }

  public PlayerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public PlayerDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#go} */
  public PlayerDaemon go(Pair<Float, Float> coords, float velocity) {
    mainDaemonEngine.pursueQuest(new GoMainQuest(coords, velocity).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#go} */
  public PlayerDaemon go(float x, float y, float velocity) {
    mainDaemonEngine.pursueQuest(new GoIMainQuest(x, y, velocity).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#interact} */
  public PlayerDaemon interact(long sleeptimems, Runnable retRun) {
    interactDaemonEngine.pursueQuest(new InteractMainQuest(sleeptimems, retRun, null).setConsumer(interactDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#pushSprite} */
  public PlayerDaemon pushSprite(Image[] sprite) {
    rotateDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite).setConsumer(rotateDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#rotAndGo} */
  public PlayerDaemon rotAndGo(Pair<Float, Float> coords, float velocity,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new RotAndGoMainQuest(coords, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#rotateTowards} */
  public PlayerDaemon rotateTowards(Pair<Float, Float> coords, Runnable retRun) {
    rotateDaemonEngine.pursueQuest(new RotateTowardsMainQuest(coords, retRun, null).setConsumer(rotateDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#rotAndGo} */
  public PlayerDaemon rotAndGo(float x, float y, float velocity, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new RotAndGoIMainQuest(x, y, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#goTo} */
  public PlayerDaemon goTo(float x, float y, float velocity, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#rotateTowards} */
  public PlayerDaemon rotateTowards(float x, float y) {
    rotateDaemonEngine.pursueQuest(new RotateTowardsIMainQuest(x, y).setConsumer(rotateDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Player#goTo} */
  public PlayerDaemon goTo(Pair<Float, Float> coords, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new GoToIMainQuest(coords, velocity, retRun, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  public Player getPrototype() {
    return prototype;
  }

  public PlayerDaemon setPrototype(Player prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public PlayerDaemon start() {
    mainDaemonEngine.start();
    rotateDaemonEngine.start();
    interactDaemonEngine.start();
    sideDaemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
    rotateDaemonEngine.stop();
    interactDaemonEngine.stop();
  }

  @Override
  public PlayerDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public PlayerDaemon clear() {
    mainDaemonEngine.clear();
    rotateDaemonEngine.clear();
    interactDaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(mainDaemonEngine.getState());
    ret.add(rotateDaemonEngine.getState());
    ret.add(interactDaemonEngine.getState());
    ret.add(sideDaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(mainDaemonEngine.queueSize());
    ret.add(rotateDaemonEngine.queueSize());
    ret.add(interactDaemonEngine.queueSize());
    return ret;
  }

  @Override
  public PlayerDaemon setName(String engineName) {
    mainDaemonEngine.setName(engineName);
    sideDaemonEngine.setName(engineName + " - SIDE");
    rotateDaemonEngine.setName(engineName + " - rotateDaemonEngine");
    interactDaemonEngine.setName(engineName + " - interactDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public PlayerDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    rotateDaemonEngine.setConsumer(consumer);
    interactDaemonEngine.setConsumer(consumer);
    return this;
  }

  public PlayerDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public PlayerDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public PlayerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    rotateDaemonEngine.setUncaughtExceptionHandler(handler);
    interactDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public PlayerDaemon interrupt() {
    mainDaemonEngine.interrupt();
    rotateDaemonEngine.interrupt();
    interactDaemonEngine.interrupt();
    return this;
  }

  @Override
  public PlayerDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    rotateDaemonEngine.clearAndInterrupt();
    interactDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimatePlayerSideQuest extends SleepSideQuest<ImageMover.PositionedImage[]> {
    private AnimatePlayerSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animatePlayer";
    }

    @Override
    public final ImageMover.PositionedImage[] pursue() throws Exception {
      return prototype.animatePlayer();
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

  private final class InteractMainQuest extends ReturnVoidMainQuest {
    private long sleeptimems;

    private InteractMainQuest(long sleeptimems, Runnable retRun,
        ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.sleeptimems = sleeptimems;
      this.description = "interact";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.interact(sleeptimems);
      return null;
    }
  }

  private final class PushSpriteMainQuest extends VoidMainQuest {
    private Image[] sprite;

    private PushSpriteMainQuest(Image[] sprite) {
      super();
      this.sprite = sprite;
      this.description = "pushSprite";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.pushSprite(sprite);
      return null;
    }
  }

  private final class RotAndGoMainQuest extends MainQuest<Boolean> {
    private Pair<Float, Float> coords;

    private float velocity;

    private RotAndGoMainQuest(Pair<Float, Float> coords, float velocity, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.coords = coords;
      this.velocity = velocity;
      this.description = "rotAndGo";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.rotAndGo(coords, velocity);
    }
  }

  private final class RotateTowardsMainQuest extends ReturnVoidMainQuest {
    private Pair<Float, Float> coords;

    private RotateTowardsMainQuest(Pair<Float, Float> coords, Runnable retRun,
        ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.coords = coords;
      this.description = "rotateTowards";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotateTowards(coords);
      return null;
    }
  }

  private final class RotAndGoIMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocity;

    private RotAndGoIMainQuest(float x, float y, float velocity, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.x = x;
      this.y = y;
      this.velocity = velocity;
      this.description = "rotAndGo";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.rotAndGo(x, y, velocity);
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

  private final class RotateTowardsIMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private RotateTowardsIMainQuest(float x, float y) {
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

  private final class GoToIMainQuest extends ReturnVoidMainQuest {
    private Pair<Float, Float> coords;

    private float velocity;

    private GoToIMainQuest(Pair<Float, Float> coords, float velocity, Runnable retRun,
        ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.coords = coords;
      this.velocity = velocity;
      this.description = "goTo";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.goTo(coords, velocity);
      return null;
    }
  }
}
