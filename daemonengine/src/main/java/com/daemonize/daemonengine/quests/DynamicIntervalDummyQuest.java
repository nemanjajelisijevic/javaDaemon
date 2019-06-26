package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class DynamicIntervalDummyQuest extends DummyQuest {

    @FunctionalInterface
    public static interface IntervalRegulator {
        long getSleepInterval();
    }

    private IntervalRegulator intervalRegulator;

    public DynamicIntervalDummyQuest(IntervalRegulator intervalRegulator) {
        super();
        this.intervalRegulator = intervalRegulator;
    }

    public DummyQuest setSleepInterval(long milliseconds) {
        throw new IllegalStateException("Sorry I dont allow kind of sleep setting.");
    }

    @Override
    public void run() {
        try {
            long sleep = intervalRegulator.getSleepInterval();
            if (sleep > 0)
                Thread.sleep(sleep);
            consumer.consume(closure);
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
        }
    }
}
