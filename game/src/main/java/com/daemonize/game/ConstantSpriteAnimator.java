package com.daemonize.game;

import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;
import com.daemonize.imagemovers.spriteiterators.BasicSpriteIterator;
import com.daemonize.imagemovers.spriteiterators.SpriteIterator;

public class ConstantSpriteAnimator implements SpriteAnimator<ConstantSpriteAnimator> {

    private ImageMover.PositionedImage posImg;
    private SpriteIterator spriteIterator;

    public ConstantSpriteAnimator(float x, float y, Image[] sprite) {
        this.posImg = new ImageMover.PositionedImage();
        this.posImg.positionX = x;
        this.posImg.positionY = y;
        this.spriteIterator = new BasicSpriteIterator(sprite);
    }

    @Override
    public ConstantSpriteAnimator setCoords(float x, float y) {
        posImg.positionX = x;
        posImg.positionY = y;
        return this;
    }

    @Override
    public ConstantSpriteAnimator setSprite(Image[] sprite) {
        spriteIterator.setSprite(sprite);
        return this;
    }

    @Override
    public ImageMover.PositionedImage animate() {
        posImg.image = spriteIterator.iterateSprite();
        return posImg;
    }

}
