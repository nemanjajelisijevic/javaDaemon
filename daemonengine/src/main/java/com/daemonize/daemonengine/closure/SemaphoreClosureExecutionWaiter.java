package com.daemonize.daemonengine.closure;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreClosureExecutionWaiter implements ClosureExecutionWaiter {

    private volatile String name = "";
    private Lock lock;
    private Condition condition;
    private volatile boolean flag = false;

    public SemaphoreClosureExecutionWaiter() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    @Override
    public void endClosureWait() {
        lock.lock();
        if (flag) {
            flag = false;
            condition.signal();
        }
        lock.unlock();
    }

    @Override
    public void awaitClosureExecution(Runnable updateConsumerAction) throws InterruptedException {

        lock.lock();
        flag = true;
        updateConsumerAction.run();
        while(flag)
            condition.awaitUninterruptibly();//TODO check if awaitUninterruptedly !!!!!!!!!!!!!!!!!!!!

        Thread.interrupted();//clear interrupt flag!!!!!
        flag = false;
        lock.unlock();
    }

    public String getName() {
        return name;
    }

    public SemaphoreClosureExecutionWaiter setName(String name) {
        this.name = name;
        return this;
    }
}