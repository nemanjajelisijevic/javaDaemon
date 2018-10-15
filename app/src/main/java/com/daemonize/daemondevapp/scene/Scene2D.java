package com.daemonize.daemondevapp.scene;

import com.daemonize.daemondevapp.view.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Scene2D {

    private List<ImageView> views = new ArrayList<>();
    private volatile boolean locked = false;

    public Scene2D lockViews() {
        this.locked = true;
        return this;
    }

    public boolean isLocked() {
        return locked;
    }

    public ImageView addImageView(ImageView view) {
        if (locked)
            throw new IllegalStateException("Scene locked. Can not add view!");
        views.add(view);
        return view;
    }

    public List<ImageView> getViews() {
        return views;
    }

    public int viewsSize() {
        return views.size();
    }

}
