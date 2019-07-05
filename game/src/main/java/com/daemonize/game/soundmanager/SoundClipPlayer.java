package com.daemonize.game.soundmanager;

import com.daemonize.daemonprocessor.annotations.Daemonize;

@Daemonize(eager = true)
public interface SoundClipPlayer<T> {
    void playClip(T soundClip);
}
