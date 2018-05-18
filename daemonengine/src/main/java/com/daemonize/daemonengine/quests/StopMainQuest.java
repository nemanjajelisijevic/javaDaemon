package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.closure.Closure;

public class StopMainQuest extends MainQuest<Void> {

    private Daemon daemon;

    public StopMainQuest(Daemon daemon, Closure<Void> closure) {
        super(closure);
    }

    @Override
    protected Void pursue() {
        daemon.stop(closure);
        return null;
    }
}
