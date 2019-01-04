package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemonize(doubleDaemonize = true)
public class DoubleExample {

    private volatile int cnt;

    public DoubleExample() {}

    public synchronized boolean increment() throws InterruptedException {
        Thread.sleep(5000);
        Log.d(DaemonUtils.tag(), "INSIDE DoubleExample increment COUNTER: " + Integer.toString(++cnt));
        return true;
    }

    @SideQuest(SLEEP = 1000)
    public synchronized int logAndReturn() {
        Log.i(DaemonUtils.tag(), "INSIDE DoubleExample logAndReturn COUNTER: " + Integer.toString(cnt));
        return cnt;
    }

}

