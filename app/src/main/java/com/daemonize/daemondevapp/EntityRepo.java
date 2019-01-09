package com.daemonize.daemondevapp;


public interface EntityRepo<T> {

    @FunctionalInterface
    interface EntityConfigurator<T> {
        void configure(T entity);
    }

    boolean add(T entity);

    T get(EntityConfigurator<T> configurator);
    T get();

    void onAdd(T entity);
    T onGet(T entity);

    //List<T> asList();
    int size();

}
