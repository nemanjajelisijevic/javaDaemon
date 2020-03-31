package com.daemonize.game;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.ReturnVoidMainQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.Button;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class ButtonDisablerDaemon implements EagerDaemon<ButtonDisablerDaemon> {
  private TowerDefenseGame.ButtonDisabler prototype;

  public EagerMainQuestDaemonEngine daemonEngine;

  public ButtonDisablerDaemon(Consumer consumer, TowerDefenseGame.ButtonDisabler prototype) {
    this.daemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  /**
   * Prototype method {@link com.daemonize.game.TowerDefenseGame.ButtonDisabler#disableButton} */
  public ButtonDisablerDaemon disableButton(Button button, Image disabled, Image enabled,
      Consumer consumer, Runnable retRun) {
    daemonEngine.pursueQuest(new DisableButtonMainQuest(button, disabled, enabled, retRun, null).setConsumer(consumer));
    return this;
  }

  public TowerDefenseGame.ButtonDisabler getPrototype() {
    return prototype;
  }

  public ButtonDisablerDaemon setPrototype(TowerDefenseGame.ButtonDisabler prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public ButtonDisablerDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public ButtonDisablerDaemon clear() {
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
  public ButtonDisablerDaemon queueStop() {
    daemonEngine.queueStop(this);
    return this;
  }

  @Override
  public ButtonDisablerDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public ButtonDisablerDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public ButtonDisablerDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  @Override
  public ButtonDisablerDaemon interrupt() {
    daemonEngine.interrupt();
    return this;
  }

  @Override
  public ButtonDisablerDaemon clearAndInterrupt() {
    daemonEngine.clearAndInterrupt();
    return this;
  }

  private final class DisableButtonMainQuest extends ReturnVoidMainQuest {
    private Button button;

    private Image disabled;

    private Image enabled;

    private DisableButtonMainQuest(Button button, Image disabled, Image enabled, Runnable retRun,
        ClosureExecutionWaiter closureAwaiter) {
      super(retRun, closureAwaiter);
      this.button = button;
      this.disabled = disabled;
      this.enabled = enabled;
      this.description = "disableButton";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.disableButton(button, disabled, enabled);
      return null;
    }
  }
}
