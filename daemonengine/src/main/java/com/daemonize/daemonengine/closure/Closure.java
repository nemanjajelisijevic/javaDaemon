package com.daemonize.daemonengine.closure;


import android.app.Activity;
import android.util.Log;

import com.daemonize.daemonengine.daemonscript.DaemonScript;
import com.daemonize.daemonengine.exceptions.DaemonException;
import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;

import java.lang.ref.WeakReference;

public abstract class Closure<T> implements Runnable {

  private T result;
  private WeakReference<Activity> activity;
  private String activityName;

  public Closure() {}

  //Use this construct to check if the activity that made this closure is alive
  public Closure(Activity activity) {
    this.activity = new WeakReference<>(activity);
    activityName = activity.getLocalClassName();
  }

  @SuppressWarnings("unchecked")
  public <K extends Closure<T>> K setResult(T result) {
    this.result = result;
    return (K) this;
  }

  public T getResult() throws DaemonException {

    if (error != null) {
      throw new DaemonException(error);
    }

    return result;
  }

  private Exception error;

  @SuppressWarnings("unchecked")
  public <K extends Closure> K setError(Exception error) {
    this.error = error;
    return (K) this;
  }

  @Override
  public void run() {

    if(activity != null && activity.get() != null && (activity.get().isDestroyed() || activity.get().isFinishing())) {
      Log.d(
              Thread.currentThread().getName(),
              activityName + " that created this closure is now dead. Terminating closure..."
      );
      return;
    }
    doTheGuiStuff();
  }

  public abstract void doTheGuiStuff();

}
