package com.daemonize.androidsound;

import com.daemonize.sound.SoundClip;

import java.io.File;

public class AndroidSoundClip implements SoundClip<File> {

    private File soundClipImp;

    public AndroidSoundClip(File soundClipImp) {
        this.soundClipImp = soundClipImp;
    }

    public AndroidSoundClip() {
    }

    @Override
    public File getImplementation() {
        return soundClipImp;
    }

    @Override
    public SoundClip<File> setImplementation(File soundClipImp) {
        this.soundClipImp = soundClipImp;
        return this;
    }
}
