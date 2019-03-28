package com.daemonize.game.scene;

import com.daemonize.game.scene.views.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


public class Scene2D {

    @FunctionalInterface
    public interface Action{
        void execute(ImageView entity);
    }

    private volatile List<ImageView> views = new ArrayList<>();
    private volatile boolean locked = false;

    private Hashtable<String, ImageView> viewNameTable = new Hashtable<>();

    public Scene2D lockViews() {
        Collections.sort(views);
        this.locked = true;
        return this;
    }

    public Scene2D unlockViews() {
        this.locked = false;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public ImageView addImageView(ImageView view) {
        if (locked)
            throw new IllegalStateException("Scene locked. Can not add view!");
        views.add(view);
        viewNameTable.put(view.getName(), view);
        return view;
    }

    public void addImageViews(List<ImageView> listOfViews) {
        if (locked)
            throw new IllegalStateException("Scene locked. Can not add view!");
        views.addAll(listOfViews);
    }

    public List<ImageView> getViews() {
        return views;
    }

    public ImageView getViewByName(String name) {
        return viewNameTable.get(name);
    }

    public int viewsSize() {
        return views.size();
    }

    public Scene2D forEach(Action action) {
        for (ImageView view : views) {
            action.execute(view);
        }
        return this;
    }

}
