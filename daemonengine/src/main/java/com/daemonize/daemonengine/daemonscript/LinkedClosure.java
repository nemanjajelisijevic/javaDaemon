package com.daemonize.daemonengine.daemonscript;

import android.app.Activity;

import com.daemonize.daemonengine.closure.UncheckedClosure;

import java.lang.ref.WeakReference;

public abstract class LinkedClosure<T> extends UncheckedClosure<T> {

    private WeakReference<DaemonScript> script;
    private boolean scriptBroken = false;

    public void breakScript() {
        scriptBroken = true;
    }

    public LinkedClosure(Activity activity, DaemonScript script) {
        super(activity);
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
