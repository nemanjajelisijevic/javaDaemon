package com.daemonize.daemondevapp.imagemovers;


import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

public class CachedArraySpriteImageMover extends ImageTranslationMover {

    private AwaitedArraySprite<Image> cache;

    public boolean pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache = new AwaitedArraySprite<>(sprite);
        Image[] last = new Image[1];
        last[0] = sprite[sprite.length - 1];
        cache.await(()->setSprite(last));
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

    public CachedArraySpriteImageMover(Image [] sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
    }


}
