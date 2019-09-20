package com.daemonize.daemonprocessor.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Daemon {
    boolean eager() default false;
    boolean doubleDaemonize() default false;
    String className() default "";
    boolean consumer() default false;
    boolean daemonizeBaseMethods() default true;
    boolean markDaemonMethods() default false;
    boolean implementPrototypeInterfaces() default false;
}
