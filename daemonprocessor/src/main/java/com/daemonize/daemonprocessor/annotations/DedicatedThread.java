package com.daemonize.daemonprocessor.annotations;

public @interface DedicatedThread {
    boolean consumerArg() default false;
}
