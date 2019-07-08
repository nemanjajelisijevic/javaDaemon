package com.daemonize.daemondevapp;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.daemonize.daemonengine.DaemonService;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.game.soundmanager.ByteBufferBackedInputStream;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AndroidSoundManager implements SoundManager {

    private List<SoundClipPlayerDaemon<InputStreamMediaDataSource>> players; //TODO remove SoundClipPlayer and work directly with Media Player
    protected Context context;
    private int counter = 0;
    private int noOfChannels;

    private SoundClipPlayer<File> backGroundMusicPlayer;
    //private File backgroundMusic;


    private Map<File, InputStreamMediaDataSource> mediaMap;

    public AndroidSoundManager(Context cnt, int noOfChannels) {
        this.context = cnt;

        if (noOfChannels < 1)
            throw new IllegalArgumentException("arg noOfChannels can not be less than 1");

        this.noOfChannels = noOfChannels;
        this.players = new ArrayList<>(noOfChannels);
        for (int i = 0; i < noOfChannels; ++i) {
            players.add(new SoundClipPlayerDaemon<InputStreamMediaDataSource>(new Consumer() {

                Handler handler = new Handler(Looper.getMainLooper());

                @Override
                public boolean consume(Runnable runnable) {
                    return handler.post(runnable);
                }

                @Override
                public DaemonService start() {
                    return null;
                }

                @Override
                public void stop() {
                }
            }, new AndroidSoundClipPlayer(cnt, new MediaPlayer())));
        }

        //backGroundMusicPlayer = new AndroidBackgroundMusicPlayer(cnt, new MediaPlayer());
        mediaMap = new TreeMap<>();
    }

    @Override
    public File loadFile(String name) throws SoundException {

        InputStream is = null;
        //FileOutputStream fos = null;
        String newName = "new" + name;

        try {
            ArrayList<Byte> soundBytes = new ArrayList<>();
            is = getClass().getResourceAsStream("/" + name);
            //fos = context.openFileOutput(newName, Context.MODE_PRIVATE);

            File soundFile = new File("/" + name);

            int res = Integer.MIN_VALUE;
            while ((res = is.read()) != -1)
                //fos.write(res);
                soundBytes.add((byte) res);

            byte[] raw = new byte[soundBytes.size()];

            for(int i = 0; i < soundBytes.size(); ++i)
                raw[i] = soundBytes.get(i);

            ByteBuffer buff = ByteBuffer.allocate(soundBytes.size());
            buff.put(raw);


            ByteBufferBackedInputStream buffStream = new ByteBufferBackedInputStream(buff);

            InputStreamMediaDataSource source = new InputStreamMediaDataSource(buffStream, buffStream.getLength());

            mediaMap.put(soundFile, source);

        } catch (Exception e) {
            throw new SoundException("Unable to load sound file: " + name, e);
        } finally {
            try {
                if (is != null) is.close();
                //if (fos != null) fos.close();
            } catch (IOException ex) {}
        }

        return new File(newName);
    }

    private void playSound(int channel, File soundFile) {
        players.get(channel).playClip(mediaMap.get(soundFile));
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
//        try {
//            FileInputStream fis = context.openFileInput("new" + backgroundMusic);
//            this.backgroundMusic = new File("new" + backgroundMusic);
//            if (fis != null) fis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            File bckMusic = loadFile(backgroundMusic);
//            this.backgroundMusic = bckMusic;
//        }
    }

    @Override
    public void playBackgroundMusic() {
        //backGroundMusicPlayer.playClip(backgroundMusic);
    }
}
