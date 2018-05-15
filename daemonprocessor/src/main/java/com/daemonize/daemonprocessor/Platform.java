package com.daemonize.daemonprocessor;

public enum Platform {
    ANDROID;

    public String getImplementationPackage() {
        return "androidconsumer";
    }

    public String getPlatformConsumer() {
        return "AndroidLooperConsumer";//TODO FixIfExpanded
    }
}
