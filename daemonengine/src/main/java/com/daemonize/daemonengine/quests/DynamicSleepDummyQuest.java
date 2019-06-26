package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class DynamicSleepDummyQuest extends DummyQuest {

    @FunctionalInterface
    public static interface SleepRegulator {
        long getSleepInterval();
    }

    private SleepRegulator sleepRegulator;

    public DynamicSleepDummyQuest(SleepRegulator sleepRegulator) {
        super();
        this.sleepRegulator = sleepRegulator;
    }

    public DummyQuest setSleepInterval(long milliseconds) {
        throw new IllegalStateException("Sorry I dont allow kind of sleep setting.");
    }

    @Override
    public void run() {
        try {
            long sleep = sleepRegulator.getSleepInterval();
            if (sleep > 0)
                Thread.sleep(sleep);
            consumer.consume(closure);
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
        }
    }
}
