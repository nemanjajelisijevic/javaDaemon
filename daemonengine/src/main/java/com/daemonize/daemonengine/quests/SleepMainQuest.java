package com.daemonize.daemonengine.quests;

public class SleepMainQuest extends MainQuest<Void> {

    private long tts;

    public SleepMainQuest(long sleepInterval) {
        this.tts = sleepInterval;
    }

    @Override
    protected Void pursue() throws Exception {
        Thread.sleep(tts);
        return null;
    }
}
