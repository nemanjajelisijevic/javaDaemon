package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class DummyQuest extends BaseQuest<Void, DummyQuest> {

    private long sleepInterval;
    protected Runnable closure;

    public DummyQuest setSleepInterval(long milliseconds) {
        this.sleepInterval = milliseconds;
        return this;
    }

    public DummyQuest() {
        this.state = DaemonState.SIDE_QUEST;
    }

    public DummyQuest setClosure(Runnable closure) {
        this.closure = closure;
        return this;
    }

    @Override
    public Void pursue() {
        return null;
    }

    @Override
    public void run(){
        try {
            if (sleepInterval > 0)
                Thread.sleep(sleepInterval);
            consumer.consume(closure);
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
        }
    }
}
