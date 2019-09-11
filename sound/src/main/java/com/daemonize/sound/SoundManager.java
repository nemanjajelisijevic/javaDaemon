package com.daemonize.sound;

import com.daemonize.daemonengine.DaemonService;

public interface SoundManager<T extends SoundClip> extends DaemonService<SoundManager> {
    T loadSoundClip(String name) throws SoundException;
    void playSound(T soundClip);
    void setResourceLocation(String path, boolean jar);
    SoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);
}
