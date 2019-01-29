package com.daemonize.game.renderer;

import com.daemonize.game.scene.Scene2D;
import com.daemonize.daemonengine.consumer.Consumer;

public interface Renderer2D<T extends Renderer2D> extends Consumer {
    T start();
    T stop();
    Scene2D getScene();
    T setScene(Scene2D scene);
    T setDirty();
    T drawScene();
}
