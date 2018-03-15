package com.daemonize.daemonprocessor;


public @interface LogExecutionTime {
    TimeUnits timeUnits() default TimeUnits.MILLISECONDS;
    String daemonName() default "";
}
