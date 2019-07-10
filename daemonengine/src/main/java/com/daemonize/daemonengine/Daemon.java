package com.daemonize.daemonengine;

import com.daemonize.daemonengine.consumer.Consumer;

import java.util.List;

public interface Daemon<D extends Daemon> extends DaemonService<D> {

  D queueStop();

  List<DaemonState> getEnginesState();

  D setName(String name);

  String getName();

  D setConsumer(Consumer consumer);

  Consumer getConsumer();

  D clear();

  D setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);
}
