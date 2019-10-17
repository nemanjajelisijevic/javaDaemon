package com.daemonize.daemonengine.consumer;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonConsumer implements Consumer<DaemonConsumer>, Daemon<DaemonConsumer> {

    private volatile DaemonState state = DaemonState.STOPPED;
    private Queue<Runnable> closureQueue = new LinkedList<>();
    private final Lock closureLock = new ReentrantLock();
    private final Condition closureAvailable = closureLock.newCondition();
    private String name;
    private Thread looperThread;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Runnable closureRunnable;

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
        if (!ret) throw new IllegalStateException(DaemonUtils.tag() + "Could not add to consumers(" + name + ") queue!!!!");
        return ret;
    }

    private void loop() {

        while (!state.equals(DaemonState.GONE_DAEMON)) {

            try {
                closureLock.lock();
                while (closureQueue.isEmpty()) {
                    state = DaemonState.IDLE;
                    closureAvailable.await();
                }
                closureRunnable = closureQueue.poll();//TODO null safety
                //System.err.println(DaemonUtils.tag() + closureQueue.size());
            } catch (InterruptedException ex) {
                System.out.println(DaemonUtils.tag() + name + " interrupted!");
                break;
            } finally { //TODO Handle Exceptions
                closureLock.unlock();
            }

            state = DaemonState.CONSUMING;
            closureRunnable.run();
            closureRunnable = null;
        }

        state = DaemonState.STOPPED;
        System.out.println(DaemonUtils.tag() + name + " stopped!");
    }

    @Override
    public DaemonConsumer start() {
        DaemonState initState = getState();
        if (initState.equals(DaemonState.STOPPED)) {
            looperThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    loop();
                }
            });
            looperThread.setName(name);
            looperThread.setPriority(Thread.MAX_PRIORITY);
            state = DaemonState.INITIALIZING;
            if (uncaughtExceptionHandler != null)
                looperThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            looperThread.start();
        }
        return this;
    }

    @Override
    public void stop() {
        state = DaemonState.GONE_DAEMON;
        if (looperThread != null && !Thread.currentThread().equals(looperThread) && looperThread.isAlive()) {
            looperThread.interrupt();
        }
    }

    @Override
    public DaemonConsumer queueStop() {
        stop();
        return this;
    }

    public DaemonState getState() {
        return state;
    }

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
    public DaemonConsumer setConsumer(Consumer consumer) {
        throw new IllegalStateException("This object already encapsulates a consumer thread. This operation is not permitted!");
    }

    public DaemonConsumer registerDaemon(Daemon daemon) {
        daemon.setConsumer(this);
        return this;
    }

    @Override
    public List<DaemonState> getEnginesState() {
        List<DaemonState> ret = new ArrayList<>(1);
        ret.add(getState());
        return ret;
    }

    @Override
    public DaemonConsumer clear() {
        closureLock.lock();
        closureQueue.clear();
        closureLock.unlock();
        return null;
    }

    @Override
    public Consumer getConsumer() {
        return this;
    }

    @Override
    public DaemonConsumer setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandler = handler;
        looperThread.setUncaughtExceptionHandler(handler);
        return this;
    }
}
