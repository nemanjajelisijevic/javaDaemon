package com.daemonize.daemondevapp.imagemovers;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitedSprite<T> {

//    private List<T> list;
//    private Iterator<T> spriteIterator;
    private T[] sprite;
    private int spriteSize;
    private int spriteIndex;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean flag = false;

    public AwaitedSprite(T[] sprite) {
         this.sprite = sprite;
         this.spriteSize = (sprite == null) ? 0 : sprite.length ;
         this.spriteIndex = 0;
    }

//    public boolean add(T element) {
//        return list.add(element);
//    }

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

    public T getNext() {
//        if (!spriteIterator.hasNext()) {
//            lock.lock();
//            flag = true;
//            condition.signal();
//            lock.unlock();
//            spriteIterator = list.iterator();
//        }
//
//        return spriteIterator.next();

        if (spriteIndex >= spriteSize) {
            lock.lock();
            flag = true;
            condition.signal();
            lock.unlock();
            spriteIndex = 0;
        }
        return sprite[spriteIndex++];
    }


}
