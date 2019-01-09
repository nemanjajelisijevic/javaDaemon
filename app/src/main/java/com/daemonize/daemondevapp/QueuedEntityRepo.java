package com.daemonize.daemondevapp;

import java.util.LinkedList;
import java.util.Queue;

public abstract class QueuedEntityRepo<T> implements EntityRepo<Queue<T>, T> {

    protected Queue<T> queue;

    public QueuedEntityRepo() {
        this.queue = new LinkedList<>();
    }

    public QueuedEntityRepo(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void setStructure(Queue<T> structure) {
        this.queue = structure;
    }

    @Override
    public Queue<T> getStructure() {
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
    public final T get(EntityConfigurator<T> configurator) {
        T ret  = queue.poll();
        if (ret != null) {
            if (configurator != null)
                configurator.configure(ret);
            this.onGet(ret);
        }
        return ret;
    }

    @Override
    public final T get() {
        return get(null);
    }

    @Override
    public abstract void onAdd(T entity);

    @Override
    public abstract T onGet(T entity);
}
