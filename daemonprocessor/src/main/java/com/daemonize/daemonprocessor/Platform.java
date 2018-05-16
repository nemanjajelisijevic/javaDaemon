package com.daemonize.daemonprocessor;

public enum Platform {

    ANDROID (1);

    Platform(int p) {
        switch (p){
            case 1:
                implementationPackageName = "androidconsumer";
                platformConsumerName = "AndroidLooperConsumer";
                break;
            default:
                throw new IllegalStateException("No way Hose!");
        }
    }

    private String implementationPackageName;
    private String platformConsumerName;

    public String getImplementationPackage() {
        return implementationPackageName;
    }

    public String getPlatformConsumer() {
        return platformConsumerName;
    }
}
