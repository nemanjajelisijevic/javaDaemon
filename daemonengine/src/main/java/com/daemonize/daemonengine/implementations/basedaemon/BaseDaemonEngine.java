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

    System.out.println(DaemonUtils.tag() + "Daemon started!");
    Quest currentQuest;

    while (!state.equals(DaemonState.STOPPED)) {

      currentQuest = getQuest();

      if (currentQuest == null) {
        System.err.println(DaemonUtils.tag() + "No quest set. Terminating daemon...");
        break;
      }

      if (!currentQuest.getIsVoid() && currentQuest.getReturnRunnable() == null) {
        System.err.println(
                DaemonUtils.tag()
                + " No returnRunnable set for current quest: "
                + currentQuest.getDescription() + " (" + currentQuest.getState()
                + ") . Terminating daemon..."
        );
        break;
      }

      setState(currentQuest.getState());
      currentQuest.setConsumer(consumer).run();
    }

    setState(DaemonState.STOPPED);
    System.out.println(DaemonUtils.tag() + "Daemon stopped!");
  }

  @Override
  public void start() {
    DaemonState initState = getState();
    if (!(initState.equals(DaemonState.STOPPED))) {
      System.out.println(DaemonUtils.tag() +  name + "already running. State: " + getState());
    } else {
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
  public void stop() {//TODO check final
    state = DaemonState.STOPPED;
    if (daemonThread != null
        && !Thread.currentThread().equals(daemonThread)//TODO check if possible to stop from daemon thread
        && daemonThread.isAlive()) {
      daemonThread.interrupt();
    }
  }

}
