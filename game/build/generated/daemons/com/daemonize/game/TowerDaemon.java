package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.InterruptibleSleepSideQuest;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.ReturnVoidMainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;
import com.daemonize.imagemovers.spriteiterators.SpriteIterator;
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

public class TowerDaemon implements EagerDaemon<TowerDaemon>, Target<TowerDaemon>, Shooter, Paralyzable<TowerDaemon> {
  private Tower prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine scanDaemonEngine;

  public TowerDaemon(Consumer consumer, Tower prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.scanDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - scanDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Tower#animateTower} */
  public SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setAnimateTowerSideQuest(Consumer consumer) {
    SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateTowerSideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  /**
   * Prototype method {@link Tower#deactivateTower} */
  public InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setDeactivateTowerSideQuest(Consumer consumer) {
    InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new DeactivateTowerSideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  /**
   * Prototype method {@link Tower#activateTower} */
  public InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setActivateTowerSideQuest(Consumer consumer) {
    InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new ActivateTowerSideQuest(sideDaemonEngine.getClosureAwaiter());
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public TowerDaemon setHp(int hp) {
    prototype.setHp(hp);
    return this;
  }

  @Override
  public TowerDaemon setParalyzed(boolean paralyzed) {
    prototype.setParalyzed(paralyzed);
    return this;
  }

  public TowerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public Tower.TowerLevel getTowerLevel() {
    return prototype.getTowerLevel();
  }

  public TowerDaemon setVelocity(ImageMover.Velocity velocity) {
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
  public boolean isAttackable() {
    return prototype.isAttackable();
  }

  public double getAbsoluteAngle(double angle) {
    return prototype.getAbsoluteAngle(angle);
  }

  @Override
  public int getHp() {
    return prototype.getHp();
  }

  public Tower.TowerType getTowertype() {
    return prototype.getTowertype();
  }

  public TowerDaemon levelUp() {
    prototype.levelUp();
    return this;
  }

  @Override
  public Movable.AnimationWaiter getAnimationWaiter() {
    return prototype.getAnimationWaiter();
  }

  @Override
  public TowerDaemon setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
    return this;
  }

  public GenericNode<Pair<ImageMover.PositionedImage, ImageView>> animateTower() throws
      InterruptedException {
    return prototype.animateTower();
  }

  @Override
  public TowerDaemon setAttackable(boolean attackable) {
    prototype.setAttackable(attackable);
    return this;
  }

  public TowerDaemon setTowerLevel(Tower.TowerLevel towerlevel) {
    prototype.setTowerLevel(towerlevel);
    return this;
  }

  public TowerDaemon setDirection(ImageMover.Direction direction) {
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

  @Override
  public TowerDaemon destroy() {
    prototype.destroy();
    return this;
  }

  @Override
  public boolean isParalyzed() {
    return prototype.isParalyzed();
  }

  public TowerDaemon setCurrentAngle(int currentangle) {
    prototype.setCurrentAngle(currentangle);
    return this;
  }

  public GenericNode<Pair<ImageMover.PositionedImage, ImageView>> deactivateTower() throws
      InterruptedException {
    return prototype.deactivateTower();
  }

  @Override
  public float getRange() {
    return prototype.getRange();
  }

  @Override
  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public TowerDaemon setView(ImageView view) {
    prototype.setView(view);
    return this;
  }

  public TowerDaemon setRotaterName(String name) {
    prototype.setRotaterName(name);
    return this;
  }

  public TowerDaemon setRotationSprite(Image[] rotationsprite) {
    prototype.setRotationSprite(rotationsprite);
    return this;
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public TowerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public double getAngle(Pair<Float, Float> one, Pair<Float, Float> two) {
    return prototype.getAngle(one, two);
  }

  public TowerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public ImageView getHpView() {
    return prototype.getHpView();
  }

  public TowerDaemon prepareForActivation() {
    prototype.prepareForActivation();
    return this;
  }

  public double getAngle(float x1, float y1, float x2, float y2) {
    return prototype.getAngle(x1, y1, x2, y2);
  }

  public ImageView getView() {
    return prototype.getView();
  }

  public TowerDaemon setSpriteIterator(SpriteIterator spriteiterator) {
    prototype.setSpriteIterator(spriteiterator);
    return this;
  }

  public TowerDaemon rotate(int targetangle) throws InterruptedException {
    prototype.rotate(targetangle);
    return this;
  }

  public boolean setDirectionToPoint(float x, float y) {
    return prototype.setDirectionToPoint(x, y);
  }

  public TowerDaemon addTarget(Target target) {
    prototype.addTarget(target);
    return this;
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  public String toString() {
    return prototype.toString();
  }

  public double absDistance(float x1, float y1, float x2, float y2) {
    return prototype.absDistance(x1, y1, x2, y2);
  }

  public TowerDaemon rotateTowards(float lastx, float lasty, float targetx, float targety) throws
      InterruptedException {
    prototype.rotateTowards(lastx, lasty, targetx, targety);
    return this;
  }

  public TowerDaemon prepareForDeactivation() {
    prototype.prepareForDeactivation();
    return this;
  }

  public Image iterateSprite() {
    return prototype.iterateSprite();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public TowerDaemon setHpView(ImageView hpview) {
    prototype.setHpView(hpview);
    return this;
  }

  public double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
    return prototype.absDistance(source, dest);
  }

  public TowerDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public GenericNode<Pair<ImageMover.PositionedImage, ImageView>> activateTower() throws
      InterruptedException {
    return prototype.activateTower();
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#pushSprite} */
  public TowerDaemon pushSprite(Image[] sprite, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, retRun, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#updateSprite} */
  public TowerDaemon updateSprite(Consumer consumer,
      Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new UpdateSpriteMainQuest(closure, null).setConsumer(consumer));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#reload} */
  public TowerDaemon reload(long millis, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new ReloadMainQuest(millis, retRun, null).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#scan} */
  public TowerDaemon scan(Closure<Pair<Tower.TowerType, Target>> closure) {
    scanDaemonEngine.pursueQuest(new ScanMainQuest(closure, null).setConsumer(scanDaemonEngine.getConsumer()));
    return this;
  }

  public Tower getPrototype() {
    return prototype;
  }

  public TowerDaemon setPrototype(Tower prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public TowerDaemon start() {
    mainDaemonEngine.start();
    scanDaemonEngine.start();
    sideDaemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
    scanDaemonEngine.stop();
  }

  @Override
  public TowerDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public TowerDaemon clear() {
    mainDaemonEngine.clear();
    scanDaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(mainDaemonEngine.getState());
    ret.add(scanDaemonEngine.getState());
    ret.add(sideDaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(mainDaemonEngine.queueSize());
    ret.add(scanDaemonEngine.queueSize());
    return ret;
  }

  @Override
  public TowerDaemon setName(String engineName) {
    mainDaemonEngine.setName(engineName);
    sideDaemonEngine.setName(engineName + " - SIDE");
    scanDaemonEngine.setName(engineName + " - scanDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public TowerDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    scanDaemonEngine.setConsumer(consumer);
    return this;
  }

  public TowerDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public TowerDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public TowerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    scanDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public TowerDaemon interrupt() {
    mainDaemonEngine.interrupt();
    scanDaemonEngine.interrupt();
    return this;
  }

  @Override
  public TowerDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    scanDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateTowerSideQuest extends SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateTowerSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "animateTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateTower();
    }
  }

  private final class DeactivateTowerSideQuest extends InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private DeactivateTowerSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "deactivateTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.deactivateTower();
    }
  }

  private final class ActivateTowerSideQuest extends InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private ActivateTowerSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "activateTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.activateTower();
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

  private final class UpdateSpriteMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private UpdateSpriteMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.description = "updateSprite";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.updateSprite();
    }
  }

  private final class ReloadMainQuest extends ReturnVoidMainQuest {
    private long millis;

    private ReloadMainQuest(long millis, Runnable retRun, ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.millis = millis;
      this.description = "reload";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.reload(millis);
      return null;
    }
  }

  private final class ScanMainQuest extends MainQuest<Pair<Tower.TowerType, Target>> {
    private ScanMainQuest(Closure<Pair<Tower.TowerType, Target>> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.description = "scan";
    }

    @Override
    public final Pair<Tower.TowerType, Target> pursue() throws Exception {
      return prototype.scan();
    }
  }
}
