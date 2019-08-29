package com.daemonize.daemonengine.quests;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class DynamicIntervalDummyQuest extends DummyQuest {

    private DaemonUtils.IntervalRegulator intervalRegulator;

    public DynamicIntervalDummyQuest(DaemonUtils.IntervalRegulator intervalRegulator) {
        super();
        this.intervalRegulator = intervalRegulator;
    }

    public DummyQuest setSleepInterval(long milliseconds) {
        throw new IllegalStateException("Sorry I dont allow kind of sleep setting.");
    }

    @Override
    public boolean run() {
        try {
            long sleep = intervalRegulator.getSleepInterval();
            if (sleep > 0)
                Thread.sleep(sleep);
            consumer.consume(closure);
            return true;
        } catch (InterruptedException ex) {
            System.out.println(DaemonUtils.tag() + description + " interrupted.");
            return true;
        }
    }
}
