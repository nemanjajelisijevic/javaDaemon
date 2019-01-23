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
import com.daemonize.game.images.Image;
import com.daemonize.game.view.ImageView;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Void;

public class EnemyDoubleDaemon implements Daemon {
  private Enemy prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine goToDaemonEngine;

  public EnemyDoubleDaemon(Consumer mainConsumer, Consumer sideConsumer, Enemy prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - MAIN");
    this.sideDaemonEngine = new SideQuestDaemonEngine(sideConsumer).setName(this.getClass().getSimpleName() + " - SIDE");
    this.goToDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - goToDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Enemy#animateEnemy} */
  public SideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> setAnimateEnemySideQuest() {
    SideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> sideQuest = new AnimateEnemySideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25));
    return sideQuest;
  }

  public boolean isShootable() {
    return prototype.isShootable();
  }

  public void setShootable(boolean shootable) {
    prototype.setShootable(shootable);
  }

  public int getHp() {
    return prototype.getHp();
  }

  public void setHp(int hp) {
    prototype.setHp(hp);
  }

  public void setMaxHp(int maxhp) {
    prototype.setMaxHp(maxhp);
  }

  public ImageView getView() {
    return prototype.getView();
  }

  public ImageView getHpView() {
    return prototype.getHpView();
  }

  public void setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public void pause() {
    prototype.pause();
  }

  public void cont() {
    prototype.cont();
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public void setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
  }

  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype mapped method {@link Enemy#setHealthBarImage} */
  public void setHealthBarImage(Image[] healthbarimage, Closure<Enemy> closure) {
    mainDaemonEngine.pursueQuest(new SetHealthBarImageMainQuest(healthbarimage, closure));
  }

  /**
   * Prototype mapped method {@link Enemy#setView} */
  public void setView(ImageView view, Closure<Enemy> closure) {
    mainDaemonEngine.pursueQuest(new SetViewMainQuest(view, closure));
  }

  /**
   * Prototype mapped method {@link Enemy#setHpView} */
  public void setHpView(ImageView hpview, Closure<Enemy> closure) {
    mainDaemonEngine.pursueQuest(new SetHpViewMainQuest(hpview, closure));
  }

  /**
   * Prototype mapped method {@link Enemy#pushSprite} */
  public void pushSprite(Image[] sprite, float velocity, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new PushSpriteMainQuest(sprite, velocity, retRun));
  }

  /**
   * Prototype mapped method {@link Enemy#goTo} */
  public void goTo(float x, float y, float velocityint, Runnable retRun) {
    goToDaemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, retRun));
  }

  /**
   * Prototype mapped method {@link Enemy#rotate} */
  public void rotate(int angle) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle));
  }

  /**
   * Prototype mapped method {@link Enemy#iterateSprite} */
  public void iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure));
  }

  /**
   * Prototype mapped method {@link Enemy#animateEnemy} */
  public void animateEnemy(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateEnemyMainQuest(closure));
  }

  public Enemy getPrototype() {
    return prototype;
  }

  public EnemyDoubleDaemon setPrototype(Enemy prototype) {
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
    goToDaemonEngine.stop();
  }

  @Override
  public void queueStop() {
    mainDaemonEngine.queueStop();
    sideDaemonEngine.stop();
    goToDaemonEngine.queueStop();
  }

  @Override
  public DaemonState getState() {
    return sideDaemonEngine.getState();
  }

  @Override
  public EnemyDoubleDaemon setName(String name) {
    mainDaemonEngine.setName(name + " - MAIN");
    sideDaemonEngine.setName(name + " - SIDE");
    goToDaemonEngine.setName(name +" - goToDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public EnemyDoubleDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
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

  private final class AnimateEnemySideQuest extends SideQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateEnemySideQuest() {
      this.description = "animateEnemy";
    }

    @Override
    protected final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
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
    protected final Enemy pursue() throws Exception {
      return prototype.setHealthBarImage(healthbarimage);
    }
  }

  private final class SetViewMainQuest extends MainQuest<Enemy> {
    private ImageView view;

    private SetViewMainQuest(ImageView view, Closure<Enemy> closure) {
      super(closure);
      this.view = view;
      this.description = "setView";
    }

    @Override
    protected final Enemy pursue() throws Exception {
      return prototype.setView(view);
    }
  }

  private final class SetHpViewMainQuest extends MainQuest<Enemy> {
    private ImageView hpview;

    private SetHpViewMainQuest(ImageView hpview, Closure<Enemy> closure) {
      super(closure);
      this.hpview = hpview;
      this.description = "setHpView";
    }

    @Override
    protected final Enemy pursue() throws Exception {
      return prototype.setHpView(hpview);
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

  private final class RotateMainQuest extends MainQuest<Void> {
    private int angle;

    private RotateMainQuest(int angle) {
      setVoid();
      this.angle = angle;
      this.description = "rotate";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.rotate(angle);
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

  private final class AnimateEnemyMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private AnimateEnemyMainQuest(Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
      super(closure);
      this.description = "animateEnemy";
    }

    @Override
    protected final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.animateEnemy();
    }
  }
}
