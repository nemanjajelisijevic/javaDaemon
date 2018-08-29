package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonSemaphore {

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public DaemonSemaphore() {}

    public void signal() {
        lock.lock();
        flag = true;
        condition.signal();
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(!flag) {
                condition.await();
            }
        } finally {
            flag = false;
            lock.unlock();
        }
    }

}
