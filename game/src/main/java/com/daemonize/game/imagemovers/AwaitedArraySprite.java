package com.daemonize.game.imagemovers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitedArraySprite<T> {

    private T[] sprite;
    private int cnt = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public AwaitedArraySprite(){}

    public AwaitedArraySprite(T[] sprite) {
        this.sprite = sprite;
    }

    public synchronized AwaitedArraySprite<T> setSprite(T[] sprite) {
        this.sprite = sprite;
        return this;
    }

    public synchronized AwaitedArraySprite<T> clearCache() {
        sprite = null;
        return this;
    }

    public synchronized boolean isValid() {
        return sprite != null;
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


    public void await(Runnable action) throws InterruptedException {
        lock.lock();
        try {
            while(!flag)
                condition.await();
        } finally {
            flag = false;
            if (action != null)
                action.run();
            lock.unlock();
        }
    }

    public synchronized T getNext() {

        if (cnt >= sprite.length) {
            cnt = 0;
        }

        T ret = sprite[cnt++];

        if (cnt == sprite.length) {
            lock.lock();
            flag = true;
            condition.signal();
            lock.unlock();
            cnt = 0;
        }

        return ret;
    }


}
