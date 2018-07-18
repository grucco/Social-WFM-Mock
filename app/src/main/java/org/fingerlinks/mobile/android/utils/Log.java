package org.fingerlinks.mobile.android.utils;

/**
 * Created by Raphael on 10/10/2015.
 */
public class Log {

    private static boolean showLog;

    public static void setShowLog(boolean showLog) {
        Log.showLog = showLog;
    }

    public static void d(Class clazz, String message) {
        printLog(android.util.Log.INFO, clazz.getName(), message, null);
    }

    public static void e(Class clazz, Throwable e) {
        printLog(android.util.Log.ERROR, clazz.getName(), null, e);
    }

    public static void e(Class clazz, String message) {
        printLog(android.util.Log.ERROR, clazz.getName(), message, null);
    }

    public static void v(Class clazz, String message) {
        printLog(android.util.Log.VERBOSE, clazz.getName(), message, null);
    }

    public static void i(Class clazz, String message) {
        printLog(android.util.Log.INFO, clazz.getName(), message, null);
    }

    public static void w(Class clazz, String message) {
        printLog(android.util.Log.WARN, clazz.getName(), message, null);
    }

    public static void d(String clazz, String message) {
        printLog(android.util.Log.INFO, clazz, message, null);
    }

    public static void e(String clazz, Throwable e) {
        printLog(android.util.Log.ERROR, clazz, null, e);
    }

    public static void e(String clazz, String message) {
        printLog(android.util.Log.ERROR, clazz, message, null);
    }

    public static void v(String clazz, String message) {
        printLog(android.util.Log.VERBOSE, clazz, message, null);
    }

    public static void i(String clazz, String message) {
        printLog(android.util.Log.INFO, clazz, message, null);
    }

    public static void w(String clazz, String message) {
        printLog(android.util.Log.WARN, clazz, message, null);
    }

    private static void printLog(int type, String tag, String message, Throwable e) {
        //if (showLog) {
            switch(type) {
                case android.util.Log.INFO:
                    android.util.Log.i(tag, message);
                    break;
                case android.util.Log.VERBOSE:
                    android.util.Log.v(tag, message);
                    break;
                case android.util.Log.ERROR:
                    if(e == null) {
                        android.util.Log.e(tag, message);
                    } else {
                        if(message == null) {
                            message = e.getLocalizedMessage();
                        }
                        android.util.Log.e(tag, message, e);
                    }
                    break;
                case android.util.Log.DEBUG:
                    android.util.Log.d(tag, message);
                    break;

                case android.util.Log.WARN:
                    android.util.Log.w(tag, message);
                    break;
            }
        //}
    }

};