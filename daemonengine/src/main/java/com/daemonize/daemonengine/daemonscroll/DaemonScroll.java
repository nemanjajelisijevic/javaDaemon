package com.daemonize.daemonengine.daemonscroll;


public interface DaemonScroll {
    <K extends DaemonScroll> K addSpell(DaemonSpell spell);
    void next();
    void run();
}
