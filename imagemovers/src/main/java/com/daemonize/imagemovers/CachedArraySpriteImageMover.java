package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.graphics2d.images.Image;


public class CachedArraySpriteImageMover extends ImageTranslationMover {

    protected AwaitedArraySprite cache = new AwaitedArraySprite();

    public synchronized void pushSprite(Image[] sprite) throws InterruptedException {

        cache.clearCache();
        cache.setSprite(sprite);
        setSprite(new Image[]{sprite[sprite.length - 1]});
        animateSemaphore.subscribe();
        try {
            cache.await();
        } finally {
            cache.clearCache();
            animateSemaphore.unsubscribe();
        }
    }

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

    public CachedArraySpriteImageMover(Image [] sprite, Pair<Float, Float> startingPos, float dXY) {
        super(sprite, startingPos, dXY);
    }
}
