package com.daemonize.daemondevapp;

import android.content.Context;
import android.media.MediaPlayer;

import com.daemonize.game.soundmanager.SoundClipPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidSoundClipPlayer implements SoundClipPlayer<File> {

    private Context context;
    private MediaPlayer player;

    public AndroidSoundClipPlayer(Context context, MediaPlayer player) {
        this.context = context;
        this.player = player;
    }

    @Override
    public void playClip(File soundClip) {
        FileInputStream fis = null;
        try {
            player.reset();
            fis = context.openFileInput(soundClip.getName());
            player.setDataSource(fis.getFD());
            player.prepare();
            player.setLooping(false);
            player.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException ignore) {}
        }
    }
}
