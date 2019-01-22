package com.daemonize.daemondevapp.renderer;

import com.daemonize.daemondevapp.scene.Scene2D;
import com.daemonize.daemonengine.consumer.Consumer;

public interface Renderer2D<T extends Renderer2D> extends Consumer {
    T start();
    T stop();
    Scene2D getScene();
    T setScene(Scene2D scene);
    void setDirty();
}
