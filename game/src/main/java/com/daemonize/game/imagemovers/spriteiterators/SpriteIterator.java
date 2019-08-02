package com.daemonize.game.imagemovers.spriteiterators;

import com.daemonize.graphics2d.images.Image;

public interface SpriteIterator {
    <K extends SpriteIterator> K setSprite(Image[] sprite);
    Image[] getSprite();
    int getSize();
    Image iterateSprite();
}
