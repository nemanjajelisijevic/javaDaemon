package com.daemonize.graphics2d.renderer;

import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.daemonengine.consumer.Consumer;

public interface Renderer2D<T extends Renderer2D> extends Consumer<T> {
    Scene2D getScene();
    T setScene(Scene2D scene);
    T setDirty();
    T drawScene();
}
