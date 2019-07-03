package com.daemonize.game.soundmanager;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.io.File;
import java.io.IOException;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Void;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SoundManagerDaemon implements EagerDaemon<SoundManagerDaemon> {
  private SoundManager prototype;

  protected EagerMainQuestDaemonEngine daemonEngine;

  public SoundManagerDaemon(Consumer consumer, SoundManager prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public boolean isPlaying() {
    return prototype.isPlaying();
  }

  public File loadFile(String name) throws URISyntaxException, IOException {
    return prototype.loadFile(name);
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundManager#playSound} */
  public SoundManagerDaemon playSound(File soundfile) {
    daemonEngine.pursueQuest(new PlaySoundMainQuest(soundfile).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  public SoundManager getPrototype() {
    return prototype;
  }

  public SoundManagerDaemon setPrototype(SoundManager prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public SoundManagerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public SoundManagerDaemon clear() {
    daemonEngine.clear();
    return this;
  }

  @Override
  public SoundManagerDaemon queueStop() {
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
  public SoundManagerDaemon setName(String name) {
    daemonEngine.setName(name);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public SoundManagerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public SoundManagerDaemon interrupt() {
    daemonEngine.interrupt();
    return this;
  }

  @Override
  public SoundManagerDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    return this;
  }

  private final class PlaySoundMainQuest extends VoidMainQuest {
    private File soundfile;

    private PlaySoundMainQuest(File soundfile) {
      setVoid();
      this.soundfile = soundfile;
      this.description = "playSound";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.playSound(soundfile);
      return null;
    }
  }
}
