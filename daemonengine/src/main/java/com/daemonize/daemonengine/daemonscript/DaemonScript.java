package com.daemonize.daemonengine.daemonscript;


public interface DaemonScript {
    <K extends DaemonScript> K addState(DaemonState state);
    void next();
    void run();
}
