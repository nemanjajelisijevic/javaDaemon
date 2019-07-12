package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonCountingLatch {

    private String name = this.getClass().getSimpleName();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile int counter = 0;

    public DaemonCountingLatch() {}

    public DaemonCountingLatch(int startCount) {
        this.counter = startCount;
    }

    public void subscribe() {
        lock.lock();
        counter++;
        lock.unlock();
    }

    public void unsubscribe() {
        lock.lock();
        if (--counter < 1) {
            condition.signalAll();
            counter = 0;
        }
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(counter > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public DaemonCountingLatch clear() {
        counter = 0;
        return this;
    }

    public String getName() {
        return name;
    }

    public DaemonCountingLatch setName(String name) {
        this.name = name;
        return this;
    }
}
