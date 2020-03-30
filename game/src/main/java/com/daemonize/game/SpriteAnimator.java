package com.daemonize.game;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;

@Daemon
public interface SpriteAnimator<T extends SpriteAnimator> {
    T setSprite(Image[] sprite);
    T setCoords(float x, float y);
    @SideQuest(SLEEP = 25, blockingClosure = true)
    ImageMover.PositionedImage animate() throws InterruptedException;
}
