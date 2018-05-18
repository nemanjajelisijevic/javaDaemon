package com.daemonize.daemonengine;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;

public interface Daemon {

  void start();

  void stop();

  void stop (Closure<Void> closure);

  DaemonState getState();

  <K extends Daemon> K setName(String name);

  String getName();

  <K extends Daemon> K setConsumer(Consumer consumer);

//  <K extends Daemon> K sleep();
//
//  <K extends Daemon> K sleep(long millis);



}
