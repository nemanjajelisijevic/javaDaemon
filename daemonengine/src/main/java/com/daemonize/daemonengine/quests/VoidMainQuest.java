package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.closure.AwaitedVoidReturnRunnable;
import com.daemonize.daemonengine.closure.ClosureWaiter;
import com.daemonize.daemonengine.closure.VoidReturnRunnable;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class VoidMainQuest extends MainQuest<Void> {

    private Runnable retRun;

    public VoidMainQuest() { super(); }

    public VoidMainQuest(Runnable retRun) {
        this();
        this.retRun = retRun;
        this.returnRunnable = new VoidReturnRunnable(retRun);
    }

    @Override
    public VoidMainQuest setClosureWaiter(ClosureWaiter closureWaiter) {
        this.closureWaiter = closureWaiter;
        this.returnRunnable = new AwaitedVoidReturnRunnable(this.closureWaiter).setRetRun(retRun);
        return this;
    }

    @Override
    public boolean run() {
        try {
            pursue();
            if (!Thread.currentThread().isInterrupted() && retRun != null) {
                closureWaiter.reset();
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
}
