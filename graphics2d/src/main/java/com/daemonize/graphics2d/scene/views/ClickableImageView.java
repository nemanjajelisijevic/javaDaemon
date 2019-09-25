package com.daemonize.graphics2d.scene.views;

public interface ClickableImageView<V extends ClickableImageView> extends ImageView {
    V onClick(Runnable onclick);
    V disable();
    V enable();
}
