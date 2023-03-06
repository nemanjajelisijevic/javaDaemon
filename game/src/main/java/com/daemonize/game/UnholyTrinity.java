package com.daemonize.game;

import com.daemonize.daemonengine.Daemon;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;

import java.util.Map;
import java.util.TreeMap;

public class UnholyTrinity<D extends Daemon> {

    private D daemon;
    private Map<String, Image[]> spriteMap;
    private Map<String, ImageView> viewMap;

    public UnholyTrinity() {
        this.spriteMap = new TreeMap<>();
        this.viewMap = new TreeMap<>();
    }

    public UnholyTrinity<D> setDaemon(D daemon) {
        this.daemon = daemon;
        return this;
    }

    public D getDaemon() {
        return daemon;
    }

    public UnholyTrinity<D> addSprite(String key, Image[] sprite) {
        spriteMap.put(key, sprite);
        return this;
    }

    public UnholyTrinity<D> removeSprite(String key) {
        spriteMap.remove(key);
        return this;
    }

    public Image[] getSprite(String key) {
        return spriteMap.get(key);
    }

    public UnholyTrinity<D> addView(String key, ImageView view) {
        viewMap.put(key, view);
        return this;
    }

    public UnholyTrinity<D> removeView(String key) {
        viewMap.remove(key);
        return this;
    }

    public ImageView getView(String key) {
        return viewMap.get(key);
    }
}
