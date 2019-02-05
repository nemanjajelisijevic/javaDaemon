package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonSimpleSemaphore {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile boolean flag = true;

    public DaemonSimpleSemaphore() {}

    public void go() {
        lock.lock();
        flag = true;
        condition.signalAll();
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(!flag)
                condition.await();
        } finally {
            flag = false;
            lock.unlock();
        }
    }
}
