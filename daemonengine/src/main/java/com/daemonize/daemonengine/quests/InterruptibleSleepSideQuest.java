package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.closure.BareClosure;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonLatch;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSleepSideQuest<T, D extends Daemon<D>> extends SleepSideQuest<T> implements InterruptibleQuest<InterruptibleSleepSideQuest<T, D>, D> {

    private Runnable onInterruptRunnable;
    private DaemonLatch initLatch = new DaemonLatch(2).setName(this.getClass().getSimpleName() + " init latch");
    private D daemon;

    @Override
    public InterruptibleSleepSideQuest<T, D> setDaemon(D daemon) {
        this.daemon = daemon;
        return this;
    }

    public InterruptibleSleepSideQuest() {
        this(null);
    }

    public InterruptibleSleepSideQuest(ClosureExecutionWaiter closureExecutionWaiter) {
        super(closureExecutionWaiter);
    }

    @Override
    public InterruptibleSleepSideQuest<T, D> onInterrupt(final Consumer consumer, final BareClosure<D> interruptClosure) {
        this.onInterruptRunnable = new Runnable() {
            @Override
            public void run() {
                consumer.consume(new Runnable() {
                    @Override
                    public void run() {
                        interruptClosure.onReturn(daemon);
                    }
                });
            }
        };
        initLatch.decrement();
        return this;
    }

    @Override
    public InterruptibleSleepSideQuest<T, D> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        initLatch.decrement();
        return this;
    }

    @Override
    public InterruptibleSleepSideQuest<T, D> setSleepInterval(long milliseconds) {
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
