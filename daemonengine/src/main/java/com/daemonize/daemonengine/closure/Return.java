package com.daemonize.daemonengine.closure;


import com.daemonize.daemonengine.exceptions.DaemonException;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;

public class Return<T> {

    private volatile T result;
    private volatile Exception error;
    private volatile String daemonDescription;

    public Return(){}

    public Return(T result) {
        this.result = result;
    }

    public void setResult(T result) {
        this.result = result;
        this.error = null;
    }

    void setError(Exception error, String daemonDescription) {
        this.error = error;
        this.daemonDescription = daemonDescription;
    }

    public T checkAndGet() throws DaemonException {
        if (error != null) {
            DaemonException exc = new DaemonException(daemonDescription, error);
            exc.setStackTrace(new StackTraceElement[]{});
            throw exc;
        }
        return result;
    }

    public T runtimeCheckAndGet() {
        if(error != null) {
            DaemonRuntimeError err = new DaemonRuntimeError(daemonDescription, error);
            err.setStackTrace(new StackTraceElement[]{});
            throw err;
        }
        return result;
    }

    public T get() {
        return result;
    }

}
