package com.daemonize.daemonengine.utils;

import com.sun.tracing.dtrace.FunctionAttributes;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonSemaphore {// TODO inject the method reference instead of a flag

    @FunctionalInterface
    public static interface ConditionTester {
        boolean testCondition();
    }

    @FunctionalInterface
    public static interface CriticalSection {
        boolean execute();
    }

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    //private volatile boolean flag = false;
    private ConditionTester tester;

    public DaemonSemaphore(ConditionTester conditionTester) {
        this.tester = conditionTester;
    }

    public void signal(CriticalSection criticalSection) {
        lock.lock();
        //flag = true;
        if (criticalSection.execute())
            condition.signal();
        lock.unlock();
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(tester.testCondition()) {
                condition.await();
            }
        } finally {
            //flag = false;
            lock.unlock();
        }
    }

}
