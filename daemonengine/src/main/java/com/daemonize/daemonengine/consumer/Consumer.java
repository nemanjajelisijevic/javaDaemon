package com.daemonize.daemonengine.consumer;


import com.daemonize.daemonengine.DaemonService;

public interface Consumer<C extends Consumer> extends DaemonService<C> {
    boolean consume(Runnable runnable);
}
