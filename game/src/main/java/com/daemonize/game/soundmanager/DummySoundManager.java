package com.daemonize.game.soundmanager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DummySoundManager implements SoundManager {

    @Override
    public File loadFile(String name) throws URISyntaxException, IOException {
        return null;
    }

    @Override
    public void playSoundChannel1(File soundFile) {

    }

    @Override
    public void playSoundChannel2(File soundFile) {

    }

    @Override
    public void playSoundChannel3(File soundFile) {

    }

    @Override
    public void playSoundChannel4(File soundFile) {

    }
}
