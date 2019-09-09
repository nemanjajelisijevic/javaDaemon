package com.daemonize.androidsound;

import android.content.Context;
import android.media.MediaPlayer;

import com.daemonize.sound.SoundClip;
import com.daemonize.sound.SoundClipPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidSoundClipPlayer implements SoundClipPlayer {

    private Context context;
    private MediaPlayer player;

    public AndroidSoundClipPlayer(Context context, MediaPlayer player) {
        this.context = context;
        this.player = player;
    }

    protected void playClip(AndroidSoundClip soundClip, boolean loop) {
        FileInputStream fis = null;
        try {
            player.reset();
            fis = context.openFileInput(soundClip.getImplementation().getName());
            player.setDataSource(fis.getFD());
            player.prepare();
            player.setLooping(loop);
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

    @Override
    public void playClip(SoundClip soundClip) {
        playClip((AndroidSoundClip) soundClip, false);
    }

    @Override
    public void stopClip() {
        if (player.isPlaying())
            player.stop();
    }
}
