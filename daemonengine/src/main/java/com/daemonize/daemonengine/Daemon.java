package com.daemonize.daemonengine;

public interface Daemon {

  void start();

  void stop();

  DaemonState getState();

  <K extends Daemon> K setName(String name);

}
