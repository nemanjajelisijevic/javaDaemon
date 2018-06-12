package com.daemonize.daemonengine.daemonscript;


public interface DaemonScript {
    <K extends DaemonScript> K addSpell(DaemonSpell spell);
    void next();
    void run();
}
