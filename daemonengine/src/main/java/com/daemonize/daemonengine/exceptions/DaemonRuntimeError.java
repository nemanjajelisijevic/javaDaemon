package com.daemonize.daemonengine.exceptions;


public class DaemonRuntimeError extends RuntimeException {

    public DaemonRuntimeError(String message) {
        super(message);
    }

    public DaemonRuntimeError(Throwable cause) {
        super(cause);
    }

    public DaemonRuntimeError(String message, Throwable cause) {
        super(message, cause);
    }

}
