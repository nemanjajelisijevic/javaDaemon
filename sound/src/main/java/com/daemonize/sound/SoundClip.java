package com.daemonize.sound;

public interface SoundClip<T> {
    T getImplementation();
    SoundClip<T> setImplementation(T soundClipImp);
}
