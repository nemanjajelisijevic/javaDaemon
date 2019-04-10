package com.daemonize.daemonengine.implementations;

import com.daemonize.daemonengine.consumer.Consumer;

public abstract class MainQuestDaemonEngine extends MainQuestDaemonBaseEngine<MainQuestDaemonEngine> {

    public MainQuestDaemonEngine(Consumer consumer) {
        super(consumer);
    }
}
