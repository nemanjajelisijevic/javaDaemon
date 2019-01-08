package com.daemonize.daemondevapp.imagemovers.spriteiterators;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BasicSpriteIterator implements SpriteIterator {

    private volatile Image[] sprite;
    private int spriteIndex;
    private Lock spriteLock = new ReentrantLock();

    public BasicSpriteIterator(Image[] sprite){
        this.sprite = sprite;
    }

    @Override
    public int getSize() {
        return sprite.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BasicSpriteIterator setSprite(Image[] sprite) {
        spriteLock.lock();
        this.sprite = sprite;
        this.spriteIndex = 0;
        spriteLock.unlock();
        return this;
    }

    @Override
    public Image[] getSprite() {
        return sprite;
    }

    @Override
    public Image iterateSprite() {
        spriteLock.lock();
        if(spriteIndex == sprite.length) {
            spriteIndex = 0;
        }
        Image ret = sprite[spriteIndex++];
        spriteLock.unlock();
        return ret;
    }
}
