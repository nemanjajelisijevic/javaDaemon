package com.daemonize.daemondevapp.imagemovers;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

public class CachedArraySpriteImageMover extends ImageTranslationMover {

    private AwaitedArraySprite<Image> cache = new AwaitedArraySprite<>();

    public boolean pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        //cache = new AwaitedArraySprite<>(sprite);
        cache.setSprite(sprite);
        Image[] last = new Image[1];
        last[0] = sprite[sprite.length - 1];
        setSprite(last);
        cache.await();
        //cache.await(()->setSprite(last));
        //cache = null;
        cache.clearCache();
        return true;
    }

    @Override
    public Image iterateSprite() {
        //if (cache != null)
        if (cache.isValid())
            return cache.getNext();
        else
            return super.iterateSprite();
    }

    public CachedArraySpriteImageMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }
}
