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
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.ImageTranslationMover;
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

public class LaserBulletDaemon implements EagerDaemon<LaserBulletDaemon> {
  private LaserBullet prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public LaserBulletDaemon(Consumer consumer, LaserBullet prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.sideDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Bullet#animateBullet} */
  public SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setAnimateBulletSideQuest(Consumer consumer) {
    SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateBulletSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  /**
   * Prototype method {@link LaserBullet#animateLaser} */
  public SleepSideQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> setAnimateLaserSideQuest(Consumer consumer) {
    SleepSideQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> sideQuest = new AnimateLaserSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  public LaserBulletDaemon setVelocity(float velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  public LaserBulletDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public LaserBulletDaemon setLevel(int level) {
    prototype.setLevel(level);
    return this;
  }

  public LaserBulletDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public LaserBulletDaemon setOutOfBordersConsumer(Consumer consumer) {
    prototype.setOutOfBordersConsumer(consumer);
    return this;
  }

  public List<ImageView> getViews() {
    return prototype.getViews();
  }

  public LaserBulletDaemon setCurrentAngle(int angle) {
    prototype.setCurrentAngle(angle);
    return this;
  }

  public LaserBulletDaemon setView3(ImageView view3) {
    prototype.setView3(view3);
    return this;
  }

  public int getDamage() {
    return prototype.getDamage();
  }

  public LaserBulletDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public LaserBulletDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public LaserBulletDaemon pause() {
    prototype.pause();
    return this;
  }

  public LaserBulletDaemon cont() {
    prototype.cont();
    return this;
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public ImageTranslationMover setSprite(Image[] sprite) {
    return prototype.setSprite(sprite);
  }

  public LaserBulletDaemon setViews(List<ImageView> views) {
    prototype.setViews(views);
    return this;
  }

  public LaserBulletDaemon setOutOfBordersClosure(Runnable closure) {
    prototype.setOutOfBordersClosure(closure);
    return this;
  }

  public LaserBulletDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public LaserBulletDaemon setDamage(int damage) {
    prototype.setDamage(damage);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link com.daemonize.game.LaserBullet#iterateSprite} */
  public LaserBulletDaemon iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#pushSprite} */
  public LaserBulletDaemon pushSprite(Image[] sprite, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#animateBullet} */
  public LaserBulletDaemon animateBullet(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateBulletMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#setView} */
  public LaserBulletDaemon setView(ImageView view, Closure<Bullet> closure) {
    mainDaemonEngine.pursueQuest(new SetViewMainQuest(view, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#rotate} */
  public LaserBulletDaemon rotate(int angle, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#toString} */
  public LaserBulletDaemon toString(Closure<String> closure) {
    mainDaemonEngine.pursueQuest(new ToStringMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.LaserBullet#desintegrateTarget} */
  public LaserBulletDaemon desintegrateTarget(Pair<Float, Float> sourcecoord, Target target,
      long duration, Consumer drawconsumer, Closure<List<ImageView>> closure) {
    mainDaemonEngine.pursueQuest(new DesintegrateTargetMainQuest(sourcecoord, target, duration, drawconsumer, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#rotateAndGoTo} */
  public LaserBulletDaemon rotateAndGoTo(int angle, float x, float y, float velocityint,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new RotateAndGoToMainQuest(angle, x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public LaserBulletDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.ImageTranslationMover#setDirectionAndMove} */
  public LaserBulletDaemon setDirectionAndMove(float x, float y, float velocityint,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new SetDirectionAndMoveMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#animate} */
  public LaserBulletDaemon animate(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.ImageTranslationMover#setBorders} */
  public LaserBulletDaemon setBorders(float x1, float x2, float y1, float y2,
      Closure<ImageTranslationMover> closure) {
    mainDaemonEngine.pursueQuest(new SetBordersMainQuest(x1, x2, y1, y2, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.LaserBullet#animateLaser} */
  public LaserBulletDaemon animateLaser(Closure<List<Pair<ImageView, ImageMover.PositionedImage>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateLaserMainQuest(closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#setView2} */
  public LaserBulletDaemon setView2(ImageView view2, Closure<Bullet> closure) {
    mainDaemonEngine.pursueQuest(new SetView2MainQuest(view2, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  public LaserBullet getPrototype() {
    return prototype;
  }

  public LaserBulletDaemon setPrototype(LaserBullet prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public LaserBulletDaemon start() {
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
  public LaserBulletDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public LaserBulletDaemon clear() {
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
  public LaserBulletDaemon setName(String name) {
    mainDaemonEngine.setName(name);
    sideDaemonEngine.setName(name + " - SIDE");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public LaserBulletDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    return this;
  }

  public LaserBulletDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public LaserBulletDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public LaserBulletDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public LaserBulletDaemon interrupt() {
    mainDaemonEngine.interrupt();
    return this;
  }

  @Override
  public LaserBulletDaemon clearAndInterrupt() {
    mainDaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class AnimateBulletSideQuest extends SleepSideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateBulletSideQuest() {
      super();
      this.description = "animateBullet";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateBullet();
    }
  }

  private final class AnimateLaserSideQuest extends SleepSideQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> {
    private AnimateLaserSideQuest() {
      super();
      this.description = "animateLaser";
    }

    @Override
    public final List<Pair<ImageView, ImageMover.PositionedImage>> pursue() throws Exception {
      return prototype.animateLaser();
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

  private final class AnimateBulletMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateBulletMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "animateBullet";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateBullet();
    }
  }

  private final class SetViewMainQuest extends MainQuest<Bullet> {
    private ImageView view;

    private SetViewMainQuest(ImageView view, Closure<Bullet> closure) {
      super(closure);
      this.view = view;
      this.description = "setView";
    }

    @Override
    public final Bullet pursue() throws Exception {
      return prototype.setView(view);
    }
  }

  private final class RotateMainQuest extends VoidMainQuest {
    private int angle;

    private RotateMainQuest(int angle, Runnable retRun) {
      super(retRun);
      this.angle = angle;
      this.description = "rotate";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.rotate(angle);
      return null;
    }
  }

  private final class ToStringMainQuest extends MainQuest<String> {
    private ToStringMainQuest(Closure<String> closure) {
      super(closure);
      this.description = "toString";
    }

    @Override
    public final String pursue() throws Exception {
      return prototype.toString();
    }
  }

  private final class DesintegrateTargetMainQuest extends MainQuest<List<ImageView>> {
    private Pair<Float, Float> sourcecoord;

    private Target target;

    private long duration;

    private Consumer drawconsumer;

    private DesintegrateTargetMainQuest(Pair<Float, Float> sourcecoord, Target target,
        long duration, Consumer drawconsumer, Closure<List<ImageView>> closure) {
      super(closure);
      this.sourcecoord = sourcecoord;
      this.target = target;
      this.duration = duration;
      this.drawconsumer = drawconsumer;
      this.description = "desintegrateTarget";
    }

    @Override
    public final List<ImageView> pursue() throws Exception {
      return prototype.desintegrateTarget(sourcecoord, target, duration, drawconsumer);
    }
  }

  private final class RotateAndGoToMainQuest extends MainQuest<Boolean> {
    private int angle;

    private float x;

    private float y;

    private float velocityint;

    private RotateAndGoToMainQuest(int angle, float x, float y, float velocityint,
        Closure<Boolean> closure) {
      super(closure);
      this.angle = angle;
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "rotateAndGoTo";
    }

    @Override
    public final Boolean pursue() throws Exception {
      return prototype.rotateAndGoTo(angle, x, y, velocityint);
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

  private final class AnimateLaserMainQuest extends MainQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> {
    private AnimateLaserMainQuest(Closure<List<Pair<ImageView, ImageMover.PositionedImage>>> closure) {
      super(closure);
      this.description = "animateLaser";
    }

    @Override
    public final List<Pair<ImageView, ImageMover.PositionedImage>> pursue() throws Exception {
      return prototype.animateLaser();
    }
  }

  private final class SetView2MainQuest extends MainQuest<Bullet> {
    private ImageView view2;

    private SetView2MainQuest(ImageView view2, Closure<Bullet> closure) {
      super(closure);
      this.view2 = view2;
      this.description = "setView2";
    }

    @Override
    public final Bullet pursue() throws Exception {
      return prototype.setView2(view2);
    }
  }
}
