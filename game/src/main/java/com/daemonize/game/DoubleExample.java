package com.daemonize.game;

//import android.util.Log;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.ConsumerArg;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemon
public class DoubleExample {

    private volatile int cnt;

    public DoubleExample() {}

    //@DedicatedThread(name = "FUCK")
//    @ConsumerArg
//    @GenerateRunnable
//    public void testVoid(long sleepMillis) throws InterruptedException {
//        Thread.sleep(sleepMillis);
//    }

    //@DedicatedThread(name = "FUCK")
    //@ConsumerArg
    @Daemonize(consumerArg = true)
    public synchronized boolean increment() throws InterruptedException {
        Thread.sleep(5000);
        //Log.d(DaemonUtils.tag(), "INSIDE DoubleExample increment COUNTER: " + Integer.toString(++cnt));
        return true;
    }

    @SideQuest
    public synchronized int logAndReturn() {
        //Log.i(DaemonUtils.tag(), "INSIDE DoubleExample logAndReturn COUNTER: " + Integer.toString(cnt));
        return ++cnt;
    }



}

