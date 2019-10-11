package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonLatch;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSleepSideQuest<T> extends SleepSideQuest<T> implements InterruptibleQuest<InterruptibleSleepSideQuest<T>>{

    private Runnable onInterruptRunnable;
    private DaemonLatch initLatch = new DaemonLatch(2).setName(this.getClass().getSimpleName() + " init latch");

    public InterruptibleSleepSideQuest() {
        this(null);
    }

    public InterruptibleSleepSideQuest(ClosureWaiter closureWaiter) {
        super(closureWaiter);
    }

    public InterruptibleSleepSideQuest<T> onInterrupt(final Consumer consumer, final Runnable interruptClosure) {
        this.onInterruptRunnable = new Runnable() {
            @Override
            public void run() {
                consumer.consume(interruptClosure);
            }
        };
        initLatch.decrement();
        return this;
    }

    @Override
    public InterruptibleSleepSideQuest<T> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        initLatch.decrement();
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
