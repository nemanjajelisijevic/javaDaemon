package com.daemonize.game.soundmanager;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import java.io.File;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class SoundManagerDaemon implements EagerDaemon<SoundManagerDaemon> {
  protected EagerMainQuestDaemonEngine playSoundChannel2DaemonEngine;

  protected EagerMainQuestDaemonEngine playSoundChannel4DaemonEngine;

  protected EagerMainQuestDaemonEngine playSoundChannel3DaemonEngine;

  private SoundManager prototype;

  protected EagerMainQuestDaemonEngine daemonEngine;

  public SoundManagerDaemon(Consumer consumer, SoundManager prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.playSoundChannel2DaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - playSoundChannel2DaemonEngine");
    this.playSoundChannel4DaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - playSoundChannel4DaemonEngine");
    this.playSoundChannel3DaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName() + " - playSoundChannel3DaemonEngine");
    this.prototype = prototype;
  }

  public File loadFile(String name) throws SoundException {
    return prototype.loadFile(name);
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundManager#playSoundChannel3} */
  public SoundManagerDaemon playSoundChannel3(File soundfile) {
    playSoundChannel3DaemonEngine.pursueQuest(new PlaySoundChannel3MainQuest(soundfile).setConsumer(playSoundChannel3DaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundManager#playSoundChannel1} */
  public SoundManagerDaemon playSoundChannel1(File soundfile) {
    daemonEngine.pursueQuest(new PlaySoundChannel1MainQuest(soundfile).setConsumer(daemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundManager#playSoundChannel2} */
  public SoundManagerDaemon playSoundChannel2(File soundfile) {
    playSoundChannel2DaemonEngine.pursueQuest(new PlaySoundChannel2MainQuest(soundfile).setConsumer(playSoundChannel2DaemonEngine.getConsumer()));
    return this;
  }

  /**
   * Prototype method {@link com.daemonize.game.soundmanager.SoundManager#playSoundChannel4} */
  public SoundManagerDaemon playSoundChannel4(File soundfile) {
    playSoundChannel4DaemonEngine.pursueQuest(new PlaySoundChannel4MainQuest(soundfile).setConsumer(playSoundChannel4DaemonEngine.getConsumer()));
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
    playSoundChannel2DaemonEngine.start();
    playSoundChannel3DaemonEngine.start();
    playSoundChannel4DaemonEngine.start();
    return this;
  }

  @Override
  public SoundManagerDaemon clear() {
    daemonEngine.clear();
    playSoundChannel2DaemonEngine.clear();
    playSoundChannel3DaemonEngine.clear();
    playSoundChannel4DaemonEngine.clear();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(daemonEngine.getState());
    ret.add(playSoundChannel2DaemonEngine.getState());
    ret.add(playSoundChannel3DaemonEngine.getState());
    ret.add(playSoundChannel4DaemonEngine.getState());
    return ret;
  }

  public List<Integer> getEnginesQueueSizes() {
    List<Integer> ret = new ArrayList<Integer>();
    ret.add(daemonEngine.queueSize());
    ret.add(playSoundChannel2DaemonEngine.queueSize());
    ret.add(playSoundChannel3DaemonEngine.queueSize());
    ret.add(playSoundChannel4DaemonEngine.queueSize());
    return ret;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
    playSoundChannel2DaemonEngine.stop();
    playSoundChannel3DaemonEngine.stop();
    playSoundChannel4DaemonEngine.stop();
  }

  @Override
  public SoundManagerDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public SoundManagerDaemon setName(String name) {
    daemonEngine.setName(name);
    playSoundChannel2DaemonEngine.setName(name +" - playSoundChannel2DaemonEngine");
    playSoundChannel3DaemonEngine.setName(name +" - playSoundChannel3DaemonEngine");
    playSoundChannel4DaemonEngine.setName(name +" - playSoundChannel4DaemonEngine");
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public SoundManagerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    playSoundChannel2DaemonEngine.setConsumer(consumer);
    playSoundChannel3DaemonEngine.setConsumer(consumer);
    playSoundChannel4DaemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public SoundManagerDaemon interrupt() {
    daemonEngine.interrupt();
    playSoundChannel2DaemonEngine.interrupt();
    playSoundChannel3DaemonEngine.interrupt();
    playSoundChannel4DaemonEngine.interrupt();
    return this;
  }

  @Override
  public SoundManagerDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    playSoundChannel2DaemonEngine.clearAndInterrupt();
    playSoundChannel3DaemonEngine.clearAndInterrupt();
    playSoundChannel4DaemonEngine.clearAndInterrupt();
    return this;
  }

  private final class PlaySoundChannel3MainQuest extends VoidMainQuest {
    private File soundfile;

    private PlaySoundChannel3MainQuest(File soundfile) {
      setVoid();
      this.soundfile = soundfile;
      this.description = "playSoundChannel3";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.playSoundChannel3(soundfile);
      return null;
    }
  }

  private final class PlaySoundChannel1MainQuest extends VoidMainQuest {
    private File soundfile;

    private PlaySoundChannel1MainQuest(File soundfile) {
      setVoid();
      this.soundfile = soundfile;
      this.description = "playSoundChannel1";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.playSoundChannel1(soundfile);
      return null;
    }
  }

  private final class PlaySoundChannel2MainQuest extends VoidMainQuest {
    private File soundfile;

    private PlaySoundChannel2MainQuest(File soundfile) {
      setVoid();
      this.soundfile = soundfile;
      this.description = "playSoundChannel2";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.playSoundChannel2(soundfile);
      return null;
    }
  }

  private final class PlaySoundChannel4MainQuest extends VoidMainQuest {
    private File soundfile;

    private PlaySoundChannel4MainQuest(File soundfile) {
      setVoid();
      this.soundfile = soundfile;
      this.description = "playSoundChannel4";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.playSoundChannel4(soundfile);
      return null;
    }
  }
}
