package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonLatch {

    private String name = this.getClass().getSimpleName();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private volatile int counter;
    private int startCounter;


    public DaemonLatch(int startCount) {
        this.startCounter = startCount;
        this.counter = startCount;
    }

    public final int getCounter() {
        return counter;
    }

    public void decrement() {
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

    public DaemonLatch clear() {
        lock.lock();
        counter = 0;
        //condition.signalAll();
        lock.unlock();
        return this;
    }

    public DaemonLatch reset(int counter) {
        lock.lock();
        this.startCounter = counter;
        this.counter = counter;
        lock.unlock();
        return this;
    }

    public DaemonLatch reset() {
        lock.lock();
        this.counter = this.startCounter;
        lock.unlock();
        return this;
    }

    public String getName() {
        return name;
    }

    public DaemonLatch setName(String name) {
        this.name = name;
        return this;
    }
}
