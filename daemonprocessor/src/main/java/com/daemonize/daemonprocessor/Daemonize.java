package com.daemonize.daemonprocessor;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Daemonize {
    boolean eager() default false;
    boolean returnDaemonInstance() default false;
    String daemonName() default "";
}
