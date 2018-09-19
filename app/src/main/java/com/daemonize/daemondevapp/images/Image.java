package com.daemonize.daemondevapp.images;

public interface Image<T> {
    int getWidth();
    int getHeight();
    T getImageImp();
}
