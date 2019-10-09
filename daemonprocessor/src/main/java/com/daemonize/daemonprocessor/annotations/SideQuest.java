package com.daemonize.daemonprocessor.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface SideQuest {
    long SLEEP() default 0;
    boolean interruptible() default false;
    boolean blockingClosure() default false;
}
