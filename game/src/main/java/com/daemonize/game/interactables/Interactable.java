package com.daemonize.game.interactables;

import com.daemonize.graphics2d.scene.views.ImageView;

public interface Interactable {
    void interact();
    ImageView getView();
}