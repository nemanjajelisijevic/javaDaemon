package com.daemonize.sound;

public interface SoundClipPlayer<T> {
    void playClip(T soundClip);
    void stopClip();
}
