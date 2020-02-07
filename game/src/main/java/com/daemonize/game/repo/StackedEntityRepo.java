package com.daemonize.game.repo;

import java.util.Stack;

public abstract class StackedEntityRepo<T> implements EntityRepo<Stack<T>, T> {

    private String name;

    private Stack<T> stack;

    public StackedEntityRepo() {
        this.stack = new Stack<>();
    }

    public StackedEntityRepo(Stack<T> stack) {
        this.stack = stack;
    }

    @Override
    public void setStructure(Stack<T> structure) {
        this.stack = structure;
    }

    @Override
    public Stack<T> getStructure() {
        return stack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean add(T entity) {
        if (this.onAdd(entity)) {
            stack.push(entity);
            return true;
        } else
            return false;
    }

    @Override
    public void addAndConfigure(T entity, EntityConfigurator<T> configurator) {
        this.onAdd(entity);
        if (configurator != null)
            configurator.configure(entity);
        stack.push(entity);
    }

    @Override
    public T configureAndGet(EntityConfigurator<T> configurator) {
        T ret  = stack.pop();
        if (ret != null) {
            if (configurator != null)
                configurator.configure(ret);
            this.onGet(ret);
        }
        return ret;
    }

    @Override
    public T getAndConfigure(EntityConfigurator<T> configurator) {
        T ret = stack.pop();
        if(ret != null) {
            this.onGet(ret);
            if (configurator != null)
                configurator.configure(ret);
        }
        return ret;
    }

    @Override
    public T get() {
        return configureAndGet(null);
    }

    @Override
    public abstract boolean onAdd(T entity);

    @Override
    public abstract void onGet(T entity);

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public void forEach(EntityConfigurator<T> configurator) {
        for(T entity : getStructure())
            configurator.configure(entity);
    }
}
