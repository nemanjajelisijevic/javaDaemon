package com.daemonize.daemonengine.closure;


import com.daemonize.daemonengine.exceptions.DaemonException;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;

public class Return<T> {

    protected volatile T result;
    protected volatile Exception error;

    @SuppressWarnings("unchecked")
    public <K extends Return<T>> K setResult(T result) {
        this.result = result;
        return (K) this;
    }

    @SuppressWarnings("unchecked")
    public <K extends Return<T>> K setError(Exception error) {
        this.error = error;
        return (K) this;
    }

    public T checkAndGet() throws DaemonException {
        if (error != null) {
            throw new DaemonException(error);
        }
        return result;
    }

    public T get() {
        if(error != null) {
            throw new DaemonRuntimeError(error);
        }
        return result;
    }

}
