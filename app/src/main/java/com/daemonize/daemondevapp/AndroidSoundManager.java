package com.daemonize.daemondevapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.daemonize.game.soundmanager.SoundManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;

public class AndroidSoundManager implements SoundManager {

    private MediaPlayer player;
    private Context context;


    public AndroidSoundManager(Context cnt) {
        this.player = new MediaPlayer();
        this.context = cnt;
    }

    @Override
    public File loadFile(String name) throws URISyntaxException, IOException {

        InputStream is = getClass().getResourceAsStream("/" + name);
        String newName = "new" + name;
        FileOutputStream fos = context.openFileOutput(newName, Context.MODE_PRIVATE);

        try {
            int res = Integer.MAX_VALUE;
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
    public void playSound(File soundFile){


        FileInputStream fis = null;
        try {

            fis = context.openFileInput(soundFile.getName());
            player.setDataSource(fis.getFD());

            player.prepare();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            player.setLooping(false);
            player.start();
        } catch (FileNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
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


//        try {
//            player.setDataSource(soundFile.toURI());
//            player.prepare();
//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    mp.release();
//                }
//            });
//            player.setLooping(false);
//            player.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
