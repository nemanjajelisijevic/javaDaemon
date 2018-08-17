package com.daemonize.daemonengine.implementations.basedaemon;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class BaseDaemonEngine implements Daemon {

  private volatile DaemonState state = DaemonState.STOPPED;
  private Consumer consumer;
  private String name = this.getClass().getSimpleName();

  protected Thread daemonThread;

  @SuppressWarnings("unchecked")
  public <K extends Daemon> K setName(String name) {
    this.name = name;
    return (K) this;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public BaseDaemonEngine setConsumer(Consumer consumer) {
    this.consumer = consumer;
    return this;
  }

  protected BaseDaemonEngine(Consumer consumer) {
    this.consumer = consumer;
  }

  protected void setState(DaemonState state) {
    this.state = state;
  }

  @Override
  public DaemonState getState() {
    return state;
  }

  protected abstract Quest getQuest();

  private void loop(){

    Quest currentQuest;

    while (!state.equals(DaemonState.GONE_DAEMON)) {

      currentQuest = getQuest();

      if (currentQuest == null) {
        break;
      }

      if (!currentQuest.getIsVoid() && currentQuest.getReturnRunnable() == null) {
        break;
      }

      setState(currentQuest.getState());
      currentQuest.setConsumer(consumer).run();
    }

    System.out.println(DaemonUtils.tag() + "Daemon stopped!");

    setState(DaemonState.STOPPED);
  }

  @Override
  public void start() {
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
  }

  @Override
  public void stop() {
    state = DaemonState.GONE_DAEMON;
    if (daemonThread != null
        && !Thread.currentThread().equals(daemonThread)//TODO check if possible to stopDaemon from daemon thread
        && daemonThread.isAlive()) {
      daemonThread.interrupt();
    }
  }

  @Override
  public void queueStop() {
    throw new IllegalStateException("This method can only be called from MainQuestDaemonEngine");
  }

}
