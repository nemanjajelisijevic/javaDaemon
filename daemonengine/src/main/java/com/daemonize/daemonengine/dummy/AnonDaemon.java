package com.daemonize.daemonengine.dummy;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;
import com.daemonize.daemonengine.implementations.mainquestdaemon.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.quests.AnonMainQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.VoidMainQuest;

import java.util.List;

public class AnonDaemon implements EagerDaemon<AnonDaemon> {

    private EagerMainQuestDaemonEngine engine;

    public AnonDaemon(Consumer consumer) {
        engine = new EagerMainQuestDaemonEngine(consumer);
    }

    public <T> AnonDaemon daemonize(Quest<T> quest, Closure<T> closure) {
        engine.addMainQuest((AnonMainQuest)new AnonMainQuest(quest, closure).setConsumer(engine.getConsumer())); //TODO check ret
        return this;
    }

    public AnonDaemon daemonize(final Runnable quest, Runnable closure) {
        engine.addMainQuest((VoidMainQuest)new VoidMainQuest(closure) {
            @Override
            public Void pursue() throws Exception {
                quest.run();
                return null;
            }
        }.setConsumer(engine.getConsumer()));
        return this;
    }

    @Override
    public AnonDaemon interrupt() {
        engine.interrupt();
        return this;
    }

    @Override
    public AnonDaemon clearAndInterrupt() {
        engine.clearAndInterrupt();
        return this;
    }

    @Override
    public AnonDaemon start() {
        engine.start();
        return this;
    }

    @Override
    public void stop() {
        engine.stop();
    }

    @Override
    public AnonDaemon queueStop() {
        engine.queueStop(this);
        return this;
    }

    @Override
    public List<DaemonState> getEnginesState() {
        return engine.getEnginesState();
    }

    @Override
    public AnonDaemon setName(String name) {
        engine.setName(name);
        return this;
    }

    @Override
    public String getName() {
        return engine.getName();
    }

    @Override
    public AnonDaemon setConsumer(Consumer consumer) {
        engine.setConsumer(consumer);
        return this;
    }

    @Override
    public AnonDaemon clear() {
        engine.clear();
        return this;
    }
}
