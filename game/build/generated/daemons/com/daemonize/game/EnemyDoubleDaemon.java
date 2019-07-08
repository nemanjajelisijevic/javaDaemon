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
import com.daemonize.game.scene.views.ImageView;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class EnemyDoubleDaemon implements EagerDaemon<EnemyDoubleDaemon>, Target<EnemyDoubleDaemon> {
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
    SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateEnemySideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  @Override
  public int getHp() {
    return prototype.getHp();
  }

  public EnemyDoubleDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
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

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public EnemyDoubleDaemon setShootable(boolean shootable) {
    prototype.setShootable(shootable);
    return this;
  }

  public EnemyDoubleDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public ImageView getTargetView() {
    return prototype.getTargetView();
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public EnemyDoubleDaemon setTarget(Target target) {
    prototype.setTarget(target);
    return this;
  }

  @Override
  public boolean isParalyzed() {
    return prototype.isParalyzed();
  }

  @Override
  public int getMaxHp() {
    return prototype.getMaxHp();
  }

  public EnemyDoubleDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public EnemyDoubleDaemon setOutOfBordersConsumer(Consumer consumer) {
    prototype.setOutOfBordersConsumer(consumer);
    return this;
  }

  public EnemyDoubleDaemon setHp(int hp) {
    prototype.setHp(hp);
    return this;
  }

  public EnemyDoubleDaemon setHpView(ImageView hpview) {
    prototype.setHpView(hpview);
    return this;
  }

  public EnemyDoubleDaemon setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
    return this;
  }

  public EnemyDoubleDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public Target getTarget() {
    return prototype.getTarget();
  }

  public EnemyDoubleDaemon setParalyzed(boolean paralyzed) {
    prototype.setParalyzed(paralyzed);
    return this;
  }

  public EnemyDoubleDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public EnemyDoubleDaemon pause() {
    prototype.pause();
    return this;
  }

  public EnemyDoubleDaemon cont() {
    prototype.cont();
    return this;
  }

  @Override
  public boolean isShootable() {
    return prototype.isShootable();
  }

  public EnemyDoubleDaemon setView(ImageView view) {
    prototype.setView(view);
    return this;
  }

  public Pair<Integer, Integer> getPreviousField() {
    return prototype.getPreviousField();
  }

  public EnemyDoubleDaemon setTargetView(ImageView targetview) {
    prototype.setTargetView(targetview);
    return this;
  }

  public EnemyDoubleDaemon setOutOfBordersClosure(Runnable closure) {
    prototype.setOutOfBordersClosure(closure);
    return this;
  }

  public EnemyDoubleDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public ImageView getView() {
    return prototype.getView();
  }

  public ImageView getHpView() {
    return prototype.getHpView();
  }

  public int getSize() {
    return prototype.getSize();
  }

  public EnemyDoubleDaemon setPreviousField(Pair<Integer, Integer> previousfield) {
    prototype.setPreviousField(previousfield);
    return this;
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#setBorders} */
  public EnemyDoubleDaemon setBorders(float x1, float x2, float y1, float y2,
      Closure<ImageTranslationMover> closure) {
    mainDaemonEngine.pursueQuest(new SetBordersMainQuest(x1, x2, y1, y2, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#rotate} */
  public EnemyDoubleDaemon rotate(int angle) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#pushSprite} */
  public EnemyDoubleDaemon pushSprite(Image[] sprite, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#animateEnemy} */
  public EnemyDoubleDaemon animateEnemy(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateEnemyMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#setHealthBarImage} */
  public EnemyDoubleDaemon setHealthBarImage(Image[] healthbarimage, Closure<Enemy> closure) {
    mainDaemonEngine.pursueQuest(new SetHealthBarImageMainQuest(healthbarimage, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.CoordinatedImageTranslationMover#animate} */
  public EnemyDoubleDaemon animate(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#goTo} */
  public EnemyDoubleDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    goToDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure).setConsumer(goToDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageTranslationMover#setDirectionAndMove} */
  public EnemyDoubleDaemon setDirectionAndMove(float x, float y, float velocityint,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new SetDirectionAndMoveMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#reload} */
  public EnemyDoubleDaemon reload(Closure<Target> closure) {
    reloadDaemonEngine.pursueQuest(new ReloadMainQuest(closure).setConsumer(reloadDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Enemy#iterateSprite} */
  public EnemyDoubleDaemon iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
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
  public EnemyDoubleDaemon setName(String name) {
    mainDaemonEngine.setName(name);
    sideDaemonEngine.setName(name + " - SIDE");
    goToDaemonEngine.setName(name + " - goToDaemonEngine");
    reloadDaemonEngine.setName(name + " - reloadDaemonEngine");
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
    private AnimateEnemySideQuest() {
      this.description = "animateEnemy";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateEnemy();
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

  private final class RotateMainQuest extends VoidMainQuest {
    private int angle;

    private RotateMainQuest(int angle) {
      setVoid();
      this.angle = angle;
      this.description = "rotate";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotate(angle);
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

  private final class AnimateEnemyMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateEnemyMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "animateEnemy";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateEnemy();
    }
  }

  private final class SetHealthBarImageMainQuest extends MainQuest<Enemy> {
    private Image[] healthbarimage;

    private SetHealthBarImageMainQuest(Image[] healthbarimage, Closure<Enemy> closure) {
      super(closure);
      this.healthbarimage = healthbarimage;
      this.description = "setHealthBarImage";
    }

    @Override
    public final Enemy pursue() throws Exception {
      return prototype.setHealthBarImage(healthbarimage);
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

  private final class ReloadMainQuest extends MainQuest<Target> {
    private ReloadMainQuest(Closure<Target> closure) {
      super(closure);
      this.description = "reload";
    }

    @Override
    public final Target pursue() throws Exception {
      return prototype.reload();
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
