package com.daemonize.game.scene.views;

public interface ClickableImageView extends ImageView {
    <K extends ClickableImageView> K onClick(Runnable onclick);
}
