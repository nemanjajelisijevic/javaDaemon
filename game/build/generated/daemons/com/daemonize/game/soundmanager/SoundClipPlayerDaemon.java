package com.daemonize.game.soundmanager;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class SoundClipPlayerDaemon<T> implements EagerDaemon<SoundClipPlayerDaemon> {
  private SoundClipPlayer<T> prototype;

  protected EagerMainQuestDaemonEngine daemonEngine;

  public SoundClipPlayerDaemon(Consumer consumer, SoundClipPlayer<T> prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundClipPlayer#stopClip} */
  public SoundClipPlayerDaemon stopClip() {
    daemonEngine.pursueQuest(new StopClipMainQuest().setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundClipPlayer#playClip} */
  public SoundClipPlayerDaemon playClip(T soundclip) {
    daemonEngine.pursueQuest(new PlayClipMainQuest(soundclip).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public SoundClipPlayer<T> getPrototype() {
    return prototype;
  }

  public SoundClipPlayerDaemon setPrototype(SoundClipPlayer<T> prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public SoundClipPlayerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public SoundClipPlayerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public SoundClipPlayerDaemon queueStop() {
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
  public SoundClipPlayerDaemon setName(String name) {
    daemonEngine.setName(name);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public SoundClipPlayerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public SoundClipPlayerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public SoundClipPlayerDaemon interrupt() {
    daemonEngine.interrupt();
    return this;
  }

  @Override
  public SoundClipPlayerDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    return this;
  }

  private final class StopClipMainQuest extends VoidMainQuest {
    private StopClipMainQuest() {
      setVoid();
      this.description = "stopClip";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.stopClip();
      return null;
    }
  }

  private final class PlayClipMainQuest extends VoidMainQuest {
    private T soundclip;

    private PlayClipMainQuest(T soundclip) {
      setVoid();
      this.soundclip = soundclip;
      this.description = "playClip";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.playClip(soundclip);
      return null;
    }
  }
}
