package com.daemonize.daemonprocessor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface Daemonize {
    boolean dedicatedThread() default false;
    String name() default "";
    boolean consumerArg() default false;
    boolean generateRunnable() default false;
}
