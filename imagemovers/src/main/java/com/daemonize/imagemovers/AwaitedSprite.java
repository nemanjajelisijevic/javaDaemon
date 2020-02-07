package com.daemonize.imagemovers;

import com.daemonize.graphics2d.images.Image;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitedSprite {

    private Image[] sprite;
    private int cnt = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean flag = false;

    public AwaitedSprite(){}

    public AwaitedSprite(Image[] sprite) {
        this.sprite = sprite;
    }

    public AwaitedSprite setSprite(Image[] sprite) {
        lock.lock();
        this.sprite = sprite;
        flag = false;
        lock.unlock();
        return this;
    }

    public AwaitedSprite clearCache() {
        lock.lock();
        sprite = null;
        lock.unlock();
        return this;
    }

    public boolean isValid() {
        boolean ret;
        lock.lock();
        ret = sprite != null;
        lock.unlock();
        return ret;
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

    public Image getNext() {

        lock.lock();

        if (cnt >= sprite.length)
            cnt = 0;

        Image ret = sprite[cnt++];

        if (cnt == sprite.length) {

            flag = true;
            condition.signal();
            cnt = 0;
        }

        lock.unlock();

        return ret;
    }


}
