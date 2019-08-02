package com.daemonize.graphics2d.scene.views;

public interface ClickableImageView<K extends ClickableImageView> extends ImageView {
    K onClick(Runnable onclick);
    K disable();
    K enable();
}
