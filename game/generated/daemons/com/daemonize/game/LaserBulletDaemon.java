package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.game.imagemovers.ImageMover;
import com.daemonize.game.images.Image;
import com.daemonize.game.view.ImageView;
import java.lang.Exception;
import java.lang.Float;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.Void;
import java.util.List;

public class LaserBulletDaemon implements Daemon {
  private LaserBullet prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  public LaserBulletDaemon(Consumer mainConsumer, Consumer sideConsumer, LaserBullet prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - MAIN");
    this.sideDaemonEngine = new SideQuestDaemonEngine(sideConsumer).setName(this.getClass().getSimpleName() + " - SIDE");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link LaserBullet#animateLaser} */
  public SideQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> setAnimateLaserSideQuest() {
    SideQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> sideQuest = new AnimateLaserSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25));
    return sideQuest;
  }

  public List<ImageView> getViews() {
    return prototype.getViews();
  }

  public void setViews(List<ImageView> views) {
    prototype.setViews(views);
  }

  public int getDamage() {
    return prototype.getDamage();
  }

  public void setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype mapped method {@link LaserBullet#iterateSprite} */
  public void iterateSprite(Closure<Image> closure) {
    mainDaemonEngine.pursueQuest(new IterateSpriteMainQuest(closure));
  }

  /**
   * Prototype mapped method {@link LaserBullet#rotate} */
  public void rotate(int angle) {
    mainDaemonEngine.pursueQuest(new RotateMainQuest(angle));
  }

  /**
   * Prototype mapped method {@link LaserBullet#desintegrateTarget} */
  public void desintegrateTarget(Pair<Float, Float> sourcecoord, EnemyDoubleDaemon target,
      long duration, Consumer drawconsumer, Closure<List<ImageView>> closure) {
    mainDaemonEngine.pursueQuest(new DesintegrateTargetMainQuest(sourcecoord, target, duration, drawconsumer, closure));
  }

  /**
   * Prototype mapped method {@link LaserBullet#animateLaser} */
  public void animateLaser(Closure<List<Pair<ImageView, ImageMover.PositionedImage>>> closure) {
    mainDaemonEngine.pursueQuest(new AnimateLaserMainQuest(closure));
  }

  public LaserBullet getPrototype() {
    return prototype;
  }

  public LaserBulletDaemon setPrototype(LaserBullet prototype) {
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
  public LaserBulletDaemon setName(String name) {
    mainDaemonEngine.setName(name + " - MAIN");
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

  private final class AnimateLaserSideQuest extends SideQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> {
    private AnimateLaserSideQuest() {
      this.description = "animateLaser";
    }

    @Override
    protected final List<Pair<ImageView, ImageMover.PositionedImage>> pursue() throws Exception {
      return prototype.animateLaser();
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

  private final class DesintegrateTargetMainQuest extends MainQuest<List<ImageView>> {
    private Pair<Float, Float> sourcecoord;

    private EnemyDoubleDaemon target;

    private long duration;

    private Consumer drawconsumer;

    private DesintegrateTargetMainQuest(Pair<Float, Float> sourcecoord, EnemyDoubleDaemon target,
        long duration, Consumer drawconsumer, Closure<List<ImageView>> closure) {
      super(closure);
      this.sourcecoord = sourcecoord;
      this.target = target;
      this.duration = duration;
      this.drawconsumer = drawconsumer;
      this.description = "desintegrateTarget";
    }

    @Override
    protected final List<ImageView> pursue() throws Exception {
      return prototype.desintegrateTarget(sourcecoord, target, duration, drawconsumer);
    }
  }

  private final class AnimateLaserMainQuest extends MainQuest<List<Pair<ImageView, ImageMover.PositionedImage>>> {
    private AnimateLaserMainQuest(Closure<List<Pair<ImageView, ImageMover.PositionedImage>>> closure) {
      super(closure);
      this.description = "animateLaser";
    }

    @Override
    protected final List<Pair<ImageView, ImageMover.PositionedImage>> pursue() throws Exception {
      return prototype.animateLaser();
    }
  }
}
