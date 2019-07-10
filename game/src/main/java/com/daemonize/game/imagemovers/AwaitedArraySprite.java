package com.daemonize.game.imagemovers;

import com.daemonize.game.images.Image;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitedArraySprite {

    private Image[] sprite;
    private int cnt = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public AwaitedArraySprite(){}

    public AwaitedArraySprite(Image[] sprite) {
        this.sprite = sprite;
    }

    public synchronized AwaitedArraySprite setSprite(Image[] sprite) {
        this.sprite = sprite;
        lock.lock();
        flag = false;
        lock.unlock();
        return this;
    }

    public synchronized AwaitedArraySprite clearCache() {
        sprite = null;
        return this;
    }

    public boolean isValid() {
        return sprite != null;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while(!flag)
                condition.await();
        } finally {
            //flag = false;
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

    public synchronized Image getNext() {

        if (cnt >= sprite.length)
            cnt = 0;

        Image ret = sprite[cnt++];

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
