package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonSimpleSemaphore {

    private String name = this.getClass().getSimpleName();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public DaemonSimpleSemaphore() {}

    public void go() {
        lock.lock();
        flag = false;
        condition.signalAll();
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        flag = true;
        try {
            while(flag)
                condition.await();
        } finally {
            lock.unlock();
        }
    }

    public DaemonSimpleSemaphore setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " - Blocking: " + flag + "\n    Lock : " + lock.toString() + "\n    Condition: " + condition.toString();
    }
}
