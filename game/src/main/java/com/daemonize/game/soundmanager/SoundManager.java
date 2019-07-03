package com.daemonize.game.soundmanager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface SoundManager {
    File loadFile(String name) throws URISyntaxException, IOException;
    void playSound(File soundFile);
}
