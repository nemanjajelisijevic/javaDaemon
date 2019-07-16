package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingLatch;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSideQuest<T> extends SideQuest<T> implements InterruptibleQuest<InterruptibleSideQuest<T>> {

    private Runnable onInterruptRunnable;
    private DaemonCountingLatch initLatch = new DaemonCountingLatch(2).setName(this.getClass().getSimpleName() + " init latch");

    @Override
    public Runnable getOnInterruptRunnable() {
        return onInterruptRunnable;
    }

    public InterruptibleSideQuest<T> onInterrupt(final Consumer consumer, final Runnable interruptClosure) {
        this.onInterruptRunnable = new Runnable() {
            @Override
            public void run() {
                consumer.consume(interruptClosure);
            }
        };
        initLatch.unsubscribe();
        return this;
    }

    public InterruptibleSideQuest() {
        super();
    }

    @Override
    public InterruptibleSideQuest<T> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        initLatch.unsubscribe();
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
            return false;
        } catch (Exception ex) {
            setErrorAndUpdate(ex);
            return false;
        }
    }
}
