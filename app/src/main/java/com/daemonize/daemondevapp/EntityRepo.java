package com.daemonize.daemondevapp;


public interface EntityRepo<T> {

    @FunctionalInterface
    interface EntityConfigurator<T> {
        void configure(T entity);
    }

    boolean add(T entity);

    T poll(EntityConfigurator<T> configurator);
    T poll();

    void onAdd(T entity);
    T onPoll(T entity);

    //List<T> asList();
    int size();

}
