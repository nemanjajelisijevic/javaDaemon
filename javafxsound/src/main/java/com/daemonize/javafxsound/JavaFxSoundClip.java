package com.daemonize.javafxsound;

import com.daemonize.sound.SoundClip;

import javax.sound.sampled.Clip;

public class JavaFxSoundClip implements SoundClip<Clip> {

    private Clip soundClipImp;

    public JavaFxSoundClip(Clip soundClipImp) {
        this.soundClipImp = soundClipImp;
    }

    @Override
    public Clip getImplementation() {
        return soundClipImp;
    }

    @Override
    public SoundClip<Clip> setImplementation(Clip soundClipImp) {
        this.soundClipImp = soundClipImp;
        return this;
    }
}
