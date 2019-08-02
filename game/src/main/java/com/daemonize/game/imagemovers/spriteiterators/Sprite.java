package com.daemonize.game.imagemovers.spriteiterators;

import com.daemonize.graphics2d.images.Image;

public class Sprite {

    private Image[] images;
    private long sleepInterval;

    public Sprite(Image[] images, long sleepInterval) {
        this.images = images;

        if (sleepInterval < 0)
            throw new IllegalArgumentException("Sleep interval cannot be less than 0 millis");

        this.sleepInterval = sleepInterval;
    }

    public Image[] getImages() {
        return images;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }
}
