package com.daemonize.daemonengine.dummy;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.BaseDaemonEngine;
import com.daemonize.daemonengine.quests.DummyQuest;
import com.daemonize.daemonengine.quests.BaseQuest;

public class DummyDaemon extends BaseDaemonEngine<DummyDaemon> {

    private DummyQuest dummyQuest;

    public static DummyDaemon create(Consumer consumer, long sleep) {
        return new DummyDaemon(consumer, sleep);
    }

    @Override
    protected BaseQuest getQuest() {
        return dummyQuest;
    }

    public DummyDaemon(Consumer consumer, long sleepInMillis) {
        super(consumer);
        dummyQuest = new DummyQuest().setConsumer(consumer).setSleepInterval(sleepInMillis);
    }

    @Override
    public DummyDaemon setConsumer(Consumer consumer) {
        super.setConsumer(consumer);
        return this;
    }

    public DummyDaemon setClosure(Runnable closure) {
        dummyQuest.setClosure(closure);
        return this;
    }

    public DummyDaemon setSleepInterval(long sleepInMillis) {
        dummyQuest.setSleepInterval(sleepInMillis);
        return this;
    }

    @Override
    public DummyDaemon clear() {
        return this;
    }
}
