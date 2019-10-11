package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.VoidReturnRunnable;

public class ConsumeQuest extends VoidMainQuest {

    public ConsumeQuest(Runnable runnable) {
        this.returnRunnable = new VoidReturnRunnable(runnable);
        this.state = DaemonState.CONSUMING;
    }

    @Override
    public Void pursue() throws Exception {
        returnRunnable.run();
        return null;
    }
}
