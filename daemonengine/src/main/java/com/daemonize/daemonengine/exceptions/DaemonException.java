package com.daemonize.daemonengine.exceptions;

public class DaemonException extends Exception {

  public DaemonException(String message) {
    super(message);
  }

  public DaemonException(Throwable cause) {
    super(cause);
  }

  public DaemonException(String message, Throwable cause) {
    super(message, cause);
  }
}
