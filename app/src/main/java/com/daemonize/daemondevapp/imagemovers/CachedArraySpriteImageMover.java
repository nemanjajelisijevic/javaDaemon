package com.daemonize.daemondevapp.imagemovers;

import android.util.Pair;

import com.daemonize.daemondevapp.images.Image;

import java.util.List;

public class CachedArraySpriteImageMover extends ImageTranslationMover {

    private AwaitedArraySprite<Image> cache;

    public boolean pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache = new AwaitedArraySprite<>(sprite);
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

    public CachedArraySpriteImageMover(List<Image> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }


}
