package com.daemonize.game.imagemovers;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.HybridDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.SleepSideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.game.Pair;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class ImageMoverDaemon implements Daemon<ImageMoverDaemon>, Movable {
  private ImageMover prototype;

  protected HybridDaemonEngine daemonEngine;

  public ImageMoverDaemon(Consumer consumer, ImageMover prototype) {
    this.daemonEngine = new HybridDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link ImageMover#animate} */
  public SleepSideQuest<ImageMover.PositionedImage> setAnimateSideQuest(Consumer consumer) {
    SleepSideQuest<ImageMover.PositionedImage> sideQuest = new AnimateSideQuest();
    daemonEngine.setSideQuest(sideQuest.setSleepInterval(25).setConsumer(consumer));
    return sideQuest;
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  public <K extends ImageMover> K setBorders(float x1, float x2, float y1, float y2) {
    return prototype.setBorders(x1, x2, y1, y2);
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageMover#setVelocity} */
  public ImageMoverDaemon setVelocity(float velocity) {
    daemonEngine.pursueQuest(new SetVelocityMainQuest(velocity).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageMover#setCoordinates} */
  public ImageMoverDaemon setCoordinates(float lastx, float lasty) {
    daemonEngine.pursueQuest(new SetCoordinatesMainQuest(lastx, lasty).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageMover#setDirection} */
  public ImageMoverDaemon setDirection(ImageMover.Direction direction) {
    daemonEngine.pursueQuest(new SetDirectionMainQuest(direction).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageMover#animate} */
  public ImageMoverDaemon animate(Closure<ImageMover.PositionedImage> closure) {
    daemonEngine.pursueQuest(new AnimateMainQuest(closure).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageMover#setDirectionAndMove} */
  public ImageMoverDaemon setDirectionAndMove(float x, float y, float velocityint,
      Closure<Boolean> closure) {
    daemonEngine.pursueQuest(new SetDirectionAndMoveMainQuest(x, y, velocityint, closure).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.imagemovers.ImageMover#setVelocity} */
  public ImageMoverDaemon setVelocity(ImageMover.Velocity velocity) {
    daemonEngine.pursueQuest(new SetVelocityIMainQuest(velocity).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public ImageMover getPrototype() {
    return prototype;
  }

  public ImageMoverDaemon setPrototype(ImageMover prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public ImageMoverDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public ImageMoverDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public ImageMoverDaemon queueStop() {
    daemonEngine.queueStop(this);
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
  public ImageMoverDaemon setName(String name) {
    daemonEngine.setName(name);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public ImageMoverDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public ImageMoverDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class AnimateSideQuest extends SleepSideQuest<ImageMover.PositionedImage> {
    private AnimateSideQuest() {
      super();
      this.description = "animate";
    }

    @Override
    public final ImageMover.PositionedImage pursue() throws Exception {
      return prototype.animate();
    }
  }

  private final class SetVelocityMainQuest extends VoidMainQuest {
    private float velocity;

    private SetVelocityMainQuest(float velocity) {
      super();
      setVoid();
      this.velocity = velocity;
      this.description = "setVelocity";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.setVelocity(velocity);
      return null;
    }
  }

  private final class SetCoordinatesMainQuest extends VoidMainQuest {
    private float lastx;

    private float lasty;

    private SetCoordinatesMainQuest(float lastx, float lasty) {
      super();
      setVoid();
      this.lastx = lastx;
      this.lasty = lasty;
      this.description = "setCoordinates";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.setCoordinates(lastx, lasty);
      return null;
    }
  }

  private final class SetDirectionMainQuest extends VoidMainQuest {
    private ImageMover.Direction direction;

    private SetDirectionMainQuest(ImageMover.Direction direction) {
      super();
      setVoid();
      this.direction = direction;
      this.description = "setDirection";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.setDirection(direction);
      return null;
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

  private final class SetVelocityIMainQuest extends VoidMainQuest {
    private ImageMover.Velocity velocity;

    private SetVelocityIMainQuest(ImageMover.Velocity velocity) {
      super();
      setVoid();
      this.velocity = velocity;
      this.description = "setVelocity";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.setVelocity(velocity);
      return null;
    }
  }
}
