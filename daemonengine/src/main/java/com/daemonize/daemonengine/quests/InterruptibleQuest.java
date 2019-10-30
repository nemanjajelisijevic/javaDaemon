package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.closure.BareClosure;
import com.daemonize.daemonengine.consumer.Consumer;

public interface InterruptibleQuest<Q extends InterruptibleQuest, D extends Daemon<D>> {
    Q setDaemon(D daemon);
    Q onInterrupt(Consumer consumer, BareClosure<D> interruptClosure);
}
