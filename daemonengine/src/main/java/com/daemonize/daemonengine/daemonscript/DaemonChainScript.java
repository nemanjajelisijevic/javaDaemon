package com.daemonize.daemonengine.daemonscript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DaemonChainScript implements DaemonScript {

    private List<DaemonState> chain = new ArrayList<>(10);
    private Iterator<DaemonState> it;

    @SuppressWarnings("unchecked")
    public DaemonChainScript addState(DaemonState state) {
        chain.add(state);
        return this;
    }

    public void next() {
        if (it.hasNext()){
            it.next().enter();
        }
    }

    public void run() {
        it = chain.iterator();
        next();
    }

}
