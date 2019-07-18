package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.consumer.Consumer;

public interface InterruptibleQuest<Q extends InterruptibleQuest> {
    Q onInterrupt(Consumer consumer, Runnable interruptClosure);
    Runnable getOnInterruptRunnable();
}
