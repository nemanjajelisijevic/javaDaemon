package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.SideQuest;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.Thread;
import java.lang.Void;
import java.util.ArrayList;
import java.util.List;

public class CommandParserDaemon implements Daemon<CommandParserDaemon> {
  private CommandParser prototype;

  protected SideQuestDaemonEngine daemonEngine;

  public CommandParserDaemon(CommandParser prototype) {
    this.daemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName());
    this.prototype = prototype;
  }

  public SideQuest getCurrentSideQuest() {
    return this.daemonEngine.getSideQuest();
  }

  /**
   * Prototype method {@link CommandParser#parse} */
  public SideQuest<Void> setParseSideQuest(Consumer consumer) {
    SideQuest<Void> sideQuest = new ParseSideQuest(null);
    daemonEngine.setSideQuest(sideQuest.setConsumer(consumer));
    return sideQuest;
  }

  public CommandParser getPrototype() {
    return prototype;
  }

  public CommandParserDaemon setPrototype(CommandParser prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public CommandParserDaemon start() {
    daemonEngine.start();
    return this;
  }

  @Override
  public void stop() {
    daemonEngine.stop();
  }

  @Override
  public CommandParserDaemon clear() {
    return this;
  }

  @Override
  public CommandParserDaemon queueStop() {
    stop();
    return this;
  }

  public List<DaemonState> getEnginesState() {
    List<DaemonState> ret = new ArrayList<DaemonState>();
    ret.add(daemonEngine.getState());
    return ret;
  }

  @Override
  public CommandParserDaemon setName(String engineName) {
    daemonEngine.setName(engineName);
    return this;
  }

  @Override
  public String getName() {
    return daemonEngine.getName();
  }

  @Override
  public CommandParserDaemon setConsumer(Consumer consumer) {
    daemonEngine.setConsumer(consumer);
    return this;
  }

  @Override
  public Consumer getConsumer() {
    return daemonEngine.getConsumer();
  }

  @Override
  public CommandParserDaemon setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
    daemonEngine.setUncaughtExceptionHandler(handler);
    return this;
  }

  private final class ParseSideQuest extends SideQuest<Void> {
    private ParseSideQuest(ClosureExecutionWaiter closureAwaiter) {
      super(closureAwaiter);
      this.description = "parse";
    }

    @Override
    public final Void pursue() throws Exception {
      prototype.parse();
      return null;
    }
  }
}
