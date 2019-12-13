package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.ImageMover;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class TowerSpriteUpgraderDaemon implements EagerDaemon<TowerSpriteUpgraderDaemon> {
  private Game.TowerSpriteUpgrader prototype;

  public EagerMainQuestDaemonEngine daemonEngine;

  public TowerSpriteUpgraderDaemon(Consumer consumer, Game.TowerSpriteUpgrader prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.Game.TowerSpriteUpgrader#updateTowerSprite} */
  public TowerSpriteUpgraderDaemon updateTowerSprite(TowerDaemon tower, Image[] sprite,
      Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure) {
    daemonEngine.pursueQuest(new UpdateTowerSpriteMainQuest(tower, sprite, closure, null).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public Game.TowerSpriteUpgrader getPrototype() {
    return prototype;
  }

  public TowerSpriteUpgraderDaemon setPrototype(Game.TowerSpriteUpgrader prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public TowerSpriteUpgraderDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public TowerSpriteUpgraderDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(daemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(daemonEngine.queueSize());
    return ret;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public TowerSpriteUpgraderDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public TowerSpriteUpgraderDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public TowerSpriteUpgraderDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public TowerSpriteUpgraderDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public TowerSpriteUpgraderDaemon interrupt() {
    daemonEngine.interrupt();
    return this;
  }

  @Override
  public TowerSpriteUpgraderDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    return this;
  }

  private final class UpdateTowerSpriteMainQuest extends MainQuest<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> {
    private TowerDaemon tower;

    private Image[] sprite;

    private UpdateTowerSpriteMainQuest(TowerDaemon tower, Image[] sprite,
        Closure<GenericNode<Pair<ImageMover.PositionedImage, ImageView>>> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
      this.tower = tower;
      this.sprite = sprite;
      this.description = "updateTowerSprite";
    }

    @Override
    public final GenericNode<Pair<ImageMover.PositionedImage, ImageView>> pursue() throws
        Exception {
      return prototype.updateTowerSprite(tower, sprite);
    }
  }
}
