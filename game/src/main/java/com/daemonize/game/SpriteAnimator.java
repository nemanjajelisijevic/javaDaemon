package com.daemonize.game;

import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.imagemovers.ImageMover;

@Daemon
public interface SpriteAnimator {
    void setSprite(Image[] sprite);
    @SideQuest(SLEEP = 25, blockingClosure = true)
    ImageMover.PositionedImage animate();
}
