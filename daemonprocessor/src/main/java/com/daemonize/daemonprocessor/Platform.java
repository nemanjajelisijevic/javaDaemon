package com.daemonize.daemonprocessor;

public enum Platform {
    ANDROID;

    public String getPlatformConsumer() {
        return "AndroidLooperConsumer";//TODO FixIfExpanded
    }
}
