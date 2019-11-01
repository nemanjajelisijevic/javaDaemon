package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;

public class SleepMainQuest extends MainQuest<Void> {

    private long tts;

    public SleepMainQuest(long sleepInterval) {
        this.tts = sleepInterval;
    }

    @Override
    public Void pursue() throws Exception {
        daemonStateSetter.setState(DaemonState.IDLE);
        Thread.sleep(tts);
        daemonStateSetter.setState(DaemonState.MAIN_QUEST);
        return null;
    }
}
