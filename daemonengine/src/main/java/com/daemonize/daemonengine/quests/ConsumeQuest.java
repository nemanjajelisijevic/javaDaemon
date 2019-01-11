package com.daemonize.daemonengine.quests;

public class ConsumeQuest extends MainQuest<Void> {

    private Runnable runnable;

    public ConsumeQuest(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Void pursue() throws Exception {
        runnable.run();
        return null;
    }
}
