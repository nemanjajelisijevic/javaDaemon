package com.daemonize.daemondevapp.imagemovers.spriteiterators;

import com.daemonize.daemondevapp.images.Image;

public class BasicSpriteIterator implements SpriteIterator {

    private Image[] sprite;
    private int spriteSize;
    private int spriteIndex;

    public BasicSpriteIterator(Image[] sprite){
        this.sprite = sprite;
        this.spriteSize = sprite.length;
    }

    @Override
    public int getSize() {
        return spriteSize;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BasicSpriteIterator setSprite(Image[] sprite) {
        this.spriteSize = sprite.length;
        this.sprite = sprite;
        return this;
    }

    @Override
    public Image[] getSprite() {
        return sprite;
    }

    @Override
    public Image iterateSprite() {
        if(spriteIndex == spriteSize) {
            spriteIndex = 0;
        }
        return sprite[spriteIndex++];
    }
}
