package com.daemonize.game.soundmanager;

public class SoundException extends Exception {

    public SoundException(String message) {
        super(message);
    }

    public SoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoundException(Throwable cause) {
        super(cause);
    }
}
