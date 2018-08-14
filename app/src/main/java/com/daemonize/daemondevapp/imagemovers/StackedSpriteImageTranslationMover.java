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

    private Stack<List<Bitmap>> stack = new Stack<>();

    private Lock spriteLock = new ReentrantLock();
    private Condition spriteDrawnCondition = spriteLock.newCondition();
    private volatile boolean spriteDrawn = false;

    public boolean pushSprite(List<Bitmap> sprite) throws InterruptedException {

        spriteLock.lock();
        stack.push(sprite);
        try {
            while (!spriteDrawn) {
                try {
                    spriteDrawnCondition.await();
                } catch (InterruptedException ex) {}
            }
        } finally {
            spriteDrawn = false;
            spriteLock.unlock();
            Log.e(DaemonUtils.tag(), "pushSprite() returns!!!!!!!!!!!!!!!!");
        }

        return true;
    }

    @Override
    protected Bitmap iterateSprite() {

        if (!spriteIterator.hasNext()) {

            if (stack.size() > 1) {
                sprite = stack.pop();
            } else
                sprite = stack.peek();

            spriteLock.lock();
            spriteDrawn = true;
            spriteDrawnCondition.signal();
            spriteLock.unlock();

            spriteIterator = sprite.iterator();
        }

        return spriteIterator.next();
    }

    public StackedSpriteImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
        stack.push(sprite);
        this.sprite = stack.peek();
    }

}
