package com.daemonize.daemonengine.utils;


public class DaemonUtils {

    public static String tag() {
        return Thread.currentThread().getName() + ", Thread ID: " + Thread.currentThread().getId();
    }

    public static String convertNanoTimeUnits(long interval, TimeUnits units) {

        Double decimalInterval = (double) interval;
        String execTimeString;

        switch (units) {
            case NANOSECONDS:
                execTimeString = decimalInterval.toString() + " nanoseconds.";
                break;
            case MICROSECONDS:
                execTimeString  = Double.toString(decimalInterval / 1000) + " microseconds.";
                break;
            case MILLISECONDS:
                execTimeString  = Double.toString(decimalInterval / 1000000) + " milliseconds.";
                break;
            case SECONDS:
                execTimeString  = Double.toString(decimalInterval / 1000000000) + " seconds.";
                break;
            default:
                execTimeString = "INVALID";
                break;
        }

        return execTimeString;
    }

}
