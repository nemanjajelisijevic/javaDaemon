package com.daemonize.daemonprocessor;


public @interface Daemonize {
    boolean eager() default false;
}
