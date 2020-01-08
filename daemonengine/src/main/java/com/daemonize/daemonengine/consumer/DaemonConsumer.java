package com.daemonize.daemonengine.consumer;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.Pausable;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DaemonConsumer implements Consumer<DaemonConsumer>, Daemon<DaemonConsumer>, Pausable {

    private volatile String name;
    private volatile DaemonState state = DaemonState.STOPPED;

    private final Queue<Runnable> closureQueue;
    private final Lock closureLock = new ReentrantLock();
    private final Condition closureAvailable = closureLock.newCondition();

    private Thread looperThread;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Runnable closureRunnable;

    private DaemonSemaphore pauseSemaphore = new DaemonSemaphore();

    public DaemonConsumer(String name)
    {
        this(name, new LinkedList<Runnable>());
    }

    public DaemonConsumer(String name, Queue<Runnable> queueImpl) {
        this.name = name;
        this.closureQueue = queueImpl;
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

        while (!state.equals(DaemonState.GONE_DAEMON)) {

            try {
                pauseSemaphore.await();
                closureLock.lock();
                while (closureQueue.isEmpty()) {
                    state = DaemonState.IDLE;
                    closureAvailable.await();
                }
                closureRunnable = closureQueue.poll();
            } catch (InterruptedException ex) {
                System.out.println(DaemonUtils.tag() + name + " interrupted!");
                break;//TODO check this break
            } finally {
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
    public int closureQueueSize() {
        return closureQueue.size();
    }

    @Override
    public void pause() {
        pauseSemaphore.stop();
    }

    @Override
    public void cont() {
        pauseSemaphore.go();
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
