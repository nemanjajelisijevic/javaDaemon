package com.daemonize.daemonprocessor.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Daemonize {
    boolean eager() default false;
    boolean doubleDaemonize() default false;
    String className() default "";
    boolean consumer() default false;
    boolean daemonizeBaseMethods() default false;
}
