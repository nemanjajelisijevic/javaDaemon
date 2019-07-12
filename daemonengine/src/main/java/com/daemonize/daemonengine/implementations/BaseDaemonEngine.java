package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDaemonEngine<D extends BaseDaemonEngine> implements Daemon<D> {

    protected volatile DaemonState state = DaemonState.STOPPED;
    private Consumer consumer;
    private String name = this.getClass().getSimpleName();

    protected Thread daemonThread;
    protected Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private DaemonSemaphore startStopSemaphore = new DaemonSemaphore().setName("Start/Stop Daemon Semaphore");

    public D setName(String name) {
      this.name = name;
      return (D) this;
    }

    @Override
    public String getName() {
      return this.name;
    }

    @Override
    public D setConsumer(Consumer consumer) {
      this.consumer = consumer;
      return (D) this;
    }

    @Override
    public Consumer getConsumer() {
      return consumer;
    }

    protected BaseDaemonEngine(){}

    protected BaseDaemonEngine(Consumer consumer) {
      this.consumer = consumer;
    }

    protected void setState(DaemonState state) {
      this.state = state;
    }

    public DaemonState getState() {
      return state;
    }

    protected abstract BaseQuest getQuest();

    protected void loop(){

        System.out.println(DaemonUtils.tag() + "Daemon engine started!");

        BaseQuest currentQuest;

        while (!state.equals(DaemonState.GONE_DAEMON)) {

            currentQuest = getQuest();

            if (currentQuest == null) {
                if (state.equals(DaemonState.IDLE))//TODO check dis
                    continue;
              break;
            }

            if (!currentQuest.getIsVoid() && currentQuest.getReturnRunnable() == null)
              break;

            runQuest(currentQuest);
        }

        System.out.println(DaemonUtils.tag() + "Daemon engine stopped!");
        setState(DaemonState.STOPPED);
        startStopSemaphore.go();
    }

    protected void runQuest(BaseQuest quest) {
        setState(quest.getState());
        quest.run();
    };

    @Override
    public synchronized D start() {

        try {
            startStopSemaphore.await();
        } catch (InterruptedException e) {
            //
        }

        DaemonState initState = getState();
        if (initState.equals(DaemonState.STOPPED))
            initThread();

        return (D) this;
    }

    protected void initThread() {
        daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        });
        daemonThread.setName(name);
        setState(DaemonState.INITIALIZING);
        if (uncaughtExceptionHandler != null)
            daemonThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        daemonThread.start();
    }

    @Override
    public synchronized void stop() {
      if (state != DaemonState.STOPPED) {
        state = DaemonState.GONE_DAEMON;
        if (daemonThread != null
                && !Thread.currentThread().equals(daemonThread)//TODO check if possible to stopDaemon from daemon thread
                && daemonThread.isAlive()) {
            daemonThread.interrupt();
        }
      }

      startStopSemaphore.stop();
      daemonThread = null;//TODO     check this nulling
    }

    @Override
    public D queueStop() {
      throw new IllegalStateException("This method can only be called from MainQuestDaemonBaseEngine");
    }

    @Override
    public List<DaemonState> getEnginesState() {
        List<DaemonState> ret = new ArrayList<>(1);
        ret.add(getState());
        return ret;
    }

    @Override
    public D setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandler = handler;
        if (daemonThread != null)
            daemonThread.setUncaughtExceptionHandler(handler);
        return (D) this;
    }
}
