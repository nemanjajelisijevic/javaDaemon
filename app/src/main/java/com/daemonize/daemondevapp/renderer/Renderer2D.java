package com.daemonize.daemondevapp.renderer;

import com.daemonize.daemondevapp.scene.Scene2D;

public interface Renderer2D<T extends Renderer2D>{


    T start();
    T stop();

    Scene2D getScene();
    T setScene(Scene2D scene);
}
