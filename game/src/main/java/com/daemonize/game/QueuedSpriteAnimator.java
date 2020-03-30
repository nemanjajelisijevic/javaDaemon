package com.daemonize.game;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueuedSpriteAnimator implements SpriteAnimator<QueuedSpriteAnimator> {

    private Queue<Image[]> spriteQueue;
    private final Lock spriteLock = new ReentrantLock();
    private final Condition spriteCondition = spriteLock.newCondition();

    private final ImageMover.PositionedImage ret = new ImageMover.PositionedImage();

    public QueuedSpriteAnimator(float x, float y) {
        spriteQueue = new LinkedList<>();
        ret.positionX = x;
        ret.positionY = y;
    }

    @Override
    public QueuedSpriteAnimator setSprite(Image[] sprite) {
        spriteLock.lock();
        spriteQueue.add(sprite);
        if (spriteQueue.size() == 1)
            spriteCondition.signalAll();
        spriteLock.unlock();
        return this;
    }

    @Override
    public QueuedSpriteAnimator setCoords(float x, float y) {
        ret.positionX = x;
        ret.positionY = y;
        return this;
    }

    @Override
    public ImageMover.PositionedImage animate() throws InterruptedException {

        spriteLock.lock();

        try {

            while(spriteQueue.isEmpty())
                spriteCondition.await();



        } finally {
            spriteLock.unlock();
        }




        return null;
    }
}
