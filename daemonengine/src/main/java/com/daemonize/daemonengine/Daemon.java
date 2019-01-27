package com.daemonize.daemonengine;

import com.daemonize.daemonengine.consumer.Consumer;

public interface Daemon<D extends Daemon> {

  D start();

  void stop();

  D queueStop();

  DaemonState getState();

  D setName(String name);

  String getName();

  D setConsumer(Consumer consumer);
}
