package com.daemonize.daemonengine.closure;


import android.app.Activity;

import com.daemonize.daemonengine.exceptions.DaemonException;

public abstract class CheckedClosure<T> extends Closure<T> {

    public CheckedClosure(){}

    public CheckedClosure(Activity activity) {
        super(activity);
    }

    public T getResult() throws DaemonException {

        if (error != null) {
            throw new DaemonException(error);
        }

        return result;
    }

}
