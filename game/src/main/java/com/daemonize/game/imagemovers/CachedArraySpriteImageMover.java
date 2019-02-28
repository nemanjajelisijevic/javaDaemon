package com.daemonize.game.imagemovers;

import com.daemonize.game.Pair;
import com.daemonize.game.images.Image;


public class CachedArraySpriteImageMover extends ImageTranslationMover {

    protected AwaitedArraySprite<Image> cache = new AwaitedArraySprite<>();

    public void pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        animateSemaphore.subscribe();
        this.velocity.intensity = velocity;
        cache.setSprite(sprite);
        setSprite(new Image[]{sprite[sprite.length - 1]});
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

    public CachedArraySpriteImageMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos, float dXY) {
        super(sprite, velocity, startingPos, dXY);
    }
}
