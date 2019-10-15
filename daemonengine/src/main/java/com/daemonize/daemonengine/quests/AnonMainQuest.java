package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;

public class AnonMainQuest<T> extends MainQuest<T> {

    private Quest<T> userQuest;

    public AnonMainQuest(Quest<T> userQuest, Closure<T> closure, ClosureExecutionWaiter closureExecutionWaiter) {
        super(closure, closureExecutionWaiter);
        this.userQuest = userQuest;
    }

    @Override
    public T pursue() throws Exception {
        return userQuest.pursue();
    }
}
