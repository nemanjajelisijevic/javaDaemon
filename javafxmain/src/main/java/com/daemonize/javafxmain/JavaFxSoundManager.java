package com.daemonize.javafxmain;

import com.daemonize.game.soundmanager.SoundManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class JavaFxSoundManager implements SoundManager {

    @Override
    public File loadFile(String name) throws URISyntaxException {
        return new File(getClass().getResource("/" + name).getPath());
    }

    @Override
    public void playSound(File soundFile) {
        Media soundMedia = new Media(soundFile.toURI().toString());
        MediaPlayer player = new MediaPlayer(soundMedia);
        player.play();
    }
}
