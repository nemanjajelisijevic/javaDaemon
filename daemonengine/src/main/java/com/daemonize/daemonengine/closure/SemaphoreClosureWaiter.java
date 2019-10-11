package com.daemonize.daemonengine.closure;

import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreClosureWaiter implements ClosureWaiter {

    private String name = "";
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public SemaphoreClosureWaiter() {}

    @Override
    public void markAwait() {
        lock.lock();
        flag = true;
        lock.unlock();
        System.err.println(DaemonUtils.tag() + name + " marked!");
    }

    @Override
    public void clear() {
        lock.lock();
        if (flag) {
            flag = false;
            condition.signal();
        }
        lock.unlock();
        System.err.println(DaemonUtils.tag() + name + " cleared!");
    }

    @Override
    public void awaitClosure() throws InterruptedException {
        lock.lock();
        try {
            while(flag) {
                System.err.println(DaemonUtils.tag() + name + " about to await!");
                condition.await();
            }
        } finally {
            flag = false;
            lock.unlock();
            System.err.println(DaemonUtils.tag() + name + " done awaiting!");
        }
    }

    public String getName() {
        return name;
    }

    public SemaphoreClosureWaiter setName(String name) {
        this.name = name;
        return this;
    }
}