package com.daemonize.daemonengine;

import com.daemonize.daemonengine.consumer.Consumer;

public interface Daemon {

  void start();

  void stop();

  DaemonState getState();

  <K extends Daemon> K setName(String name);

  String getName();

  void setConsumer(Consumer consumer);

}
