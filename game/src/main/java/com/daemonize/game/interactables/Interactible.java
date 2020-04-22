package com.daemonize.game.interactables;

import com.daemonize.graphics2d.scene.views.ImageView;

public interface Interactible {
    void interact();
    ImageView getView();
}