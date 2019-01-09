package com.daemonize.daemondevapp;

import java.util.LinkedList;
import java.util.Queue;

public abstract class QueuedEntityRepo<T> implements EntityRepo<T> {

    protected Queue<T> queue;

    public QueuedEntityRepo() {
        this.queue = new LinkedList<>();
    }

    public QueuedEntityRepo(Queue<T> queue) {
        this.queue = queue;
    }

    public void setQueue(Queue<T> queue) {
        this.queue = queue;
    }

    public Queue<T> getQueue() {
        return queue;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean add(T entity) {
        this.onAdd(entity);
        return queue.add(entity);
    }

    @Override
    public final T poll(EntityConfigurator<T> configurator) {
        T ret  = queue.poll();
        if (ret != null) {
            if (configurator != null)
                configurator.configure(ret);
            this.onPoll(ret);
        }
        return ret;
    }

    @Override
    public final T poll() {
        return poll(null);
    }

    @Override
    public abstract void onAdd(T entity);

    @Override
    public abstract T onPoll(T entity);
}
