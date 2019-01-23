package com.daemonize.game.view;

public interface ClickableImageView extends ImageView {
    <K extends ClickableImageView> K onClick(Runnable onclick);
}
