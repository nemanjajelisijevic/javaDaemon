package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class InterruptibleSideQuest<T> extends SideQuest<T> {

    protected Consumer onInterruptConsumer;
    protected Runnable onInterruptClosure;

    public void onInterrupt(Consumer consumer, Runnable interruptClosure) {
        this.onInterruptConsumer = consumer;
        this.onInterruptClosure = interruptClosure;
    }

    public InterruptibleSideQuest() {
        super();
    }

    @SuppressWarnings("unchecked")
    public InterruptibleSideQuest<T> setClosure(Closure<T> closure) {
        super.setClosure(closure);
        return this;
    }

    @Override
    public boolean run(){
        try {
            T result = pursue();
            if (result != null)
                setResultAndUpdate(result);
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
