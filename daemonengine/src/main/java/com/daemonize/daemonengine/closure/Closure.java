package com.daemonize.daemonengine.closure;


import android.app.Activity;
import android.util.Log;

import com.daemonize.daemonengine.exceptions.DaemonException;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.lang.ref.WeakReference;

public abstract class Closure<T> implements Runnable {

  protected volatile T result;
  protected volatile Exception error;

  private WeakReference<Activity> activity;
  private String activityName;

  protected Closure() {}

  //Use this construct to check if the activity that made this closure is alive
  protected Closure(Activity activity) {
    this.activity = new WeakReference<>(activity);
    activityName = activity.getLocalClassName();
  }

  @SuppressWarnings("unchecked")
  public <K extends Closure<T>> K setResult(T result) {
    this.result = result;
    return (K) this;
  }

  @SuppressWarnings("unchecked")
  public <K extends Closure> K setError(Exception error) {
    this.error = error;
    return (K) this;
  }

  @Override
  public void run() {

    if(activity != null && activity.get() != null
            && (activity.get().isDestroyed() || activity.get().isFinishing())) {
      Log.d(
              DaemonUtils.tag(),
              activityName + " that created this closure is now dead. Terminating closure..."
      );
      return;
    }
    onReturn();
  }

  public abstract void onReturn();

}
