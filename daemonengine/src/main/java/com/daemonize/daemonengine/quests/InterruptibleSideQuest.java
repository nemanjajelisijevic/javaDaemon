package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonLatch;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSideQuest<T> extends SideQuest<T> implements InterruptibleQuest<InterruptibleSideQuest<T>> {

    private Runnable onInterruptRunnable;
    private DaemonLatch initLatch = new DaemonLatch(2).setName(this.getClass().getSimpleName() + " init latch");

    public InterruptibleSideQuest<T> onInterrupt(final Consumer consumer, final Runnable interruptClosure) {
        this.onInterruptRunnable = new Runnable() {
            @Override
            public void run() {
                consumer.consume(interruptClosure);
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
    public InterruptibleSideQuest<T> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        initLatch.decrement();
        return this;
    }

    @Override
    public boolean run(){
        try {
            if(initLatch.getCounter() > 0)
                daemonStateSetter.setState(DaemonState.INITIALIZING);
            initLatch.await();
            daemonStateSetter.setState(DaemonState.SIDE_QUEST);
            result = pursue();
            if (result != null) {
                daemonStateSetter.setState(DaemonState.AWAITING_CLOSURE);
                closureExecutionWaiter.awaitClosureExecution(resultRunnable);
                daemonStateSetter.setState(DaemonState.SIDE_QUEST);
            }
            return true;
        } catch (InterruptedException ex) {
            //System.out.println(DaemonUtils.tag() + description + " interrupted.");
            initLatch.clear();
            onInterruptRunnable.run();
            return false;
        } catch (Exception ex) {
            setErrorAndUpdate(ex);
            return false;
        }
    }
}
