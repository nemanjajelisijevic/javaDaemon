package com.daemonize.daemonengine;

import com.daemonize.daemonengine.consumer.Consumer;

import java.util.List;

public interface Daemon<D extends Daemon> {

  D start();

  void stop();

  D queueStop();

  List<DaemonState> getEnginesState();

  D setName(String name);

  String getName();

  D setConsumer(Consumer consumer);
}
