package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSleepSideQuest<T> extends SleepSideQuest<T> {

    private Consumer onInterruptConsumer;
    private Runnable onInterruptClosure;

    public InterruptibleSleepSideQuest() {
        super();
    }

    public void onInterrupt(Consumer consumer, Runnable interruptClosure) {
        this.onInterruptConsumer = consumer;
        this.onInterruptClosure = interruptClosure;
    }

    @SuppressWarnings("unchecked")
    public InterruptibleSleepSideQuest<T> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        return this;
    }

    @Override
    public boolean run() {
        try {
            T result = pursue();
            if (!Thread.currentThread().isInterrupted() && result != null)
                setResultAndUpdate(result);
            Thread.sleep(sleepInterval);
            return true;
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
            if (onInterruptConsumer != null && onInterruptClosure != null)
                onInterruptConsumer.consume(onInterruptClosure);
            return false;
        } catch (Exception ex) {
            setErrorAndUpdate(ex);
            return false;
        }
    }

}
