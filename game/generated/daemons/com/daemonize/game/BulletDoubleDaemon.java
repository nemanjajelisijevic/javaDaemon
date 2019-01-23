package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.imagemovers.ImageTranslationMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.view.ImageView;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Void;
import java.util.List;

public class BulletDoubleDaemon implements Daemon {
  private Bullet prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public BulletDoubleDaemon(Consumer mainConsumer, Consumer sideConsumer, Bullet prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - MAIN");
    this.sideDaemonEngine = new SideQuestDaemonEngine(sideConsumer).setName(this.getClass().getSimpleName() + " - SIDE");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Bullet#animateBullet} */
  public SideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setAnimateBulletSideQuest() {
    SideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateBulletSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25));
    return sideQuest;
  }

  public ImageTranslationMover setSprite(Image[] sprite) {
    return prototype.setSprite(sprite);
  }

  public void setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
  }

  public void setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
  }

  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public void setCurrentAngle(int angle) {
    prototype.setCurrentAngle(angle);
  }

  public int getDamage() {
    return prototype.getDamage();
  }

  public void setDamage(int damage) {
    prototype.setDamage(damage);
  }

  public Bullet setView3(ImageView view3) {
    return prototype.setView3(view3);
  }

  public List<ImageView> getViews() {
    return prototype.getViews();
  }

  public Bullet setLevel(int level) {
    return prototype.setLevel(level);
  }

  public void pause() {
    prototype.pause();
  }

  public void cont() {
    prototype.cont();
  }

  public Bullet setOutOfBordersConsumer(Consumer consumer) {
    return prototype.setOutOfBordersConsumer(consumer);
  }

  public Bullet setOutOfBordersClosure(Runnable outofbordersclosure) {
    return prototype.setOutOfBordersClosure(outofbordersclosure);
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype mapped method {@link Bullet#setView} */
  public BulletDoubleDaemon setView(ImageView view, Closure<Bullet> closure) {
    mainDaemonEngine.pursueQuest(new SetViewMainQuest(view, closure));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#setView2} */
  public BulletDoubleDaemon setView2(ImageView view2, Closure<Bullet> closure) {
    mainDaemonEngine.pursueQuest(new SetView2MainQuest(view2, closure));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#goTo} */
  public BulletDoubleDaemon goTo(float x, float y, float velocityint, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, retRun));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#pushSprite} */
  public BulletDoubleDaemon pushSprite(Image[] sprite, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity, retRun));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#rotate} */
  public BulletDoubleDaemon rotate(int angle, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle, retRun));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#rotateAndGoTo} */
  public BulletDoubleDaemon rotateAndGoTo(int angle, float x, float y, float velocityint,
      Runnable retRun) {
    mainDaemonEngine.pursueQuest(new RotateAndGoToMainQuest(angle, x, y, velocityint, retRun));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#iterateSprite} */
  public BulletDoubleDaemon iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#animate} */
  public BulletDoubleDaemon animate(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMainQuest(closure));
    return this;
  }

  /**
   * Prototype mapped method {@link Bullet#animateBullet} */
  public BulletDoubleDaemon animateBullet(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateBulletMainQuest(closure));
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
  public void start() {
    sideDaemonEngine.start();
  }

  @Override
  public void stop() {
    mainDaemonEngine.stop();
    sideDaemonEngine.stop();
  }

  @Override
  public void queueStop() {
    mainDaemonEngine.queueStop();
    sideDaemonEngine.stop();
  }

  @Override
  public DaemonState getState() {
    return sideDaemonEngine.getState();
  }

  @Override
  public BulletDoubleDaemon setName(String name) {
    mainDaemonEngine.setName(name + " - MAIN");
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

  private final class AnimateBulletSideQuest extends SideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateBulletSideQuest() {
      this.description = "animateBullet";
    }

    @Override
    protected final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
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
    protected final Bullet pursue() throws Exception {
      return prototype.setView(view);
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
    protected final Bullet pursue() throws Exception {
      return prototype.setView2(view2);
    }
  }

  private final class GoToMainQuest extends VoidMainQuest<Void> {
    private float x;

    private float y;

    private float velocityint;

    private GoToMainQuest(float x, float y, float velocityint, Runnable retRun) {
      super(retRun);
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "goTo";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.goTo(x, y, velocityint);
      return null;
    }
  }

  private final class PushSpriteMainQuest extends VoidMainQuest<Void> {
    private Image[] sprite;

    private float velocity;

    private PushSpriteMainQuest(Image[] sprite, float velocity, Runnable retRun) {
      super(retRun);
      this.sprite = sprite;
      this.velocity = velocity;
      this.description = "pushSprite";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.pushSprite(sprite, velocity);
      return null;
    }
  }

  private final class RotateMainQuest extends VoidMainQuest<Void> {
    private int angle;

    private RotateMainQuest(int angle, Runnable retRun) {
      super(retRun);
      this.angle = angle;
      this.description = "rotate";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.rotate(angle);
      return null;
    }
  }

  private final class RotateAndGoToMainQuest extends VoidMainQuest<Void> {
    private int angle;

    private float x;

    private float y;

    private float velocityint;

    private RotateAndGoToMainQuest(int angle, float x, float y, float velocityint,
        Runnable retRun) {
      super(retRun);
      this.angle = angle;
      this.x = x;
      this.y = y;
      this.velocityint = velocityint;
      this.description = "rotateAndGoTo";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.rotateAndGoTo(angle, x, y, velocityint);
      return null;
    }
  }

  private final class IterateSpriteMainQuest extends MainQuest<Image> {
    private IterateSpriteMainQuest(Closure<Image> closure) {
      super(closure);
      this.description = "iterateSprite";
    }

    @Override
    protected final Image pursue() throws Exception {
      return prototype.iterateSprite();
    }
  }

  private final class AnimateMainQuest extends MainQuest<ImageMover.PositionedImage> {
    private AnimateMainQuest(Closure<ImageMover.PositionedImage> closure) {
      super(closure);
      this.description = "animate";
    }

    @Override
    protected final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.animate();
    }
  }

  private final class AnimateBulletMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateBulletMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "animateBullet";
    }

    @Override
    protected final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateBullet();
    }
  }
}
