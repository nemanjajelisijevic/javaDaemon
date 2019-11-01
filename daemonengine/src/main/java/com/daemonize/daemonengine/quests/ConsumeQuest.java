package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.VoidReturnRunnable;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class ConsumeQuest extends VoidMainQuest {

    public ConsumeQuest(Runnable runnable) {
        this.returnRunnable = new VoidReturnRunnable(runnable);
    }

    @Override
    public Void pursue() throws Exception {
        daemonStateSetter.setState(DaemonState.CONSUMING);
        returnRunnable.run();
        return null;
    }
}
