package com.daemonize.daemonengine.daemonscript;


public interface DaemonScript {
    <K extends DaemonScript> K addLink(DaemonLink link);
    void next();
    void run();
}
