package com.daemonize.sound;

import java.io.File;

public class DummySoundManager implements SoundManager {

    @Override
    public SoundClip loadSoundClip(String name) throws SoundException {
        return null;
    }

    @Override
    public void playSound(SoundClip soundClip) {}

    @Override
    public SoundManager start() {
        return this;
    }

    @Override
    public void stop() {}

    @Override
    public void setResourceLocation(String path, boolean jar) {}

    @Override
    public DummySoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        return this;
    }
}
