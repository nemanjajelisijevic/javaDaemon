package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class VoidMainQuest extends MainQuest<Void> {

    private Runnable retRun;

    public VoidMainQuest setRetRun(Runnable retRun) {
        this.retRun = retRun;
        return this;
    }

    public VoidMainQuest() { super(); }

    public VoidMainQuest(Runnable retRun) {
        this();
        this.retRun = retRun;
    }

    @Override
    public boolean run() {
        try {
            pursue();
            if (!Thread.currentThread().isInterrupted() && retRun != null)
                consumer.consume(retRun);
            return true;
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
            return true;
        } catch (final Exception ex) {
            final String tag = DaemonUtils.tag();
            consumer.consume(new Runnable() {
                @Override
                public void run() {
                    throw new DaemonRuntimeError(
                            "\nDaemon: "
                                    + tag
                                    + "method '" + description + "' threw an exception:\n"
                                    + ex.getClass().getCanonicalName()
                                    + ": "
                                    + ex.getMessage(),
                            ex
                    );
                }
            });
            return false;
        }
    }
}
