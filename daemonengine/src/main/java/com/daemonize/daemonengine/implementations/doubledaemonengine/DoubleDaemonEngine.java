package com.daemonize.daemonengine.implementations.doubledaemonengine;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.EagerDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.implementations.mainquestdaemon.EagerMainQuestDaemonEngine;
import com.daemonize.daemonengine.implementations.sidequestdaemon.SideQuestDaemonEngine;
import com.daemonize.daemonengine.quests.AnonMainQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.SideQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;

import java.util.ArrayList;
import java.util.List;

public class DoubleDaemonEngine implements EagerDaemon<DoubleDaemonEngine> {

    private EagerMainQuestDaemonEngine mainQuestDaemonEngine;
    private SideQuestDaemonEngine sideQuestDaemonEngine;

    public DoubleDaemonEngine(Consumer consumer) {
        this.mainQuestDaemonEngine = new EagerMainQuestDaemonEngine(consumer).setName(this.getClass().getSimpleName());
        this.sideQuestDaemonEngine = new SideQuestDaemonEngine().setName(this.getClass().getSimpleName() + " - SIDE");
    }

    public <T> DoubleDaemonEngine daemonize(Quest<T> quest, Closure<T> closure) {
        mainQuestDaemonEngine.addMainQuest((AnonMainQuest<T>)new AnonMainQuest(quest, closure).setConsumer(mainQuestDaemonEngine.getConsumer()));
        return this;
    }

    public DoubleDaemonEngine daemonize(final Runnable quest, Runnable closure) {
        mainQuestDaemonEngine.addMainQuest((VoidMainQuest)new VoidMainQuest(closure) {
            @Override
            public Void pursue() throws Exception {
                quest.run();
                return null;
            }
        }.setConsumer(mainQuestDaemonEngine.getConsumer()));
        return this;
    }

    public <T> SideQuest<T> setSideQuest(Consumer consumer, final Quest<T> sideQuest) {
        sideQuestDaemonEngine.setSideQuest((SideQuest) new SideQuest() {
            @Override
            public T pursue() throws Exception {
                return sideQuest.pursue();
            }
        }.setConsumer(consumer));
        return sideQuestDaemonEngine.getSideQuest();
    }

    @Override
    public DoubleDaemonEngine interrupt() {
        mainQuestDaemonEngine.interrupt();
        return this;
    }

    @Override
    public DoubleDaemonEngine clearAndInterrupt() {
        mainQuestDaemonEngine.clearAndInterrupt();
        return this;
    }

    @Override
    public DoubleDaemonEngine start() {
        mainQuestDaemonEngine.start();
        sideQuestDaemonEngine.start();
        return this;
    }

    @Override
    public void stop() {
        mainQuestDaemonEngine.stop();
        sideQuestDaemonEngine.stop();
    }

    @Override
    public DoubleDaemonEngine queueStop() {
        mainQuestDaemonEngine.queueStop(this);
        return this;
    }

    @Override
    public List<DaemonState> getEnginesState() {
        List<DaemonState> ret = new ArrayList<>(2);
        ret.add(mainQuestDaemonEngine.getState());
        ret.add(sideQuestDaemonEngine.getState());
        return ret;
    }

    @Override
    public DoubleDaemonEngine setName(String name) {
        mainQuestDaemonEngine.setName(name);
        sideQuestDaemonEngine.setName(name  + " - SIDE");
        return this;
    }

    @Override
    public String getName() {
        return mainQuestDaemonEngine.getName();
    }

    @Override
    public DoubleDaemonEngine setConsumer(Consumer consumer) {
        mainQuestDaemonEngine.setConsumer(consumer);
        return this;
    }

    @Override
    public DoubleDaemonEngine clear() {
        mainQuestDaemonEngine.clear();
        return this;
    }
}
