package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonCountingLatch;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSleepSideQuest<T> extends SleepSideQuest<T> implements InterruptibleQuest<InterruptibleSleepSideQuest<T>>{

//    private Consumer onInterruptConsumer;
//    private Runnable onInterruptClosure;
    private Runnable onInterruptRunnable;
    private DaemonCountingLatch initLatch = new DaemonCountingLatch(2).setName(this.getClass().getSimpleName() + " init latch");

    @Override
    public Runnable getOnInterruptRunnable() {
        return onInterruptRunnable;
    }

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
//        this.onInterruptConsumer = consumer;
//        this.onInterruptClosure = interruptClosure;
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
//            if (onInterruptConsumer != null && onInterruptClosure != null) {
//                onInterruptConsumer.consume(onInterruptClosure);
//            } else
//                throw new IllegalStateException(DaemonUtils.tag() + this.description + " onInterruptConsumer or onInterruptClosure not set!");
            initLatch.clear();
            return false;
        } catch (Exception ex) {
            setErrorAndUpdate(ex);
            return false;
        }
    }

}
