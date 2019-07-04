package com.daemonize.daemondevapp;

import android.content.Context;
import android.media.MediaPlayer;

import com.daemonize.game.soundmanager.SoundManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class AndroidSoundManager implements SoundManager {

    private MediaPlayer playerChannel1;
    private MediaPlayer playerChannel2;
    private MediaPlayer playerChannel3;
    private MediaPlayer playerChannel4;
    private Context context;

    public AndroidSoundManager(Context cnt) {
        this.playerChannel1 = new MediaPlayer();
        this.playerChannel2 = new MediaPlayer();
        this.playerChannel3 = new MediaPlayer();
        this.playerChannel4 = new MediaPlayer();
        this.context = cnt;
    }

    protected void playSound(MediaPlayer player, File soundFile) {
        FileInputStream fis = null;
        try {
            player.reset();
            fis = context.openFileInput(soundFile.getName());
            player.setDataSource(fis.getFD());
            player.prepare();
            player.setLooping(false);
            player.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public File loadFile(String name) throws URISyntaxException, IOException {

        InputStream is = getClass().getResourceAsStream("/" + name);
        String newName = "new" + name;
        FileOutputStream fos = context.openFileOutput(newName, Context.MODE_PRIVATE);

        try {
            int res = Integer.MIN_VALUE;
            while ((res = is.read()) != -1) {
                fos.write(res);
            }
        } finally {
            is.close();
            fos.close();
        }

        return new File(newName);
    }

    @Override
    public void playSoundChannel1(File soundFile){
        playSound(playerChannel1, soundFile);
    }

    @Override
    public void playSoundChannel2(File soundFile){
        playSound(playerChannel2, soundFile);
    }

    @Override
    public void playSoundChannel3(File soundFile){
        playSound(playerChannel3, soundFile);
    }

    @Override
    public void playSoundChannel4(File soundFile){
        playSound(playerChannel4, soundFile);
    }
}
