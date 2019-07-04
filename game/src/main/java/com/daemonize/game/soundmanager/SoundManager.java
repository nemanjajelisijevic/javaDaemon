package com.daemonize.game.soundmanager;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@Daemonize(eager = true)
public interface SoundManager {

    @CallingThread
    File loadFile(String name) throws SoundException;

    void playSoundChannel1(File soundFile);

    @DedicatedThread
    void playSoundChannel2(File soundFile);
    @DedicatedThread
    void playSoundChannel3(File soundFile);
    @DedicatedThread
    void playSoundChannel4(File soundFile);

}
