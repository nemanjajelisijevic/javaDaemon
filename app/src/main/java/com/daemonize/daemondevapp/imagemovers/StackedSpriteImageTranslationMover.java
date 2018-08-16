package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;
import java.util.Stack;

public class StackedSpriteImageTranslationMover extends ImageTranslationMover {

    private Stack<AwaitedSprite<Bitmap>> stack;
    private AwaitedSprite<Bitmap> cache;

    public boolean pushSprite(List<Bitmap> sprite, float velocity) throws InterruptedException {
        this.velocity.intensity = velocity;
        AwaitedSprite<Bitmap> awaitedSprite = new AwaitedSprite<>(sprite);
        stack.push(awaitedSprite);
        awaitedSprite.await();
        cache = null;
        return true;
    }

    @Override
    protected Bitmap iterateSprite() {

        if (cache != null) {
            return cache.getNext();
        } else if (stack.size() > 0) {
            cache = stack.pop();
            return cache.getNext();
        } else
            return super.iterateSprite();
    }

    public StackedSpriteImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
        this.stack = new Stack<>();
    }

}
