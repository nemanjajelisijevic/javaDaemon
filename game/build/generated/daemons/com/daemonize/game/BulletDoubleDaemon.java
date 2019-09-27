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
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class BulletDoubleDaemon implements EagerDaemon<BulletDoubleDaemon> {
  private Bullet prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public BulletDoubleDaemon(Consumer consumer, Bullet prototype) {
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

  public boolean redirect(float x, float y) {
    return prototype.redirect(x, y);
  }

  public BulletDoubleDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public BulletDoubleDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public BulletDoubleDaemon setView3(ImageView view3) {
    prototype.setView3(view3);
    return this;
  }

  public int getSize() {
    return prototype.getSize();
  }

  public int getDamage() {
    return prototype.getDamage();
  }

  public BulletDoubleDaemon setLevel(int level) {
    prototype.setLevel(level);
    return this;
  }

  public boolean setDirectionToPoint(float x, float y) {
    return prototype.setDirectionToPoint(x, y);
  }

  public BulletDoubleDaemon setBorders(float x1, float x2, float y1, float y2) {
    prototype.setBorders(x1, x2, y1, y2);
    return this;
  }

  public BulletDoubleDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public ImageMover.PositionedImage animate() throws InterruptedException {
    return prototype.animate();
  }

  public BulletDoubleDaemon setDamage(int damage) {
    prototype.setDamage(damage);
    return this;
  }

  public BulletDoubleDaemon setVelocity(float velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  public List<ImageView> getViews() {
    return prototype.getViews();
  }

  public BulletDoubleDaemon pause() {
    prototype.pause();
    return this;
  }

  public String toString() {
    return prototype.toString();
  }

  public BulletDoubleDaemon setView2(ImageView view2) {
    prototype.setView2(view2);
    return this;
  }

  public BulletDoubleDaemon setCurrentAngle(int angle) {
    prototype.setCurrentAngle(angle);
    return this;
  }

  public Image iterateSprite() {
    return prototype.iterateSprite();
  }

  public BulletDoubleDaemon setOutOfBordersClosure(Runnable closure) {
    prototype.setOutOfBordersClosure(closure);
    return this;
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public BulletDoubleDaemon setView(ImageView view) {
    prototype.setView(view);
    return this;
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public ImageTranslationMover setSprite(Image[] sprite) {
    return prototype.setSprite(sprite);
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public BulletDoubleDaemon cont() {
    prototype.cont();
    return this;
  }

  public GenericNode<Pair<ImageMover.PositionedImage, ImageView>> animateBullet() throws
      InterruptedException {
    return prototype.animateBullet();
  }

  public BulletDoubleDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public BulletDoubleDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public BulletDoubleDaemon setOutOfBordersConsumer(Consumer consumer) {
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
   * Prototype method {@link com.daemonize.game.Bullet#pushSprite} */
  public BulletDoubleDaemon pushSprite(Image[] sprite, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#rotate} */
  public BulletDoubleDaemon rotate(int angle, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle, retRun).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public BulletDoubleDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.Bullet#rotateAndGoTo} */
  public BulletDoubleDaemon rotateAndGoTo(int angle, float x, float y, float velocityint,
      Closure<Boolean> closure) {
    mainDaemonEngine.pursueQuest(new RotateAndGoToMainQuest(angle, x, y, velocityint, closure).setConsumer(mainDaemonEngine.getConsumer()));
    return this;
  }

  public Bullet getPrototype() {
    return prototype;
  }

  public BulletDoubleDaemon setPrototype(Bullet prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public BulletDoubleDaemon start() {
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
  public BulletDoubleDaemon queueStop() {
    mainDaemonEngine.queueStop(this);
    return this;
  }

  @Override
  public BulletDoubleDaemon clear() {
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
  public BulletDoubleDaemon setName(String name) {
    mainDaemonEngine.setName(name);
    sideDaemonEngine.setName(name + " - SIDE");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public BulletDoubleDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
    return this;
  }

  public BulletDoubleDaemon setSideQuestConsumer(Consumer consumer) {
    sideDaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public BulletDoubleDaemon setConsumer(Consumer consumer) {
    throw new IllegalStateException("This method is unusable in DoubleDaemon. Please use setMainQuestConsumer(Consumer consumer) or setSideQuestConsumer(Consumer consumer)");
  }

  @Override
  public Consumer getConsumer() {
    return mainDaemonEngine.getConsumer();
  }

  @Override
  public BulletDoubleDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    mainDaemonEngine.setUncaughtExceptionHandler(handler);
    sideDaemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public BulletDoubleDaemon interrupt() {
    mainDaemonEngine.interrupt();
    return this;
  }

  @Override
  public BulletDoubleDaemon clearAndInterrupt() {
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
}
