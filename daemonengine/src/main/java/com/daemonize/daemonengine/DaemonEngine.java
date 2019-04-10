package com.daemonize.daemonengine;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.quests.Quest;
import com.daemonize.daemonengine.quests.VoidQuest;

public interface DaemonEngine<D extends DaemonEngine> extends Daemon<D> {

    <T> D daemonize(Quest<T> quest, Closure<T> closure);

    <T> D daemonize(Consumer consumer, Quest<T> quest, Closure<T> closure);

    D daemonize(Consumer consumer, final VoidQuest quest, Runnable closure);

    D daemonize(final VoidQuest quest, Runnable closure);

    D daemonize(final VoidQuest quest);
}
