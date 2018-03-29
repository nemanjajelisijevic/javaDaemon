package com.daemonize.daemonengine.daemonscript;


public interface DaemonScroll {
    <K extends DaemonScroll> K addLink(DaemonLink link);
    void next();
    void run();
}
