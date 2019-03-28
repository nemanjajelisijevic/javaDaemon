package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.closure.Closure;

public class StopMainQuest extends MainQuest<Void> {

    private Daemon daemon;

    public StopMainQuest(Daemon daemon) {
        super();
        this.daemon = daemon;
    }

    @Override
    public Void pursue() {
        daemon.stop();
        return null;
    }
}
