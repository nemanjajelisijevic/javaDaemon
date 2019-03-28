package com.daemonize.daemonengine.quests;

@FunctionalInterface
public interface Quest<T> {
    T pursue() throws Exception;
}
