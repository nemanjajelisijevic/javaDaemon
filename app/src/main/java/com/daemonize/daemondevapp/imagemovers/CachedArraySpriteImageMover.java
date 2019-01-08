package com.daemonize.daemondevapp.imagemovers;

import android.util.Log;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonUtils;

import static java.lang.Thread.currentThread;

public class CachedArraySpriteImageMover extends ImageTranslationMover {

    private AwaitedArraySprite<Image> cache = new AwaitedArraySprite<>();

    public boolean pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        cache.setSprite(sprite);
        setSprite(new Image[]{sprite[sprite.length - 1]});
        cache.await();
        cache.clearCache();
        return true;
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
