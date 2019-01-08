package com.daemonize.daemonengine.consumer;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonConsumer implements Consumer, Daemon {

//    @FunctionalInterface
//    public static interface ConsumerStateNotifier {
//        void onStateChange(DaemonState state);
//    }

    private volatile DaemonState state = DaemonState.STOPPED;
    private Queue<Runnable> closureQueue = new LinkedList<>();
    private final Lock closureLock = new ReentrantLock();
    private Condition closureAvailable = closureLock.newCondition();
    private String name;
    private Thread looperThread;

//    private ConsumerStateNotifier stateNotifier = new ConsumerStateNotifier() {
//        @Override
//        public void onStateChange(DaemonState state) {}
//    };
//
//    public DaemonConsumer setStateNotifier(ConsumerStateNotifier stateNotifier) {
//        this.stateNotifier = stateNotifier;
//        return this;
//    }

    public DaemonConsumer(String name) {
        this.name = name;
    }

    @Override
    public boolean consume(Runnable runnable) {
        boolean ret;
        closureLock.lock();
        ret = closureQueue.add(runnable);
        if (closureQueue.size() == 1)
            closureAvailable.signal();
        closureLock.unlock();
        return ret;
    }

    private void loop() {

        Runnable closure;
        while (!state.equals(DaemonState.GONE_DAEMON)) {

            try {
                closureLock.lock();
                while (closureQueue.isEmpty()) {
                    state = DaemonState.IDLE;
                    //stateNotifier.onStateChange(this.state);
                    closureAvailable.await();
                }
                closure = closureQueue.poll();//TODO null safety
            } catch (InterruptedException ex) {
                //System.out.println(DaemonUtils.tag() + " Waiting on a closure interrupted");
                break;
            } finally { //TODO Handle Exceptions
                closureLock.unlock();
            }

            state = DaemonState.CONSUMING;
            closure.run();

        }

        state = DaemonState.STOPPED;
        System.out.println(DaemonUtils.tag() + name + " stopped!");
        //stateNotifier.onStateChange(this.state);
    }

    @Override
    public void start() {
        DaemonState initState = getState();
        if (initState.equals(DaemonState.STOPPED)) {
            looperThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    loop();
                }
            });
            looperThread.setName(name);
            state = DaemonState.INITIALIZING;
            //stateNotifier.onStateChange(this.state);
            looperThread.start();
        }
    }

    @Override
    public void stop() {
        state = DaemonState.GONE_DAEMON;
        //stateNotifier.onStateChange(this.state);
        if (looperThread != null && !Thread.currentThread().equals(looperThread) && looperThread.isAlive()) {
            looperThread.interrupt();
        }
    }

    @Override
    public void queueStop() {
        stop();
    }

    @Override
    public DaemonState getState() {
        return state;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DaemonConsumer setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <K extends Daemon> K setConsumer(Consumer consumer) {
        throw new IllegalStateException("This object already encapsulates a consumer thread. This operation is not permitted!");
    }

    public DaemonConsumer registerDaemon(Daemon daemon) {
        daemon.setConsumer(this);
        return this;
    }
}
