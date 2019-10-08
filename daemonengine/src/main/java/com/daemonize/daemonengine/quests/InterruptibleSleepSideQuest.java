package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingLatch;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSleepSideQuest<T> extends SleepSideQuest<T> implements InterruptibleQuest<InterruptibleSleepSideQuest<T>>{

    private Runnable onInterruptRunnable;
    private DaemonCountingLatch initLatch = new DaemonCountingLatch(2).setName(this.getClass().getSimpleName() + " init latch");

    public InterruptibleSleepSideQuest() {
        super();
    }

    public InterruptibleSleepSideQuest<T> onInterrupt(final Consumer consumer, final Runnable interruptClosure) {
        this.onInterruptRunnable = new Runnable() {
            @Override
            public void run() {
                consumer.consume(interruptClosure);
            }
        };
        initLatch.unsubscribe();
        return this;
    }

    @Override
    public InterruptibleSleepSideQuest<T> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        initLatch.unsubscribe();
        return this;
    }

    @Override
    public InterruptibleSleepSideQuest<T> setSleepInterval(long milliseconds) {
        super.setSleepInterval(milliseconds);
        return this;
    }

    @Override
    public boolean run() {
        try {
            initLatch.await();
            T result = pursue();
            if (!Thread.currentThread().isInterrupted() && result != null)
                setResultAndUpdate(result);
            Thread.sleep(sleepInterval);
            return true;
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
            initLatch.clear();
            onInterruptRunnable.run();
            return false;
        } catch (Exception ex) {
            setErrorAndUpdate(ex);
            return false;
        }
    }

}
