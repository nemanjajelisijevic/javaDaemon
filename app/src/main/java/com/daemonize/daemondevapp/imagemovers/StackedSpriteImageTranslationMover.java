package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.List;
import java.util.Stack;

public class StackedSpriteImageTranslationMover extends ImageTranslationMover {

    private Stack<List<Bitmap>> stack = new Stack<>();

    public void pushSprite(List<Bitmap> sprite) {
        stack.push(sprite);
    }

    @Override
    protected Bitmap iterateSprite() {
        if (!spriteIterator.hasNext()) {
            if (stack.size() > 1)
                sprite = stack.pop();
            else {
                sprite = stack.peek();
            }
            spriteIterator = sprite.iterator();
        }

        return super.iterateSprite();
    }

    public StackedSpriteImageTranslationMover(List<Bitmap> sprite, float velocity, Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
        stack.push(sprite);
        this.sprite = stack.peek();
    }

}
