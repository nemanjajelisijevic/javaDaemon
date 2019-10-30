package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.closure.BareClosure;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonLatch;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSideQuest<T, D extends Daemon<D>> extends SideQuest<T> implements InterruptibleQuest<InterruptibleSideQuest<T, D>, D> {

    private Runnable onInterruptRunnable;
    private DaemonLatch initLatch = new DaemonLatch(2).setName(this.getClass().getSimpleName() + " init latch");
    private D daemon;

    @Override
    public InterruptibleSideQuest<T, D> setDaemon(D daemon) {
        this.daemon = daemon;
        return this;
    }

    public InterruptibleSideQuest<T, D> onInterrupt(final Consumer consumer, final BareClosure<D> interruptClosure) {
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

    public InterruptibleSideQuest() {
        this(null);
    }

    public InterruptibleSideQuest(ClosureExecutionWaiter closureExecutionWaiter) {
        super(closureExecutionWaiter);
    }

    @Override
    public InterruptibleSideQuest<T, D> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        initLatch.decrement();
        return this;
    }

    @Override
    public boolean run(){
        try {
            initLatch.await();
            T result = pursue();
            if (result != null)
                setResultAndUpdate(result);
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
