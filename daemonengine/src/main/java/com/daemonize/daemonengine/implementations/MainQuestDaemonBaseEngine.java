package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonEngine;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.AnonMainQuest;
import com.daemonize.daemonengine.quests.MainQuest;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.StopMainQuest;
import com.daemonize.daemonengine.quests.ReturnVoidMainQuest;
import com.daemonize.daemonengine.quests.VoidMainQuest;
import com.daemonize.daemonengine.quests.VoidQuest;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class MainQuestDaemonBaseEngine<D extends MainQuestDaemonBaseEngine> extends BaseDaemonEngine<D> implements DaemonEngine<D> {

    protected final Queue<MainQuest> mainQuestQueue;
    protected final Lock mainQuestLock = new ReentrantLock();

    MainQuestDaemonBaseEngine(Consumer consumer) {
        super(consumer);
        this.mainQuestQueue = new LinkedList<>();
    }

    @Override
    public <T> D daemonize(Quest<T> quest, Closure<T> closure, boolean awaitedClosure) {
        return daemonize(getConsumer(), quest, closure, awaitedClosure);
    }

    @Override
    public <T> D daemonize(Consumer consumer, Quest<T> quest, Closure<T> closure, boolean awaitedClosure) {
      addMainQuest((AnonMainQuest<T>) new AnonMainQuest(quest, closure, awaitedClosure ? getClosureAwaiter() : null).setConsumer(consumer)); //TODO check ret
      return (D) this;
    }

    @Override
    public D daemonize(final VoidQuest quest, Runnable closure, boolean awaitedClosure) {
        return daemonize(getConsumer(), quest, closure, awaitedClosure);
    }

    @Override
    public D daemonize(final VoidQuest quest) {
        addMainQuest(new VoidMainQuest() {
            @Override
            public Void pursue() throws Exception {
                quest.pursue();
                return null;
            }
        }.setConsumer(consumer));
        return (D) this;
    }

    @Override
    public D daemonize(Consumer consumer, final VoidQuest quest, Runnable closure, boolean awaitedClosure) {
      addMainQuest(new ReturnVoidMainQuest(closure, awaitedClosure ? getClosureAwaiter() : null) {
          @Override
          public Void pursue() throws Exception {
              quest.pursue();
              return null;
          }
      }.setConsumer(consumer));
      return (D) this;
    }

    public boolean addMainQuest(MainQuest quest) {
        boolean ret;
        mainQuestLock.lock();
        ret = mainQuestQueue.add(quest);
        mainQuestLock.unlock();
        if (!ret)
            throw new IllegalStateException("Could not add to daemons(" + getName() + ") mainQuetQueue!!!");
        return ret;
    }

    public boolean pursueQuest(MainQuest quest) {
        return addMainQuest(quest);
    }

    //returns null
    @Override
    protected BaseQuest getQuest() {
        BaseQuest ret = null;
        mainQuestLock.lock();
        if (!mainQuestQueue.isEmpty()) {
          ret = mainQuestQueue.poll();
        }
        mainQuestLock.unlock();
        return ret;
    }

    @Override
    protected boolean runQuest(BaseQuest quest) {
        if(!quest.run()) {
            setDaemonState(DaemonState.GONE_DAEMON);
            return false;
        }
        return true;
    }

    @Override
    protected void cleanUp() {
        mainQuestLock.lock();
        mainQuestQueue.clear();
        mainQuestLock.unlock();
    }

    //@Override
    public D queueStop(Daemon daemon) {
        addMainQuest(new StopMainQuest(daemon));
        return (D) this;
    }

    @Override
    public void stop() {
        clear();
        super.stop();
    }

    public int queueSize() {
        return mainQuestQueue.size();
    }


    @Override
    public D clear() {
        mainQuestLock.lock();
        mainQuestQueue.clear();
        mainQuestLock.unlock();
        return (D) this;
    }

    @Override
    public D queueStop() {
        return queueStop(this);
    }
}
