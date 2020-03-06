package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.exceptions.DaemonRuntimeError;
import com.daemonize.daemonengine.utils.DaemonUtils;

public abstract class VoidMainQuest extends MainQuest<Void> {

    public VoidMainQuest() {}

    @Override
    public boolean run() {
        try {
            daemonStateSetter.setState(DaemonState.MAIN_QUEST);
            pursue();
            return true;
        } catch (InterruptedException ex) {
            //System.out.println(DaemonUtils.tag() + description + " interrupted.");
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
