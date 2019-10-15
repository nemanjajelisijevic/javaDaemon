package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class SleepSideQuest<T> extends SideQuest<T> {

    protected long sleepInterval;

    public SleepSideQuest() {
        this(null);
    }

    public SleepSideQuest(ClosureExecutionWaiter closureExecutionWaiter) {
        super(closureExecutionWaiter);
        this.sleepInterval = 10;
    }

    public <Q extends SleepSideQuest<T>> Q setSleepInterval(long milliseconds) {
        if (milliseconds < 1)
            throw new IllegalArgumentException("Sleep interval can not be less than 1");
        this.sleepInterval = milliseconds;
        return (Q) this;
    }

    @Override
    public boolean run(){
        try {
            T result = pursue();
            if (!Thread.currentThread().isInterrupted() && result != null)
                setResultAndUpdate(result);
            Thread.sleep(sleepInterval);
            return true;
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
            return true;
        } catch (Exception ex) {
            setErrorAndUpdate(ex);
            return false;
        }
    }

}
