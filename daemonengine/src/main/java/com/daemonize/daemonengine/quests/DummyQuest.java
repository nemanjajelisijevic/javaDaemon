package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;

public class DummyQuest extends Quest<Void> {

    private long sleepInterval;

    public DummyQuest setSleepInterval(long milliseconds) {
        this.sleepInterval = milliseconds;
        return this;
    }

    public DummyQuest() {
        this.state = DaemonState.SIDE_QUEST;
    }

    public DummyQuest setClosure(Closure<Void> closure) {
        this.returnRunnable = new ReturnRunnable<>(closure);
        return this;
    }

    @Override
    protected Void pursue() {
        return null;
    }

    @Override
    public void run(){
        try {
            if (sleepInterval > 0)
                Thread.sleep(sleepInterval);
            setResultAndUpdate(null);
        } catch (InterruptedException ex) {
            //System.out.println(DaemonUtils.tag() + description + " interrupted.");
        }
    }
}
