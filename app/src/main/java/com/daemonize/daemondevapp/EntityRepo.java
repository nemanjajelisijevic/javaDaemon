package com.daemonize.daemondevapp;

import com.daemonize.daemonengine.Daemon;

import java.util.List;

public interface EntityRepo<T> {

    @FunctionalInterface
    public static interface EntityConfigurator<T> {
        void configure(T entity);
    }

    boolean add(T entity);
    boolean add(T entity,boolean executOnAdd);
    T poll(EntityConfigurator<T> configurator);
    T poll();

    void onAdd(T entity);
    T onPoll(T entity);

    //List<T> asList();
    int size();

}
