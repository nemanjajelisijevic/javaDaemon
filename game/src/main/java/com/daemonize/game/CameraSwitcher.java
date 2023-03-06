package com.daemonize.game;

import com.daemonize.graphics2d.camera.Camera2D;

public interface CameraSwitcher<T extends CameraSwitcher> {
    T setCamera(Camera2D camera);
    void switchCameras();
}
