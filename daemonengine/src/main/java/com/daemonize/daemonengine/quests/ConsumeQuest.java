package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;

public class ConsumeQuest extends MainQuest<Void> {

    private Runnable runnable;

    public ConsumeQuest(Runnable runnable) {
        this.runnable = runnable;
        this.state = DaemonState.CONSUMING;
    }

    @Override
    protected Void pursue() throws Exception {
        runnable.run();
        return null;
    }
}
