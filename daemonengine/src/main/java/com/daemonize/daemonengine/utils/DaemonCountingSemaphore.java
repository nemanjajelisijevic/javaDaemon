package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonCountingSemaphore {

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile int counter = 0;

    public void subscribe() {
        lock.lock();
        counter++;
        lock.unlock();
    }

    public void unsubscribe() {
        lock.lock();
        if (--counter < 1) {//TODO prevent counter to be less than 0
            condition.signalAll();
            counter = 0;
        }
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(counter != 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

}
