package com.daemonize.daemonengine.utils;


public class DaemonUtils {

    public static String tag() {
        return Thread.currentThread().getName() + ", Thread ID: " + Thread.currentThread().getId() + " - ";
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
