//package com.daemonize.daemondevapp;
//
//import android.content.Context;
//import android.media.MediaDataSource;
//import android.media.MediaPlayer;
//
//import com.daemonize.game.soundmanager.SoundException;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.TreeMap;
//
//public class Android23SoundManager extends AndroidSoundManager {
//
//
//    private static class InMemoryMediadataSource extends MediaDataSource {
//
//        byte[] soundbytes;
//
//        public InMemoryMediadataSource(byte[] soundbytes) {
//            this.soundbytes = soundbytes;
//        }
//
//        @Override
//        public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
//            try {
//                buffer = Arrays.copyOfRange(soundbytes, (int) position, (int) (position + size));
//            } catch (Exception e) {
//                return -1;
//            }
//            return buffer.length;
//        }
//
//        @Override
//        public long getSize() throws IOException {
//            return soundbytes.length;
//        }
//
//        @Override
//        public void close() throws IOException {}
//    }
//
//
//    private Map<File, InMemoryMediadataSource> soundmap = new TreeMap<>();
//
//
//    public Android23SoundManager(Context cnt) {
//        super(cnt);
//    }
//
//
//    @Override
//    public File loadFile(String name) throws SoundException {
//
//        InputStream is = null;
//        File soundFile = null;
//        try {
//            soundFile = new File(getClass().getResource("/" + name).getPath());
//            is = getClass().getResourceAsStream("/" + name);
//            long fileLength = soundFile.length();
//
//            ArrayList<Byte> tempSound = new ArrayList<>();
//
//            //byte[] toSoundBytes = new byte[(int)fileLength];
//            int res = Integer.MIN_VALUE;
//            byte[] buffer = new byte[1000];
//            while ((res = is.read(buffer)) != -1) {
//                for(int i = 0; i < res; ++i)
//                    tempSound.add(buffer[i]);
//            }
//
//            byte[] toSoundBytes = new byte[tempSound.size()];
//
//            for (int i = 0; i < tempSound.size(); ++i)
//                toSoundBytes[i] = tempSound.get(i);
//
//
////            ArrayList<Integer> tempList = new ArrayList<>();
////            is = getClass().getResourceAsStream("/" + name);
////            int res = Integer.MIN_VALUE;
////            while ((res = is.read()) != -1)
////                tempList.add(res);
////
////            int mask = 0x000000FF;
////
////            byte[] toSoundBytes = new byte[tempList.size()];
////
////            for (int i = 0; i < tempList.size(); ++i) {
////                toSoundBytes[i] = (byte)(((int)tempList.get(i)) & mask);
////            }
//
//
//
//
////            int[] tempArray = new int[tempList.size()];
////
////            for(int j = 0; j < tempList.size(); ++j)
////                tempArray[j] = tempList.get(j);
////
////            ByteBuffer byteBuffer = ByteBuffer.allocate(tempList.size() * 4);
////            IntBuffer intBuffer = byteBuffer.asIntBuffer();
////            intBuffer.put(tempArray);
////            byte[] toSoundBytes = byteBuffer.array();
////
//
//            InMemoryMediadataSource soundClip = new InMemoryMediadataSource(toSoundBytes);
//
//            soundmap.put(soundFile, soundClip);
//
//        } catch (IOException e) {
//            throw new SoundException("Unable to load sound file: " + name, e);
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {}
//            }
//        }
//
//        return soundFile;
//    }
//
//    @Override
//    protected void playSound(MediaPlayer player, File soundFile) {
//
//        InMemoryMediadataSource soundClip = soundmap.get(soundFile);
//        player.reset();
//        player.setDataSource(soundClip);
//        try {
//            player.prepare();
//            player.setLooping(false);
//            player.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
