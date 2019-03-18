package com.daemonize.daemonengine.implementations.basedaemon;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDaemonEngine<D extends Daemon> implements Daemon {

  protected volatile DaemonState state = DaemonState.STOPPED;
  private Consumer consumer;
  private String name = this.getClass().getSimpleName();

  protected Thread daemonThread;

  public D setName(String name) {
    this.name = name;
    return (D) this;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public D setConsumer(Consumer consumer) {
    this.consumer = consumer;
    return (D) this;
  }

  public Consumer getConsumer() {
    return consumer;
  }

  protected BaseDaemonEngine(){}

  protected BaseDaemonEngine(Consumer consumer) {
    this.consumer = consumer;
  }

  protected void setState(DaemonState state) {
    this.state = state;
  }

  public DaemonState getState() {
    return state;
  }

  protected abstract Quest getQuest();

  private void loop(){

    Quest currentQuest;

    while (!state.equals(DaemonState.GONE_DAEMON)) {

      currentQuest = getQuest();

      if (currentQuest == null) {
          if (state.equals(DaemonState.IDLE))//TODO check dis
              continue;
        break;
      }

      if (!currentQuest.getIsVoid() && currentQuest.getReturnRunnable() == null) {
        break;
      }

      setState(currentQuest.getState());
      currentQuest.run();
    }

    System.out.println(DaemonUtils.tag() + "Daemon stopped!");

    setState(DaemonState.STOPPED);
  }

  @Override
  public D start() {
    DaemonState initState = getState();
    if (initState.equals(DaemonState.STOPPED)) {
      daemonThread = new Thread(new Runnable() {
        @Override
        public void run() {
          loop();
        }
      });
      daemonThread.setName(name);
      setState(DaemonState.INITIALIZING);
      daemonThread.start();
    }
    return (D) this;
  }

  @Override
  public void stop() {
    if (state != DaemonState.STOPPED) {
      state = DaemonState.GONE_DAEMON;
      if (daemonThread != null
              && !Thread.currentThread().equals(daemonThread)//TODO check if possible to stopDaemon from daemon thread
              && daemonThread.isAlive()) {
        daemonThread.interrupt();
      }
    }
  }

  @Override
  public D queueStop() {
    throw new IllegalStateException("This method can only be called from MainQuestDaemonEngine");
  }

    @Override
    public List<DaemonState> getEnginesState() {
        List<DaemonState> ret = new ArrayList<>(1);
        ret.add(getState());
        return ret;
    }
}
