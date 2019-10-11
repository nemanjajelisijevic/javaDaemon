package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.AwaitedVoidReturnRunnable;
import com.daemonize.daemonengine.closure.ClosureWaiter;
import com.daemonize.daemonengine.closure.VoidReturnRunnable;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class ReturnVoidMainQuest extends MainQuest<Void> {

    public ReturnVoidMainQuest(Runnable retRun, ClosureWaiter closureWaiter) {
        super();
        this.state = DaemonState.MAIN_QUEST;
        if (closureWaiter != null) {
            this.closureWaiter = closureWaiter;
            this.returnRunnable = new AwaitedVoidReturnRunnable(closureWaiter, retRun);
        } else {
            this.returnRunnable = new VoidReturnRunnable(retRun);
        }
    }

    @Override
    public boolean run() {
        try {
            pursue();
            if (!Thread.currentThread().isInterrupted()) {
                closureWaiter.markAwait();
                consumer.consume(returnRunnable);
                closureWaiter.awaitClosure();
            }
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

    @Override
    public boolean getIsVoid() {
        return true;
    }
}
