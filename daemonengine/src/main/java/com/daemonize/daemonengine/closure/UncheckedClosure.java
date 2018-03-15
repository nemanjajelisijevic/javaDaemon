package com.daemonize.daemonengine.closure;


import android.app.Activity;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;

public abstract class UncheckedClosure<T> extends AndroidClosure<T> {

    public UncheckedClosure(){}

    public UncheckedClosure(Activity activity) {
        super(activity);
    }

    public T getResult() {
        if (error != null) {
            throw new DaemonRuntimeError(error);
        }
        return result;
    }

}
