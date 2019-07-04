package com.daemonize.javafxmain;

import com.daemonize.game.soundmanager.SoundException;
import com.daemonize.game.soundmanager.SoundManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class JavaFxSoundManager implements SoundManager {

    private Map<File, Clip> soundMap = new TreeMap();

    @Override
    public File loadFile(String name) throws SoundException {
        File soundFile = null;
        try {
            soundFile = new File(getClass().getResource("/" + name).getPath());
            AudioFileFormat format = AudioSystem.getAudioFileFormat(soundFile);
            Clip soundClip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile.toURI().toURL());
            soundClip.open(ais);
            soundMap.put(soundFile, soundClip);
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return soundFile;
    }

    protected void playSound(File soundFile) {
        Clip clip = soundMap.get(soundFile);
        if (!clip.isRunning()) {
            clip.setFramePosition(0);  // Must always rewind!
            clip.loop(0);
            clip.start();
        }
    }

    protected void playSoundInterruptibly(File soundFile) {
        Clip clip = soundMap.get(soundFile);
        clip.setFramePosition(0);  // Must always rewind!
        clip.loop(0);
        clip.start();
    }

    @Override
    public void playSoundChannel1(File soundFile) {
        playSound(soundFile);
    }

    @Override
    public void playSoundChannel2(File soundFile) {
        playSound(soundFile);
    }

    @Override
    public void playSoundChannel3(File soundFile) {
        playSoundInterruptibly(soundFile);
    }

    @Override
    public void playSoundChannel4(File soundFile) {
        playSoundInterruptibly(soundFile);
    }
}
