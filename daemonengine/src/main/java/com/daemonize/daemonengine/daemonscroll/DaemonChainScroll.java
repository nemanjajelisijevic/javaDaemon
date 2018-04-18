package com.daemonize.daemonengine.daemonscroll;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DaemonChainScroll implements DaemonScroll {

    private List<DaemonSpell> chain = new ArrayList<>(10);
    private Iterator<DaemonSpell> it;

    @SuppressWarnings("unchecked")
    public DaemonChainScroll addSpell(DaemonSpell spell) {
        chain.add(spell);
        return this;
    }

    public void next() {
        if (it.hasNext()){
            it.next().cast();
        }
    }

    public void run() {
        it = chain.iterator();
        next();
    }

}
