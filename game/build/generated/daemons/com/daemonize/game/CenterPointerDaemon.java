package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.MainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.Movable;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

public class CenterPointerDaemon implements Daemon<CenterPointerDaemon>, Movable {
  private MapEditor.CenterPointer prototype;

  public MainQuestDaemonEngine daemonEngine;

  public CenterPointerDaemon(Consumer consumer, MapEditor.CenterPointer prototype) {
    this.daemonEngine = new MainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public boolean redirect(float x, float y) {
    return prototype.redirect(x, y);
  }

  public Pair<Float, Float> getTargetCoordinates() {
    return prototype.getTargetCoordinates();
  }

  public CenterPointerDaemon popSprite() {
    prototype.popSprite();
    return this;
  }

  public CenterPointerDaemon setVelocity(ImageMover.Velocity velocity) {
    prototype.setVelocity(velocity);
    return this;
  }

  public double absDistance(float x1, float y1, float x2, float y2) {
    return prototype.absDistance(x1, y1, x2, y2);
  }

  public int getSize() {
    return prototype.getSize();
  }

  public CenterPointerDaemon pushSprite(Image[] sprite) throws InterruptedException {
    prototype.pushSprite(sprite);
    return this;
  }

  public Image iterateSprite() {
    return prototype.iterateSprite();
  }

  @Override
  public ImageMover.Velocity getVelocity() {
    return prototype.getVelocity();
  }

  @Override
  public Movable.AnimationWaiter getAnimationWaiter() {
    return prototype.getAnimationWaiter();
  }

  @Override
  public Pair<Float, Float> getLastCoordinates() {
    return prototype.getLastCoordinates();
  }

  public Image[] getSprite() {
    return prototype.getSprite();
  }

  public CenterPointerDaemon setSprite(Image[] sprite) {
    prototype.setSprite(sprite);
    return this;
  }

  public double absDistance(Pair<Float, Float> source, Pair<Float, Float> dest) {
    return prototype.absDistance(source, dest);
  }

  public boolean setDirectionToPoint(float x, float y) {
    return prototype.setDirectionToPoint(x, y);
  }

  public CenterPointerDaemon clearVelocity() {
    prototype.clearVelocity();
    return this;
  }

  public CenterPointerDaemon setCoordinates(float lastx, float lasty) {
    prototype.setCoordinates(lastx, lasty);
    return this;
  }

  public CenterPointerDaemon setDirection(ImageMover.Direction direction) {
    prototype.setDirection(direction);
    return this;
  }

  public ImageMover.PositionedImage animate() throws InterruptedException {
    return prototype.animate();
  }

  public boolean setDirectionAndMove(float x, float y, float velocityint) {
    return prototype.setDirectionAndMove(x, y, velocityint);
  }

  public float getdXY() {
    return prototype.getdXY();
  }

  @Override
  public void setVelocity(float velocity) {
    prototype.setVelocity(velocity);
  }

  /**
   * Prototype method {@link com.daemonize.imagemovers.CoordinatedImageTranslationMover#goTo} */
  public CenterPointerDaemon goTo(float x, float y, float velocityint, Closure<Boolean> closure) {
    daemonEngine.pursueQuest(new GoToMainQuest(x, y, velocityint, closure, null).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public MapEditor.CenterPointer getPrototype() {
    return prototype;
  }

  public CenterPointerDaemon setPrototype(MapEditor.CenterPointer prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public CenterPointerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public CenterPointerDaemon clear() {
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
  public CenterPointerDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public CenterPointerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public CenterPointerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public CenterPointerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class GoToMainQuest extends MainQuest<Boolean> {
    private float x;

    private float y;

    private float velocityint;

    private GoToMainQuest(float x, float y, float velocityint, Closure<Boolean> closure,
        ClosureExecutionWaiter closureAwaiter) {
      super(closure, closureAwaiter);
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
}
