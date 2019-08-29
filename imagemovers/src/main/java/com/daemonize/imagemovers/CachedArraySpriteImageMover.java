package com.daemonize.imagemovers;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;


public class CachedArraySpriteImageMover extends ImageTranslationMover {

    protected AwaitedArraySprite cache = new AwaitedArraySprite();

    public synchronized void pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        animateSemaphore.subscribe();
        this.velocity.intensity = velocity;
        cache.clearCache();
        cache.setSprite(sprite);
        setSprite(new Image[]{sprite[sprite.length - 1]});
        try {
            cache.await();
        } finally {
            cache.clearCache();
            animateSemaphore.unsubscribe();
        }
    }

    @CallingThread
    public void popSprite() {
        cache.clearCache();
    }

    @Override
    public Image iterateSprite() {
        if (cache.isValid())
            return cache.getNext();
        else
            return super.iterateSprite();
    }

    public CachedArraySpriteImageMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos, float dXY) {
        super(sprite, velocity, startingPos, dXY);
    }
}
