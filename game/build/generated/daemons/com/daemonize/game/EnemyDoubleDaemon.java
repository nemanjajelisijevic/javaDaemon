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
import com.daemonize.graphics2d.scene.views.ImageView;
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

public class EnemyDoubleDaemon implements EagerDaemon<EnemyDoubleDaemon>, Target<EnemyDoubleDaemon>, Paralyzable<EnemyDoubleDaemon> {
  private Enemy prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine goToDaemonEngine;

  protected EagerMainQuestDaemonEngine reloadDaemonEngine;

  public EnemyDoubleDaemon(Consumer consumer, Enemy prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.goToDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - goToDaemonEngine");
    this.reloadDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - reloadDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Enemy#animateEnemy} */
  public SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setAnimateEnemySideQuest(Consumer consumer) {
    SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateEnemySideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public EnemyDoubleDaemon setParalyzed(boolean paralyzed) {
    prototype.setParalyzed(paralyzed);
    return this;
  }

  @Override
  public EnemyDoubleDaemon setHp(int hp) {
    prototype.setHp(hp);
    return this;
  }

  public EnemyDoubleDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public EnemyDoubleDaemon setTargetView(ImageView targetview) {
    prototype.setTargetView(targetview);
    return this;
  }

  public EnemyDoubleDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public Target getTarget() {
    return prototype.getTarget();
  }

  public int getSize() {
    return prototype.getSize();
  }

  @Override
  public int getMaxHp() {
    return prototype.getMaxHp();
  }

  @Override
  public boolean isAttackable() {
    return prototype.isAttackable();
  }

  @Override
  public int getHp() {
    return prototype.getHp();
  }

  public EnemyDoubleDaemon setPreviousField(Pair<Integer, Integer> previousfield) {
    prototype.setPreviousField(previousfield);
    return this;
  }

  public ImageView getParalyzedView() {
    return prototype.getParalyzedView();
  }

  @Override
  public Movable.AnimationWaiter getAnimationWaiter() {
    return prototype.getAnimationWaiter();
  }

  @Override
  public EnemyDoubleDaemon setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
    return this;
  }

  @Override
  public EnemyDoubleDaemon setAttackable(boolean attackable) {
    prototype.setAttackable(attackable);
    return this;
  }

  public EnemyDoubleDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public ImageMover.PositionedImage animate() throws InterruptedException {
    return prototype.animate();
  }

  @Override
  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  public EnemyDoubleDaemon setTarget(Target target) {
    prototype.setTarget(target);
    return this;
  }

  @Override
  public EnemyDoubleDaemon destroy() {
    prototype.destroy();
    return this;
  }

  @Override
  public boolean isParalyzed() {
    return prototype.isParalyzed();
  }

  public GenericNode<Pair<ImageMover.PositionedImage, ImageView>> animateEnemy() throws
      InterruptedException {
    return prototype.animateEnemy();
  }

  @Override
  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public EnemyDoubleDaemon setHealthBarImage(Image[] healthbarimage) {
    prototype.setHealthBarImage(healthbarimage);
    return this;
  }

  public EnemyDoubleDaemon setView(ImageView view) {
    prototype.setView(view);
    return this;
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public EnemyDoubleDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public EnemyDoubleDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public ImageView getHpView() {
    return prototype.getHpView();
  }

  public boolean redirect(float x, float y) {
    return prototype.redirect(x, y);
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public ImageView getView() {
    return prototype.getView();
  }

  public EnemyDoubleDaemon setSpriteIterator(SpriteIterator spriteiterator) {
    prototype.setSpriteIterator(spriteiterator);
    return this;
  }

  public Pair<Integer, Integer> getPreviousField() {
    return prototype.getPreviousField();
  }

  public boolean setDirectionToPoint(float x, float y) {
    return prototype.setDirectionToPoint(x, y);
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  public EnemyDoubleDaemon setParalyzedImage(Image paralyzedimage) {
    prototype.setParalyzedImage(paralyzedimage);
    return this;
  }

  public ImageView getTargetView() {
    return prototype.getTargetView();
  }

  public double absDistance(float x1, float y1, float x2, float y2) {
    return prototype.absDistance(x1, y1, x2, y2);
  }

  public Image iterateSprite() {
    return prototype.iterateSprite();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public EnemyDoubleDaemon setHpView(ImageView hpview) {
    prototype.setHpView(hpview);
    return this;
  }

  public double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
    return prototype.absDistance(source, dest);
  }

  public EnemyDoubleDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public EnemyDoubleDaemon setParalyzedView(ImageView paralyzedview) {
    prototype.setParalyzedView(paralyzedview);
    return this;
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#redir} */
  public EnemyDoubleDaemon redir(float x, float y) {
    mainDaemonEngine.pursueQuest(new RedirMainQuest(x, y).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#pushSprite} */
  public EnemyDoubleDaemon pushSprite(Image[] sprite, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, retRun, mainDaemonEngine.getClosureAwaiter()).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#rotAndGo} */
  public EnemyDoubleDaemon rotAndGo(Pair<Float, Float> coords, float velocityint) {
    goToDaemonEngine.pursueQuest(new RotAndGoMainQuest(coords, velocityint).setConsumer(goToDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#rotAndGo} */
  public EnemyDoubleDaemon rotAndGo(float x, float y, float velocityint) {
    goToDaemonEngine.pursueQuest(new RotAndGoIMainQuest(x, y, velocityint).setConsumer(goToDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#go} */
  public EnemyDoubleDaemon go(float x, float y, float velocityint) {
    goToDaemonEngine.pursueQuest(new GoMainQuest(x, y, velocityint).setConsumer(goToDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#reload} */
  public EnemyDoubleDaemon reload(Closure<Target> closure) {
    reloadDaemonEngine.pursueQuest(new ReloadMainQuest(closure, null).setConsumer(reloadDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#rotateTowards} */
  public EnemyDoubleDaemon rotateTowards(Pair<Float, Float> coords) {
    mainDaemonEngine.pursueQuest(new RotateTowardsMainQuest(coords).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#rotate} */
  public EnemyDoubleDaemon rotate(int angle) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#goTo} */
  public EnemyDoubleDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    goToDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure, null).setConsumer(goToDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#rotateTowards} */
  public EnemyDoubleDaemon rotateTowards(float x, float y) {
    mainDaemonEngine.pursueQuest(new RotateTowardsIMainQuest(x, y).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public EnemyDoubleDaemon goTo(Pair<Float, Float> coords, float velocity,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToIMainQuest(coords, velocity, closure, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  public Enemy getPrototype() {
    return prototype;
  }

  public EnemyDoubleDaemon setPrototype(Enemy prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public EnemyDoubleDaemon start() {
    mainDaemonEngine.start();
    goToDaemonEngine.start();
    reloadDaemonEngine.start();
    sideDaemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
    goToDaemonEngine.stop();
    reloadDaemonEngine.stop();
  }

  @Override
  public EnemyDoubleDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public EnemyDoubleDaemon clear() {
    mainDaemonEngine.clear();
    goToDaemonEngine.clear();
    reloadDaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(mainDaemonEngine.getState());
    ret.add(goToDaemonEngine.getState());
    ret.add(reloadDaemonEngine.getState());
    ret.add(sideDaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(mainDaemonEngine.queueSize());
    ret.add(goToDaemonEngine.queueSize());
    ret.add(reloadDaemonEngine.queueSize());
    return ret;
  }

  @Override
  public EnemyDoubleDaemon setName(String engineName) {
    mainDaemonEngine.setName(engineName);
    sideDaemonEngine.setName(engineName + " - SIDE");
    goToDaemonEngine.setName(engineName + " - goToDaemonEngine");
    reloadDaemonEngine.setName(engineName + " - reloadDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public EnemyDoubleDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    goToDaemonEngine.setConsumer(consumer);
    reloadDaemonEngine.setConsumer(consumer);
    return this;
  }

  public EnemyDoubleDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public EnemyDoubleDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public EnemyDoubleDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    goToDaemonEngine.setUncaughtExceptionHandler(handler);
    reloadDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public EnemyDoubleDaemon interrupt() {
    mainDaemonEngine.interrupt();
    goToDaemonEngine.interrupt();
    reloadDaemonEngine.interrupt();
    return this;
  }

  @Override
  public EnemyDoubleDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    goToDaemonEngine.clearAndInterrupt();
    reloadDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateEnemySideQuest extends SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateEnemySideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animateEnemy";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateEnemy();
    }
  }

  private final class RedirMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private RedirMainQuest(float x, float y) {
      super();
      this.x = x;
      this.y = y;
      this.description = "redir";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.redir(x, y);
      return null;
    }
  }

  private final class PushSpriteMainQuest extends ReturnVoidMainQuest {
    private Image[] sprite;

    private PushSpriteMainQuest(Image[] sprite, Runnable retRun,
        ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.sprite = sprite;
      this.description = "pushSprite";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.pushSprite(sprite);
      return null;
    }
  }

  private final class RotAndGoMainQuest extends VoidMainQuest {
    private Pair<Float, Float> coords;

    private float velocityint;

    private RotAndGoMainQuest(Pair<Float, Float> coords, float velocityint) {
      super();
      this.coords = coords;
      this.velocityint = velocityint;
      this.description = "rotAndGo";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotAndGo(coords, velocityint);
      return null;
    }
  }

  private final class RotAndGoIMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private float velocityint;

    private RotAndGoIMainQuest(float x, float y, float velocityint) {
      super();
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "rotAndGo";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotAndGo(x, y, velocityint);
      return null;
    }
  }

  private final class GoMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private float velocityint;

    private GoMainQuest(float x, float y, float velocityint) {
      super();
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "go";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.go(x, y, velocityint);
      return null;
    }
  }

  private final class ReloadMainQuest extends MainQuest<Target> {
    private ReloadMainQuest(Closure<Target> closure, ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.description = "reload";
    }

    @Override
    public final Target pursue() throws Exception {
      return prototype.reload();
    }
  }

  private final class RotateTowardsMainQuest extends VoidMainQuest {
    private Pair<Float, Float> coords;

    private RotateTowardsMainQuest(Pair<Float, Float> coords) {
      super();
      this.coords = coords;
      this.description = "rotateTowards";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotateTowards(coords);
      return null;
    }
  }

  private final class RotateMainQuest extends VoidMainQuest {
    private int angle;

    private RotateMainQuest(int angle) {
      super();
      this.angle = angle;
      this.description = "rotate";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotate(angle);
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
