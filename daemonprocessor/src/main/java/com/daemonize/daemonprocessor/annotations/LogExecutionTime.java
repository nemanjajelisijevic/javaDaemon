package com.daemonize.daemonprocessor.annotations;


import com.daemonize.daemonprocessor.TimeUnits;

public @interface LogExecutionTime {
    TimeUnits timeUnits() default TimeUnits.MILLISECONDS;
    String daemonName() default "";
}
