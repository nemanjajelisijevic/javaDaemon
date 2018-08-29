package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.images.Image;

public interface DaemonView {

    <K extends DaemonView> K setX(float x);
    <K extends DaemonView> K setY(float y);
    <K extends DaemonView> K setImage(Image image);
    <K extends DaemonView> K hide();
    <K extends DaemonView> K show();

}
