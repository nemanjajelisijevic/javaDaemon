package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;

public class CachedArraySpriteImageMover extends ImageTranslationMover {

    private AwaitedArraySprite<Bitmap> cache;

    public boolean pushSprite(Bitmap[] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache = new AwaitedArraySprite<>(sprite);
        this.sprite.clear();
        this.sprite.add(sprite[sprite.length - 1]);
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

    public CachedArraySpriteImageMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }


}
