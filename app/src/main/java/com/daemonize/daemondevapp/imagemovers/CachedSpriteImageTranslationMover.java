package com.daemonize.daemondevapp.imagemovers;


import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;


public class CachedSpriteImageTranslationMover extends ImageTranslationMover {

    private AwaitedSprite<Image> cache;

    public boolean pushSprite(Image [] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache = new AwaitedSprite<>(sprite);
        cache.await();
        cache = null;
        return true;
    }

    @Override
    public Image iterateSprite() {
        if (cache != null)
            return cache.getNext();
        else
            return super.iterateSprite();
    }

    public CachedSpriteImageTranslationMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }

}
