package com.daemonize.daemonengine.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonSemaphore {

    private String name = this.getClass().getSimpleName();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public DaemonSemaphore() {}

    public void stop(){
        lock.lock();
        flag = true;
        lock.unlock();
    }

    public void go() {
        lock.lock();
        flag = false;
        condition.signalAll();
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(flag)
                condition.await();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return name + " - Blocking: " + flag + "\n    Lock : " + lock.toString() + "\n    Condition: " + condition.toString();
    }

    public String getName() {
        return name;
    }

    public DaemonSemaphore setName(String name) {
        this.name = name;
        return this;
    }
}
