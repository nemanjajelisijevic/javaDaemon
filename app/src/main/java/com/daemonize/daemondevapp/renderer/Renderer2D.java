package com.daemonize.daemondevapp.renderer;

import com.daemonize.daemondevapp.scene.Scene2D;

public interface Renderer2D {


    <K extends Renderer2D> K start();
    <K extends Renderer2D> K stop();

    Scene2D getScene();
    <K extends Renderer2D> K setScene(Scene2D scene);
}
