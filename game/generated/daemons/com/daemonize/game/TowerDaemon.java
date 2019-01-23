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

public class TowerDaemon implements Daemon {
  private Tower prototype;

  protected EagerMainQuestDaemonEngine mainDaemonEngine;

  protected SideQuestDaemonEngine sideDaemonEngine;

  protected EagerMainQuestDaemonEngine scanDaemonEngine;

  public TowerDaemon(Consumer mainConsumer, Consumer sideConsumer, Tower prototype) {
    this.mainDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - MAIN");
    this.sideDaemonEngine = new SideQuestDaemonEngine(sideConsumer).setName(this.getClass().getSimpleName() + " - SIDE");
    this.scanDaemonEngine = new EagerMainQuestDaemonEngine(mainConsumer).setName(this.getClass().getSimpleName() + " - scanDaemonEngine");
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link Tower#animate} */
  public SideQuest<ImageMover.PositionedImage> setAnimateSideQuest() {
    SideQuest<ImageMover.PositionedImage> sideQuest = new AnimateSideQuest();
    sideDaemonEngine.setSideQuest(sideQuest.setSleepInterval(25));
    return sideQuest;
  }

  public boolean addTarget(EnemyDoubleDaemon target) {
    return prototype.addTarget(target);
  }

  public float getRange() {
    return prototype.getRange();
  }

  public void levelUp() {
    prototype.levelUp();
  }

  public Tower.TowerLevel getTowerLevel() {
    return prototype.getTowerLevel();
  }

  public void setTowerLevel(Tower.TowerLevel towerlevel) {
    prototype.setTowerLevel(towerlevel);
  }

  public ImageView getView() {
    return prototype.getView();
  }

  public void setView(ImageView view) {
    prototype.setView(view);
  }

  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public void setRotationSprite(Image[] rotationsprite) {
    prototype.setRotationSprite(rotationsprite);
  }

  public void pause() {
    prototype.pause();
  }

  public void cont() {
    prototype.cont();
  }

  public void setCurrentAngle(int currentangle) {
    prototype.setCurrentAngle(currentangle);
  }

  public void pauseScan() {
    prototype.pauseScan();
  }

  public void contScan() {
    prototype.contScan();
  }

  public Tower.TowerType getTowertype() {
    return prototype.getTowertype();
  }

  public SideQuest getCurrentSideQuest() {
    return this.sideDaemonEngine.getSideQuest();
  }

  /**
   * Prototype mapped method {@link Tower#reload} */
  public void reload(long millis, Runnable retRun) {
    mainDaemonEngine.pursueQuest(new ReloadMainQuest(millis, retRun));
  }

  /**
   * Prototype mapped method {@link Tower#rotateTowards} */
  public void rotateTowards(float x, float y) {
    mainDaemonEngine.pursueQuest(new RotateTowardsMainQuest(x, y));
  }

  /**
   * Prototype mapped method {@link Tower#scan} */
  public void scan(Closure<Pair<Tower.TowerType, EnemyDoubleDaemon>> closure) {
    scanDaemonEngine.pursueQuest(new ScanMainQuest(closure));
  }

  /**
   * Prototype mapped method {@link Tower#updateSprite} */
  public void updateSprite(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new UpdateSpriteMainQuest(closure));
  }

  /**
   * Prototype mapped method {@link Tower#animate} */
  public void animate(Closure<ImageMover.PositionedImage> closure) {
    mainDaemonEngine.pursueQuest(new AnimateMainQuest(closure));
  }

  public Tower getPrototype() {
    return prototype;
  }

  public TowerDaemon setPrototype(Tower prototype) {
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
    scanDaemonEngine.stop();
  }

  @Override
  public void queueStop() {
    mainDaemonEngine.queueStop();
    sideDaemonEngine.stop();
    scanDaemonEngine.queueStop();
  }

  @Override
  public DaemonState getState() {
    return sideDaemonEngine.getState();
  }

  @Override
  public TowerDaemon setName(String name) {
    mainDaemonEngine.setName(name + " - MAIN");
    sideDaemonEngine.setName(name + " - SIDE");
    scanDaemonEngine.setName(name +" - scanDaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return mainDaemonEngine.getName();
  }

  public TowerDaemon setMainQuestConsumer(Consumer consumer) {
    mainDaemonEngine.setConsumer(consumer);
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

  private final class AnimateSideQuest extends SideQuest<ImageMover.PositionedImage> {
    private AnimateSideQuest() {
      this.description = "animate";
    }

    @Override
    protected final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.animate();
    }
  }

  private final class ReloadMainQuest extends VoidMainQuest<Void> {
    private long millis;

    private ReloadMainQuest(long millis, Runnable retRun) {
      super(retRun);
      this.millis = millis;
      this.description = "reload";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.reload(millis);
      return null;
    }
  }

  private final class RotateTowardsMainQuest extends MainQuest<Void> {
    private float x;

    private float y;

    private RotateTowardsMainQuest(float x, float y) {
      setVoid();
      this.x = x;
      this.y = y;
      this.description = "rotateTowards";
    }

    @Override
    protected final Void pursue() throws Exception {
      prototype.rotateTowards(x, y);
      return null;
    }
  }

  private final class ScanMainQuest extends MainQuest<Pair<Tower.TowerType, EnemyDoubleDaemon>> {
    private ScanMainQuest(Closure<Pair<Tower.TowerType, EnemyDoubleDaemon>> closure) {
      super(closure);
      this.description = "scan";
    }

    @Override
    protected final Pair<Tower.TowerType, EnemyDoubleDaemon> pursue() throws Exception {
      return prototype.scan();
    }
  }

  private final class UpdateSpriteMainQuest extends MainQuest<ImageMover.PositionedImage> {
    private UpdateSpriteMainQuest(Closure<ImageMover.PositionedImage> closure) {
      super(closure);
      this.description = "updateSprite";
    }

    @Override
    protected final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.updateSprite();
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
}
