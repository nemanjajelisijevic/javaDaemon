package com.daemonize.game.soundmanager;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@Daemonize(eager = true)
public interface SoundManager {
    @CallingThread
    File loadFile(String name) throws URISyntaxException, IOException;
    void playSound(File soundFile);
}
