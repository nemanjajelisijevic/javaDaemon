package com.daemonize.daemonengine.utils;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonCountingSemaphore {

    private String name = this.getClass().getSimpleName();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile int counter = 0;

    public DaemonCountingSemaphore() {}

    public void stop() {
        lock.lock();
        counter = 0;
        lock.unlock();
    }

    public void unsubscribe() {
        lock.lock();
        if (--counter < 1) {
            counter = 0;
        }
        lock.unlock();
    }

    public void subscribe() {
        lock.lock();
        if (counter++ == 0)
            condition.signalAll();
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (counter == 0)
                condition.await();
        } finally {
            lock.unlock();
        }
    }

    public String getName() {
        return name;
    }

    public DaemonCountingSemaphore setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return name + " - counter: " + counter + "\n   Lock : " + lock.toString() + "\n    Condition: " + condition.toString();
    }
}
