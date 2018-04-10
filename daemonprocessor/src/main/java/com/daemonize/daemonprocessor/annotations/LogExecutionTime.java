package com.daemonize.daemonprocessor.annotations;


import com.daemonize.daemonprocessor.TimeUnits;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface LogExecutionTime {
    TimeUnits timeUnits() default TimeUnits.MILLISECONDS;
    String daemonName() default "";
}
