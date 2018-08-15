package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StackedSpriteImageTranslationMover extends ImageTranslationMover {

    private static class AwaitedSprite {

        private List<Bitmap> sprite;

        public List<Bitmap> getSprite() {
            return sprite;
        }

        private Lock spriteLock = new ReentrantLock();
        private Condition spriteDrawnCondition = spriteLock.newCondition();
        private volatile boolean spriteDrawn = false;

        public AwaitedSprite(List<Bitmap> sprite) {
            this.sprite = sprite;
        }

        public void await() throws InterruptedException {
            spriteLock.lock();
            try {
                while (!spriteDrawn) {
                    spriteDrawnCondition.await();
                }
            } finally {
                spriteDrawn = false;
                spriteLock.unlock();
            }
        }

        public AwaitedSprite signal() {
            spriteLock.lock();
            spriteDrawn = true;
            spriteDrawnCondition.signal();
            spriteLock.unlock();
            return this;
        }

    }


    private Stack<AwaitedSprite> stack = new Stack<>();
    private List<Bitmap> sprite;

    public boolean pushSprite(List<Bitmap> sprite) throws InterruptedException {

        AwaitedSprite awaitedSprite = new AwaitedSprite(sprite);
        stack.push(awaitedSprite);
        awaitedSprite.await();
        Log.e(DaemonUtils.tag(), "pushSprite() returns!!!!!!!!!!!!!!!!");

        return true;
    }

    @Override
    protected Bitmap iterateSprite() {

        if (!spriteIterator.hasNext()) {

            AwaitedSprite awaitedSprite;

            if (stack.size() > 1) {
                awaitedSprite = stack.pop();
            } else {
                awaitedSprite = stack.peek();
            }

            sprite = awaitedSprite.getSprite();
            awaitedSprite.signal();

            spriteIterator = sprite.iterator();
        }

        return spriteIterator.next();
    }

    public StackedSpriteImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
        stack.push(new AwaitedSprite(sprite));
        this.sprite = stack.peek().getSprite();
    }

}
