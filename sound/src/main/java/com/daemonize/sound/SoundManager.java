package com.daemonize.sound;

import com.daemonize.daemonengine.DaemonService;

import java.io.File;

public interface SoundManager extends DaemonService<SoundManager> {
    File loadFile(String name) throws SoundException;
    void playSound(File soundFile);
    void loadBackgroundMusic(String backgroundMusicFile) throws SoundException;
    void playBackgroundMusic();
    SoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);
}
