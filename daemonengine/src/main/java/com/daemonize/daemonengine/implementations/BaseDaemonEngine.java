package com.daemonize.daemonengine.implementations;


import com.daemonize.daemonengine.Daemon;
import com.daemonize.daemonengine.DaemonState;
import com.daemonize.daemonengine.closure.ClosureExecutionWaiter;
import com.daemonize.daemonengine.closure.SemaphoreClosureExecutionWaiter;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.BaseQuest;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.daemonize.daemonengine.DaemonState.GONE_DAEMON;
import static com.daemonize.daemonengine.DaemonState.STOPPED;

public abstract class BaseDaemonEngine<D extends BaseDaemonEngine> implements Daemon<D> {

    protected volatile DaemonState daemonState = STOPPED;
    protected Consumer consumer;
    private String name = this.getClass().getSimpleName();

    protected Thread daemonThread;
    protected Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private BaseQuest.DaemonStateSetter stateSetter = new BaseQuest.DaemonStateSetter() {
        @Override
        public void setState(DaemonState state) {
            if (!daemonState.equals(STOPPED) && !daemonState.equals(GONE_DAEMON))
                setDaemonState(state);
        }
    };

    private SemaphoreClosureExecutionWaiter closureAwaiter = new SemaphoreClosureExecutionWaiter().setName(name + " closureAwaiter");

    public ClosureExecutionWaiter getClosureAwaiter() {
        return closureAwaiter;
    }

    public D setName(String name) {
      this.name = name;
      this.closureAwaiter.setName(name + " closureAwaiter");
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

    protected void setDaemonState(DaemonState state) {
      this.daemonState = state;
    }

    public DaemonState getState() {
      return daemonState;
    }

    protected abstract BaseQuest getQuest();

    protected void loop(){

        //System.out.println(DaemonUtils.tag() + "Daemon engine started!");

        BaseQuest currentQuest;

        while (!daemonState.equals(DaemonState.GONE_DAEMON)) {

            currentQuest = getQuest();
            if (currentQuest == null) {
                if (daemonState.equals(DaemonState.IDLE))//TODO check dis
                    continue;
              break;
            }
            currentQuest.setDaemonStateSetter(stateSetter);
            runQuest(currentQuest);
        }

        cleanUp();
        //System.out.println(DaemonUtils.tag() + "Daemon engine stopped!");
        setDaemonState(STOPPED);
    }

    protected boolean runQuest(BaseQuest quest) {
        return quest.run();
    };

    @Override
    public synchronized D start() {
        DaemonState initState = getState();
        if (initState.equals(STOPPED) || initState.equals(DaemonState.GONE_DAEMON))
            initThread();
        return (D) this;
    }

    private void initThread() {
        daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        });
        daemonThread.setName(name);
        setDaemonState(DaemonState.INITIALIZING);
        if (uncaughtExceptionHandler != null)
            daemonThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        daemonThread.start();
    }

    protected void cleanUp() {}

    @Override
    public synchronized void stop() {
      if (daemonState != STOPPED) {
        daemonState = DaemonState.GONE_DAEMON;
        if (daemonThread != null
                && !Thread.currentThread().equals(daemonThread)//TODO check if possible to stopDaemon from daemon thread
                && daemonThread.isAlive()) {
            daemonThread.interrupt();
            daemonThread = null;//TODO check this nulling
        }
      }
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
