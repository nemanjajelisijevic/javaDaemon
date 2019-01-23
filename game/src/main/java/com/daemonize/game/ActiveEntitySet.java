package com.daemonize.game;

//import android.util.Log;

import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActiveEntitySet<T> {

    private Set<T> activeEntities;

    private Runnable onDepleted;
    private Runnable onFirstAdded;

    public ActiveEntitySet<T> setOnDepleted(Runnable onDepleted) {
        this.onDepleted = onDepleted;
        return this;
    }

    public ActiveEntitySet<T> setOnFirstAdded(Runnable onFirstAdded) {
        this.onFirstAdded = onFirstAdded;
        return this;
    }

    public ActiveEntitySet() {
        this.activeEntities = new HashSet<>();
    }

    public boolean contains(T entity) {
        return activeEntities.contains(entity);
    }

    public boolean remove(T entity) {

        boolean ret = activeEntities.remove(entity);

        //Log.w(DaemonUtils.tag(), "Removing enemy result: " + ret + ", SET SIZE: " + activeEntities.size());

        if (ret && activeEntities.isEmpty()) {
            //Log.w(DaemonUtils.tag(), "SET EMPTY, RUNNING onDepleted()");
            onDepleted.run();
        }

        return ret;
    }

    public boolean add(T entity) {

        boolean wasEmpty = activeEntities.isEmpty();
        boolean added = activeEntities.add(entity);
        //Log.w(DaemonUtils.tag(), "Adding enemy, SET SIZE: " + activeEntities.size());

        if (wasEmpty && added) {
            //Log.w(DaemonUtils.tag(), "SET NOT EMPTY ANYMORE, SIZE: " + activeEntities.size() +  ", RUNNING onDepleted()");
            onFirstAdded.run();
            return true;
        }
        return added;
    }

    public List<T> asList() {
        return new ArrayList<>(activeEntities);
    }

    public int size() {
        return activeEntities.size();
    }
}
