package com.daemonize.daemonprocessor.annotations;


import com.daemonize.daemonprocessor.Platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Daemonize {
    boolean eager() default false;
    boolean returnDaemonInstance() default false;
    String className() default "";
    Platform platform() default Platform.ANDROID;
}
