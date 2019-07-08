package com.daemonize.daemondevapp;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.daemonize.game.soundmanager.ByteBufferBackedInputStream;
import com.daemonize.game.soundmanager.SoundClipPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AndroidSoundClipPlayer implements SoundClipPlayer<InputStreamMediaDataSource> {

    private Context context;
    private MediaPlayer player;
//    private ByteBufferBackedInputStream stream;
//    private InputStreamMediaDataSource source;


    public AndroidSoundClipPlayer(Context context, MediaPlayer player) {
        this.context = context;
        this.player = player;

    }

    protected void playClip(InputStreamMediaDataSource soundClip, boolean loop) {
        //FileInputStream fis = null;
        try {
            player.reset();
            //fis = context.openFileInput(soundClip.getName());
            //player.setDataSource(fis.getFD());
            player.setDataSource(soundClip);
            player.prepare();
            player.setLooping(loop);
            player.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //if (fis != null)
//            try {
//                //fis.close();
//                stream.close();
//            } catch (IOException ignore) {}
        }
    }

    @Override
    public void playClip(InputStreamMediaDataSource soundClip) {
        playClip(soundClip, false);
    }

    @Override
    public void stopClip() {
        if (player.isPlaying())
            player.stop();
    }
}
