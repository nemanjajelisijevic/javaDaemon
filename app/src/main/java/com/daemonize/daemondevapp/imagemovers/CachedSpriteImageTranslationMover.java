package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

public class CachedSpriteImageTranslationMover extends ImageTranslationMover {

    private AwaitedSprite<Bitmap> cache;

    public boolean pushSprite(List<Bitmap> sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache = new AwaitedSprite<>(sprite);
        cache.await();
        cache = null;
        return true;
    }

    @Override
    protected Bitmap iterateSprite() {
        if (cache != null)
            return cache.getNext();
        else
            return super.iterateSprite();
    }

    public CachedSpriteImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }

}
