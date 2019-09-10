package com.daemonize.javafxsound;


import com.daemonize.sound.SoundClip;
import com.daemonize.sound.SoundException;
import com.daemonize.sound.SoundManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class JavaFxSoundManager implements SoundManager {

    private static class ClipManager {

        private List<JavaFxSoundClip> soundClipList;
        private int counter = 0;
        private int noOfClipsPerSound;

        private ClipManager(int noOfClipsPerSound) {
            this.noOfClipsPerSound = noOfClipsPerSound;
            this.soundClipList = new ArrayList<>(noOfClipsPerSound);
        }

        private void registerSound(File soundFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
            for (int i = 0; i < noOfClipsPerSound; ++i) {
                AudioFileFormat format = AudioSystem.getAudioFileFormat(soundFile);
                Clip soundClip = AudioSystem.getClip();
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile.toURI().toURL());
                soundClip.open(ais);
                soundClipList.add(new JavaFxSoundClip(soundClip));
            }
        }

        private JavaFxSoundClip getClip() {
            return soundClipList.get(counter++ % noOfClipsPerSound);
        }
    }

    private int counter = 0;
    private int noOfChannelsPerClip;
    private Map<SoundClip, ClipManager> soundMap = new HashMap<>();// TODO this map is not really needed

    public JavaFxSoundManager(int noOfChannelsPerClip) {
        if (noOfChannelsPerClip < 1)
            throw new IllegalArgumentException("arg noOfChannelsPerClip can not be less than 1");
        this.noOfChannelsPerClip = noOfChannelsPerClip;
    }

    @Override
    public SoundClip loadSoundClip(String name) throws SoundException {
        ClipManager clipManager = null;
        try {
            clipManager = new ClipManager(noOfChannelsPerClip);
            clipManager.registerSound(new File(getClass().getResource("/" + name).getPath()));//TODO remove root slash!!
            soundMap.put(clipManager.soundClipList.get(0), clipManager);
        } catch (Exception e) {
            throw new SoundException("Unable to load sound file: " + name, e);
        }

        return clipManager.soundClipList.get(0);
    }


    private void playClip(Clip soundClip) {
        soundClip.setFramePosition(0);  // Must always rewind!
        soundClip.loop(0);
        soundClip.start();
    }

    @Override
    public void playSound(SoundClip soundClip) {
        playClip(soundMap.get(soundClip).getClip().getImplementation());
    }

    @Override
    public JavaFxSoundManager start() {
        return this;
    }

    @Override
    public void stop() {}

    @Override
    public JavaFxSoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        return this;
    }
}
