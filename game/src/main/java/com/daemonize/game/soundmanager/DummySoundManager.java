package com.daemonize.game.soundmanager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DummySoundManager implements SoundManager {

    @Override
    public File loadFile(String name) throws SoundException {
        return null;
    }

    @Override
    public void loadBackgroundMusic(String backgroundMusicFile) throws SoundException {}

    @Override
    public void playBackgroundMusic() {}

    @Override
    public void playSound(File soundFile) {}

    @Override
    public SoundManager start() {
        return this;
    }

    @Override
    public void stop() {}

    @Override
    public DummySoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        return this;
    }
}
