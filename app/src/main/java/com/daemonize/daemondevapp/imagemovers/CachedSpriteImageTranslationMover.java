package com.daemonize.daemondevapp.imagemovers;


import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

import java.util.List;

public class CachedSpriteImageTranslationMover extends ImageTranslationMover {

    private AwaitedSprite<Image> cache;

    public boolean pushSprite(List<Image> sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache = new AwaitedSprite<>(sprite);
        cache.await();
        cache = null;
        return true;
    }

    @Override
    protected Image iterateSprite() {
        if (cache != null)
            return cache.getNext();
        else
            return super.iterateSprite();
    }

    public CachedSpriteImageTranslationMover(List<Image> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }

}
