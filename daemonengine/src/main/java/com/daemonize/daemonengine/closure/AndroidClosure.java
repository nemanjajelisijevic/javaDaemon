package com.daemonize.daemonengine.closure;


import android.app.Activity;

import com.daemonize.daemonengine.utils.DaemonUtils;

import java.lang.ref.WeakReference;

public abstract class AndroidClosure<T> extends Closure<T> {

    private WeakReference<Activity> activity;
    private String activityName;

    protected AndroidClosure(){}

    //Use this construct to check if the activity that made this closure is alive
    protected AndroidClosure(Activity activity) {
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
                    + " that created this closure is now dead. Terminating closure..."
            );
            return;
        }
        super.run();
    }
}
