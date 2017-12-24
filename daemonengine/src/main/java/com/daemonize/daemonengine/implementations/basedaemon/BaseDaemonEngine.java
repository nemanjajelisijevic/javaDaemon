package com.daemonize.daemonengine.implementations.basedaemon;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.quests.Quest;

public abstract class BaseDaemonEngine implements Daemon {

  public static String tag() {
    return Thread.currentThread().getName() + ", Thread ID: " + Thread.currentThread().getId();
  }

  protected String name = this.getClass().getSimpleName();

  @SuppressWarnings("unchecked")
  public <K extends Daemon> K setName(String name) {
    this.name = name;
    return (K) this;
  }

  protected Thread daemonThread;
  private DaemonState state = DaemonState.STOPPED;

  private Handler handler = new Handler(Looper.getMainLooper());

  protected void setState(DaemonState state) {
    this.state = state;
  }

  @Override
  public DaemonState getState() {
    return state;
  }

  //private Handler handler = new Handler(Looper.getMainLooper());

  public void update(Closure closure, Handler handler) {
    handler.post(closure);
  }

  protected abstract Quest getQuest();

  private void loop(){

    Log.i(tag(),"Daemon started!");
    Quest currentQuest;

    while (!state.equals(DaemonState.STOPPED)) {

      currentQuest = getQuest();

      if (currentQuest == null) {
        Log.w(tag(), "No quest set. Terminating daemon...");
        break;
      }

      if (!currentQuest.getIsVoid() && currentQuest.getClosure() == null) {
        Log.e(
                tag(),
            " No closure set for current quest: "
                + currentQuest.getDescription() + " (" + currentQuest.getState()
                + ") . Terminating daemon..."
        );
        break;
      }

//      Log.d(//TODO debug only
//              tag(),
//          "Pursuing quest: " + currentQuest.getDescription()
//              + " (" + currentQuest.getState() + ")"
//      );

      //currentQuest.setHandler(new Handler(Looper.myLooper()));//TODO double check this
      currentQuest.setHandler(handler);
      setState(currentQuest.getState());
      currentQuest.run();
    }

    setState(DaemonState.STOPPED);
    Log.i(tag(), "Daemon stopped!");
  }

  @Override
  public void start() {
    DaemonState initState = getState();
    if (!(initState.equals(DaemonState.STOPPED))) {
      Log.w(tag(), name + "already running. State: " + getState());
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
