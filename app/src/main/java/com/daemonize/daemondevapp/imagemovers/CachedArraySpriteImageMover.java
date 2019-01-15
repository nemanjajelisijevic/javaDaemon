package com.daemonize.daemondevapp.imagemovers;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;


public class CachedArraySpriteImageMover extends ImageTranslationMover {

    protected AwaitedArraySprite<Image> cache = new AwaitedArraySprite<>();

    public void pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache.setSprite(sprite);
        setSprite(new Image[]{sprite[sprite.length - 1]});
        cache.await();
        cache.clearCache();
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

    public CachedArraySpriteImageMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }
}
