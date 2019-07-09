package com.daemonize.daemondevapp;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;

import com.daemonize.game.soundmanager.SoundClipPlayer;
import com.daemonize.game.soundmanager.SoundClipPlayerDaemon;
import com.daemonize.game.soundmanager.SoundException;
import com.daemonize.game.soundmanager.SoundManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class AndroidSoundManager implements SoundManager {

    private List<SoundClipPlayerDaemon<File>> players; //TODO remove SoundClipPlayer and work directly with Media Player
    protected Context context;
    private int counter = 0;
    private int noOfChannels;

    private SoundClipPlayer<File> backGroundMusicPlayer;
    private File backgroundMusic;

    public AndroidSoundManager(Context cnt, int noOfChannels) {
        this.context = cnt;

        if (noOfChannels < 1)
            throw new IllegalArgumentException("arg noOfChannels can not be less than 1");

        this.noOfChannels = noOfChannels;
        this.players = new ArrayList<>(noOfChannels);
        for (int i = 0; i < noOfChannels; ++i) {
            players.add(new SoundClipPlayerDaemon<File>(null, new AndroidSoundClipPlayer(cnt, new MediaPlayer())));
        }

        backGroundMusicPlayer = new AndroidBackgroundMusicPlayer(cnt, new MediaPlayer());
    }

    @Override
    public File loadFile(String name) throws SoundException {

        InputStream is = null;
        FileOutputStream fos = null;
        String newName = "new" + name;

        try {

            is = getClass().getResourceAsStream("/" + name);
            fos = context.openFileOutput(newName, Context.MODE_PRIVATE);

            int res = Integer.MIN_VALUE;
            while ((res = is.read()) != -1)
                fos.write(res);

        } catch (Exception e) {
            throw new SoundException("Unable to load sound file: " + name, e);
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException ex) {}
        }

        return new File(newName);
    }

    private void playSound(int channel, File soundFile) {
        players.get(channel).playClip(soundFile);
    }

    @Override
    public void playSound(File soundFile) {
        playSound((counter++) % noOfChannels,  soundFile);
    }

    @Override
    public AndroidSoundManager start() {
        for (SoundClipPlayerDaemon player : players) {
            player.start();
        }
        return this;
    }

    @Override
    public void stop() {
        for (SoundClipPlayerDaemon player : players) {
            player.getPrototype().stopClip();
            player.stop();
        }
        backGroundMusicPlayer.stopClip();
    }

    @Override
    public void loadBackgroundMusic(String backgroundMusic) throws SoundException {
        try {
            FileInputStream fis = context.openFileInput("new" + backgroundMusic);
            this.backgroundMusic = new File("new" + backgroundMusic);
            if (fis != null) fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            File bckMusic = loadFile(backgroundMusic);
            this.backgroundMusic = bckMusic;
        }
    }

    @Override
    public void playBackgroundMusic() {
        backGroundMusicPlayer.playClip(backgroundMusic);
    }

    @Override
    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        for(SoundClipPlayerDaemon player : players)
            player.setUncaughtExceptionHandler(handler);
    }
}
