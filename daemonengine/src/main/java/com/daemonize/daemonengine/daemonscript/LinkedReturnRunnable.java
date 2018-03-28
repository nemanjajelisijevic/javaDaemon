package com.daemonize.daemonengine.daemonscript;

import android.app.Activity;

import com.daemonize.daemonengine.closure.AndroidReturnRunnable;
import com.daemonize.daemonengine.closure.Closure;

import java.lang.ref.WeakReference;

public abstract class LinkedReturnRunnable<T> extends AndroidReturnRunnable<T> {

    private WeakReference<DaemonScript> script;
    private boolean scriptBroken = false;

    public void breakScript() {
        scriptBroken = true;
    }

    public LinkedReturnRunnable(Closure<T> closure, Activity activity, DaemonScript script) {
        super(closure, activity);
        this.script = new WeakReference<>(script);
    }

    @Override
    public void run() {
        super.run();
        if (script.get() != null && !scriptBroken) {
            script.get().next();
        }
    }
}
