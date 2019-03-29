package com.daemonize.game.scene.views;

public interface ClickableImageView<K extends ClickableImageView> extends ImageView {
    K onClick(Runnable onclick);
}
