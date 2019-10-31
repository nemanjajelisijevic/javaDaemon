package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.VoidReturnRunnable;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class DummyQuest extends BaseQuest<Void, DummyQuest> {

    private long sleepInterval;
    protected DaemonSemaphore pauseSemaphore = new DaemonSemaphore();

    public DummyQuest setSleepInterval(long milliseconds) {
        this.sleepInterval = milliseconds;
        return this;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }

    public DummyQuest() {}

    public DummyQuest setClosure(Runnable closure) {
        this.returnRunnable = new VoidReturnRunnable(closure);
        return this;
    }

    @Override
    public Void pursue() {
        return null;
    }

    public DummyQuest pause() {
        pauseSemaphore.stop();
        return this;
    }

    public DummyQuest cont() {
        pauseSemaphore.go();
        return this;
    }

    @Override
    public boolean run() {
        try {
            daemonStateSetter.setState(DaemonState.IDLE);
            if (sleepInterval > 0)
                Thread.sleep(sleepInterval);
            pauseSemaphore.await();
            daemonStateSetter.setState(DaemonState.CONSUMING);
            consumer.consume(returnRunnable);
            return true;
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
            return true;
        }
    }
}
