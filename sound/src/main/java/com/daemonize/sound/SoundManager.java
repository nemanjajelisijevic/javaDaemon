package com.daemonize.sound;

import com.daemonize.daemonengine.DaemonService;

public interface SoundManager<T extends SoundClip> extends DaemonService<SoundManager> {
    T loadSoundClip(String name) throws SoundException;
    void playSound(T soundClip);
    void setResourceLocation(String path);
    void setJarResourceLocation(String jarResourceLocation, String jarInternalLocation);
    SoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);
}
