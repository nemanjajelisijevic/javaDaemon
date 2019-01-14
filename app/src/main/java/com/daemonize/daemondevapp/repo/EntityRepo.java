package com.daemonize.daemondevapp.repo;

import java.util.Collection;

public interface EntityRepo<C extends Collection<T>, T> {

    @FunctionalInterface
    interface EntityConfigurator<T> {
        void configure(T entity);
    }

    void setStructure(C structure);
    C getStructure();

    boolean add(T entity);

    T configureAndGet(EntityConfigurator<T> configurator);
    T getAndConfigure(EntityConfigurator<T> configurator);
    T get();

    void onAdd(T entity);
    T onGet(T entity);

    //List<T> asList();
    int size();

}
