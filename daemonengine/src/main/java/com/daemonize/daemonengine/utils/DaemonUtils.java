package com.daemonize.daemonengine.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class DaemonUtils {

    @FunctionalInterface
    public static interface IntervalRegulator {
        long getSleepInterval();
    }

    public static String tag() {
        return Thread.currentThread().getName() + ", Thread ID: " + Thread.currentThread().getId() + " - ";
    }

    public static String timedTag() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss:SSS");
        return dateFormat.format(date) +  " " + tag();
    }

    public static String convertNanoTimeUnitsToString(long nanoTime, TimeUnits units) {

        Double nano = convertNanoTimeUnits(nanoTime, units);
        String execTimeString;

        switch (units) {
            case NANOSECONDS:
                execTimeString = nano.toString() + " nanoseconds";
                break;
            case MICROSECONDS:
                execTimeString  = Double.toString(nano / 1000) + " microseconds";
                break;
            case MILLISECONDS:
                execTimeString  = Double.toString(nano / 1000000) + " milliseconds";
                break;
            case SECONDS:
                execTimeString  = Double.toString(nano / 1000000000) + " seconds";
                break;
            default:
                execTimeString = "INVALID";
                break;
        }

        return execTimeString;
    }


    public static double convertNanoTimeUnits(long nanoTime, TimeUnits units) {

        double decimalInterval = (double) nanoTime;
        double ret;

        switch (units) {
            case NANOSECONDS:
                ret = decimalInterval;
                break;
            case MICROSECONDS:
                ret  = decimalInterval / 1000;
                break;
            case MILLISECONDS:
                ret  = decimalInterval / 1000000;
                break;
            case SECONDS:
                ret  = decimalInterval / 1000000000;
                break;
            default:
                ret = -1D;
                break;
        }

        return ret;
    }

}
