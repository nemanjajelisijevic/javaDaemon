package com.daemonize.androidsound;

import android.content.Context;
import android.media.MediaPlayer;

import com.daemonize.sound.SoundClipPlayer;
import com.daemonize.sound.SoundException;
import com.daemonize.sound.SoundManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AndroidSoundManager implements SoundManager<AndroidSoundClip> {

    private List<SoundClipPlayerDaemon> players; //TODO remove SoundClipPlayer and work directly with Media Player
    protected Context context;
    private int counter = 0;
    private int noOfChannels;

    public AndroidSoundManager(Context cnt, int noOfChannels) {
        this.context = cnt;

        if (noOfChannels < 1)
            throw new IllegalArgumentException("arg noOfChannels can not be less than 1");

        this.noOfChannels = noOfChannels;
        this.players = new ArrayList<>(noOfChannels);
        for (int i = 0; i < noOfChannels; ++i) {
            players.add(new SoundClipPlayerDaemon(null, new AndroidSoundClipPlayer(cnt, new MediaPlayer())));
        }
    }

    @Override
    public AndroidSoundClip loadSoundClip(String name) throws SoundException {

        String newName = "new" + name;
        try (
                InputStream is = getClass().getResourceAsStream("/" + name);
                FileOutputStream fos = context.openFileOutput(newName, Context.MODE_PRIVATE)
        ){
            int res = Integer.MIN_VALUE;
            while ((res = is.read()) != -1)
                fos.write(res);
        } catch (IOException e) {
            throw new SoundException("Unable to load sound file: " + name, e);
        }

        return new AndroidSoundClip(new File(newName));
    }

    private void playSound(int channel, AndroidSoundClip soundFile) {
        players.get(channel).playClip(soundFile);
    }

    @Override
    public void playSound(AndroidSoundClip soundClip) {
        playSound((counter++) % noOfChannels, soundClip);
    }

    @Override
    public AndroidSoundManager start() {
        for (SoundClipPlayerDaemon player : players)
            player.start();
        return this;
    }

    @Override
    public void stop() {
        for (SoundClipPlayerDaemon player : players) {
            player.getPrototype().stopClip();
            player.stop();
        }
    }

    @Override
    public void setResourceLocation(String path, boolean jar) {
        //TODO check this
    }

    @Override
    public AndroidSoundManager setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        for(SoundClipPlayerDaemon player : players)
            player.setUncaughtExceptionHandler(handler);
        return this;
    }
}
