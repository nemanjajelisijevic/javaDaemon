package com.daemonize.daemondevapp.scene.view;

public interface ClickableImageView extends ImageView {
    <K extends ClickableImageView> K onClick(Runnable onclick);
}
