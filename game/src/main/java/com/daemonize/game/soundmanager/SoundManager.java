package com.daemonize.game.soundmanager;

import com.daemonize.daemonengine.DaemonService;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface SoundManager extends DaemonService<SoundManager> {
    File loadFile(String name) throws SoundException;
    void playSound(File soundFile);
    void loadBackgroundMusic(String backgroundMusicFile) throws SoundException;
    void playBackgroundMusic();
    SoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);
}
