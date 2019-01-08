package com.daemonize.daemondevapp.view;

public interface ClickableImageView extends ImageView {
    <K extends ClickableImageView> K onClick(Runnable onclick);
}
