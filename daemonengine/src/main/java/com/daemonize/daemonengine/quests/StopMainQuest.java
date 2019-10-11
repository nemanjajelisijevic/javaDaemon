package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.Daemon;

public class StopMainQuest extends VoidMainQuest {

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
