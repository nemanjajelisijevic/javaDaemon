package com.daemonize.daemonengine.closure;


import android.app.Activity;

import com.daemonize.daemonengine.utils.DaemonUtils;

import java.lang.ref.WeakReference;

public class AndroidReturnRunnable<T> extends ReturnRunnable<T> {

    private WeakReference<Activity> activity;
    private String activityName;

    public AndroidReturnRunnable(Closure<T> closure){
        super(closure);
    }

    //Use this construct to check if the activity that made this returnRunnable is alive
    protected AndroidReturnRunnable(Closure<T> closure, Activity activity) {
        super(closure);
        this.activity = new WeakReference<>(activity);
        activityName = activity.getLocalClassName();
    }

    @Override
    public void run() {
        if(activity != null && activity.get() != null
                && (activity.get().isDestroyed() || activity.get().isFinishing())) {
            System.out.println(
                    DaemonUtils.tag()
                    + activityName
                    + " that created this returnRunnable is now dead. Terminating returnRunnable..."
            );
            return;
        }
        super.run();
    }
}
