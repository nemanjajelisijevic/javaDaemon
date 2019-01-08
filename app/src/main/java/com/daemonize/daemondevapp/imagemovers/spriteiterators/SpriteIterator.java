package com.daemonize.daemondevapp.imagemovers.spriteiterators;

import com.daemonize.daemondevapp.images.Image;

public interface SpriteIterator {
    <K extends SpriteIterator> K setSprite(Image[] sprite);
    Image[] getSprite();
    int getSize();
    Image iterateSprite();
}
