package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.InterruptibleSleepSideQuest;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.imagemovers.ImageTranslationMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.scene.views.ImageView;
import java.lang.Boolean;
import java.lang.Double;
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

public class TowerDaemon implements EagerDaemon<TowerDaemon>, Target<TowerDaemon> {
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
    SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateTowerSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  /**
   * Prototype method {@link Tower#initTower} */
  public InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setInitTowerSideQuest(Consumer consumer) {
    InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new InitTowerSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public int getHp() {
    return prototype.getHp();
  }

  public TowerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public Tower.TowerLevel getTowerLevel() {
    return prototype.getTowerLevel();
  }

  public TowerDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public Tower.TowerType getTowertype() {
    return prototype.getTowertype();
  }

  public TowerDaemon setOutOfBordersClosure(Runnable closure) {
    prototype.setOutOfBordersClosure(closure);
    return this;
  }

  public float getRange() {
    return prototype.getRange();
  }

  public TowerDaemon setHpView(ImageView hpview) {
    prototype.setHpView(hpview);
    return this;
  }

  public TowerDaemon setView(ImageView view) {
    prototype.setView(view);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  public boolean addTarget(Target target) {
    return prototype.addTarget(target);
  }

  public TowerDaemon setHp(int hp) {
    prototype.setHp(hp);
    return this;
  }

  @Override
  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  public TowerDaemon contScan() {
    prototype.contScan();
    return this;
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  @Override
  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public String toString() {
    return prototype.toString();
  }

  @Override
  public boolean isParalyzed() {
    return prototype.isParalyzed();
  }

  @Override
  public int getMaxHp() {
    return prototype.getMaxHp();
  }

  public TowerDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public TowerDaemon setOutOfBordersConsumer(Consumer consumer) {
    prototype.setOutOfBordersConsumer(consumer);
    return this;
  }

  public TowerDaemon setCurrentAngle(int currentangle) {
    prototype.setCurrentAngle(currentangle);
    return this;
  }

  public TowerDaemon setRotationSprite(Image[] rotationsprite) {
    prototype.setRotationSprite(rotationsprite);
    return this;
  }

  public TowerDaemon levelUp() {
    prototype.levelUp();
    return this;
  }

  public TowerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public TowerDaemon pause() {
    prototype.pause();
    return this;
  }

  public TowerDaemon cont() {
    prototype.cont();
    return this;
  }

  public TowerDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  @Override
  public boolean isShootable() {
    return prototype.isShootable();
  }

  public TowerDaemon setTowerLevel(Tower.TowerLevel towerlevel) {
    prototype.setTowerLevel(towerlevel);
    return this;
  }

  public TowerDaemon pauseScan() {
    prototype.pauseScan();
    return this;
  }

  public TowerDaemon setParalyzed(boolean paralyzed) {
    prototype.setParalyzed(paralyzed);
    return this;
  }

  public TowerDaemon setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
    return this;
  }

  public TowerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public ImageView getView() {
    return prototype.getView();
  }

  public TowerDaemon setShootable(boolean shootable) {
    prototype.setShootable(shootable);
    return this;
  }

  public ImageView getHpView() {
    return prototype.getHpView();
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.RotatingSpriteImageMover#rotateTowards} */
  public TowerDaemon rotateTowards(float x, float y) {
    mainDaemonEngine.pursueQuest(new RotateTowardsMainQuest(x, y).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.RotatingSpriteImageMover#rotate} */
  public TowerDaemon rotate(int targetangle) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(targetangle).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#pushSprite} */
  public TowerDaemon pushSprite(Image[] sprite, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#updateSprite} */
  public TowerDaemon updateSprite(Consumer consumer,
      Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new UpdateSpriteMainQuest(closure).setConsumer(consumer));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.RotatingSpriteImageMover#getRotationSprite} */
  public TowerDaemon getRotationSprite(int targetangle, Closure<Image[]> closure) {
    mainDaemonEngine.pursueQuest(new GetRotationSpriteMainQuest(targetangle, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.RotatingSpriteImageMover#getAngle} */
  public TowerDaemon getAngle(float x1, float y1, float x2, float y2, Closure<Double> closure) {
    mainDaemonEngine.pursueQuest(new GetAngleMainQuest(x1, y1, x2, y2, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#animateTower} */
  public TowerDaemon animateTower(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateTowerMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#reload} */
  public TowerDaemon reload(long millis, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new ReloadMainQuest(millis, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.RotatingSpriteImageMover#getAbsoluteAngle} */
  public TowerDaemon getAbsoluteAngle(double angle, Closure<Double> closure) {
    mainDaemonEngine.pursueQuest(new GetAbsoluteAngleMainQuest(angle, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#setBorders} */
  public TowerDaemon setBorders(float x1, float x2, float y1, float y2,
      Closure<ImageTranslationMover> closure) {
    mainDaemonEngine.pursueQuest(new SetBordersMainQuest(x1, x2, y1, y2, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#scan} */
  public TowerDaemon scan(Closure<Pair<Tower.TowerType, Target>> closure) {
    scanDaemonEngine.pursueQuest(new ScanMainQuest(closure).setConsumer(scanDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#setHealthBarImage} */
  public TowerDaemon setHealthBarImage(Image[] healthbarimage, Closure<Tower> closure) {
    mainDaemonEngine.pursueQuest(new SetHealthBarImageMainQuest(healthbarimage, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.RotatingSpriteImageMover#getAngle} */
  public TowerDaemon getAngle(Pair<Float, Float> one, Pair<Float, Float> two,
      Closure<Double> closure) {
    mainDaemonEngine.pursueQuest(new GetAngleIMainQuest(one, two, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#animate} */
  public TowerDaemon animate(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#setDirectionAndMove} */
  public TowerDaemon setDirectionAndMove(float x, float y, float velocityint,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new SetDirectionAndMoveMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Tower#initTower} */
  public TowerDaemon initTower(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new InitTowerMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.CachedArraySpriteImageMover#iterateSprite} */
  public TowerDaemon iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
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
  public TowerDaemon setName(String name) {
    mainDaemonEngine.setName(name);
    sideDaemonEngine.setName(name + " - SIDE");
    scanDaemonEngine.setName(name + " - scanDaemonEngine");
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
    private AnimateTowerSideQuest() {
      this.description = "animateTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateTower();
    }
  }

  private final class InitTowerSideQuest extends InterruptibleSleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private InitTowerSideQuest() {
      this.description = "initTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.initTower();
    }
  }

  private final class RotateTowardsMainQuest extends VoidMainQuest {
    private float x;

    private float y;

    private RotateTowardsMainQuest(float x, float y) {
      setVoid();
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

  private final class RotateMainQuest extends VoidMainQuest {
    private int targetangle;

    private RotateMainQuest(int targetangle) {
      setVoid();
      this.targetangle = targetangle;
      this.description = "rotate";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotate(targetangle);
      return null;
    }
  }

  private final class PushSpriteMainQuest extends VoidMainQuest {
    private Image[] sprite;

    private float velocity;

    private PushSpriteMainQuest(Image[] sprite, float velocity, Runnable retRun) {
      super(retRun);
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

  private final class UpdateSpriteMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private UpdateSpriteMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "updateSprite";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.updateSprite();
    }
  }

  private final class GetRotationSpriteMainQuest extends MainQuest<Image[]> {
    private int targetangle;

    private GetRotationSpriteMainQuest(int targetangle, Closure<Image[]> closure) {
      super(closure);
      this.targetangle = targetangle;
      this.description = "getRotationSprite";
    }

    @Override
    public final Image[] pursue() throws Exception {
      return prototype.getRotationSprite(targetangle);
    }
  }

  private final class GetAngleMainQuest extends MainQuest<Double> {
    private float x1;

    private float y1;

    private float x2;

    private float y2;

    private GetAngleMainQuest(float x1, float y1, float x2, float y2, Closure<Double> closure) {
      super(closure);
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.description = "getAngle";
    }

    @Override
    public final Double pursue() throws Exception {
      return Tower.getAngle(x1, y1, x2, y2);
    }
  }

  private final class AnimateTowerMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateTowerMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "animateTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateTower();
    }
  }

  private final class ReloadMainQuest extends VoidMainQuest {
    private long millis;

    private ReloadMainQuest(long millis, Runnable retRun) {
      super(retRun);
      this.millis = millis;
      this.description = "reload";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.reload(millis);
      return null;
    }
  }

  private final class GetAbsoluteAngleMainQuest extends MainQuest<Double> {
    private double angle;

    private GetAbsoluteAngleMainQuest(double angle, Closure<Double> closure) {
      super(closure);
      this.angle = angle;
      this.description = "getAbsoluteAngle";
    }

    @Override
    public final Double pursue() throws Exception {
      return Tower.getAbsoluteAngle(angle);
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

  private final class ScanMainQuest extends MainQuest<Pair<Tower.TowerType, Target>> {
    private ScanMainQuest(Closure<Pair<Tower.TowerType, Target>> closure) {
      super(closure);
      this.description = "scan";
    }

    @Override
    public final Pair<Tower.TowerType, Target> pursue() throws Exception {
      return prototype.scan();
    }
  }

  private final class SetHealthBarImageMainQuest extends MainQuest<Tower> {
    private Image[] healthbarimage;

    private SetHealthBarImageMainQuest(Image[] healthbarimage, Closure<Tower> closure) {
      super(closure);
      this.healthbarimage = healthbarimage;
      this.description = "setHealthBarImage";
    }

    @Override
    public final Tower pursue() throws Exception {
      return prototype.setHealthBarImage(healthbarimage);
    }
  }

  private final class GetAngleIMainQuest extends MainQuest<Double> {
    private Pair<Float, Float> one;

    private Pair<Float, Float> two;

    private GetAngleIMainQuest(Pair<Float, Float> one, Pair<Float, Float> two,
        Closure<Double> closure) {
      super(closure);
      this.one = one;
      this.two = two;
      this.description = "getAngle";
    }

    @Override
    public final Double pursue() throws Exception {
      return Tower.getAngle(one, two);
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

  private final class InitTowerMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private InitTowerMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "initTower";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.initTower();
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
