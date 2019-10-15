package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedVoidReturnRunnable;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.closure.VoidReturnRunnable;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class ReturnVoidMainQuest extends VoidMainQuest {

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            consumer.consume(returnRunnable);
        }
    };

    public ReturnVoidMainQuest(Runnable retRun, ClosureExecutionWaiter closureExecutionWaiter) {
        super();
        if (closureExecutionWaiter != null) {
            this.closureExecutionWaiter = closureExecutionWaiter;
            this.returnRunnable = new AwaitedVoidReturnRunnable(closureExecutionWaiter, retRun);
        } else {
            this.returnRunnable = new VoidReturnRunnable(retRun);
        }
    }

    @Override
    public boolean run() {
        try {
            pursue();
            if (!Thread.currentThread().isInterrupted())
                closureExecutionWaiter.awaitClosureExecution(updateRunnable);
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
