package com.daemonize.daemonengine.daemonscript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DaemonChainScript implements DaemonScript {

    private List<DaemonLink> chain = new ArrayList<>(10);

    private Iterator<DaemonLink> it;

    @SuppressWarnings("unchecked")
    public DaemonChainScript addLink(DaemonLink link) {
        chain.add(link);
        return this;
    }

    public void next() {
        if (it.hasNext()){
            it.next().execute();
        }
    }

    public void run() {
        it = chain.iterator();
        next();
    }

}
