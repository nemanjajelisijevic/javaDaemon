package com.daemonize.daemonengine.utils;


public class DaemonUtils {

    public static String tag() {
        return Thread.currentThread().getName() + ", Thread ID: " + Thread.currentThread().getId();
    }

}
