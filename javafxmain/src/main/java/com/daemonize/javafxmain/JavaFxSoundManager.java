package com.daemonize.javafxmain;

import com.daemonize.game.soundmanager.SoundClipPlayerDaemon;
import com.daemonize.game.soundmanager.SoundException;
import com.daemonize.game.soundmanager.SoundManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        private List<Clip> soundClipList;
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
                soundClipList.add(soundClip);
            }
        }

        private Clip getClip() {
            return soundClipList.get(counter++ % noOfClipsPerSound);
        }
    }

    private int counter = 0;
    private int noOfChannelsPerClip;
    private Map<File, ClipManager> soundMap = new TreeMap();

    private Clip backGroundMusic;

    public JavaFxSoundManager(int noOfChannelsPerClip) {
        if (noOfChannelsPerClip < 1)
            throw new IllegalArgumentException("arg noOfChannelsPerClip can not be less than 1");
        this.noOfChannelsPerClip = noOfChannelsPerClip;
    }

    @Override
    public File loadFile(String name) throws SoundException {
        File soundFile = null;
        try {
            soundFile = new File(getClass().getResource("/" + name).getPath());//TODO remove root slash!!
            ClipManager clipManager = new ClipManager(noOfChannelsPerClip);
            clipManager.registerSound(soundFile);
            soundMap.put(soundFile, clipManager);
        } catch (Exception e) {
            throw new SoundException("Unable to load sound file: " + name, e);
        }

        return soundFile;
    }


    private void playClip(Clip soundClip) {
        soundClip.setFramePosition(0);  // Must always rewind!
        soundClip.loop(0);
        soundClip.start();
    }

    @Override
    public void playSound(File soundFile) {
        ClipManager clipManager = soundMap.get(soundFile);
        playClip(clipManager.getClip());
    }

    @Override
    public void loadBackgroundMusic(String backgroundMusicFile) throws SoundException {
        File soundFile = null;
        try {
            soundFile = new File(getClass().getResource("/" + backgroundMusicFile).getPath());//TODO remove root slash!!
            AudioFileFormat format = AudioSystem.getAudioFileFormat(soundFile);
            Clip soundClip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile.toURI().toURL());
            soundClip.loop(Integer.MAX_VALUE);
            soundClip.open(ais);
            backGroundMusic = soundClip;
        } catch (Exception e) {
            throw new SoundException("Unable to load sound file: " + backgroundMusicFile, e);
        }
    }

    @Override
    public void playBackgroundMusic() {
        backGroundMusic.setFramePosition(0);  // Must always rewind!
        backGroundMusic.start();
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
