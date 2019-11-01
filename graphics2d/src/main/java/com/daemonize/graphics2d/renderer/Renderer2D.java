package com.daemonize.graphics2d.renderer;

import com.daemonize.daemonengine.Pausable;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.daemonengine.consumer.Consumer;

public interface Renderer2D<R extends Renderer2D> extends Consumer<R>, Pausable {
    Scene2D getScene();
    R setScene(Scene2D scene);
    R setDirty();
    R drawScene();
}
