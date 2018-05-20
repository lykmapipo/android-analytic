package com.github.lykmapipo.analytic;

import android.util.Log;

import java.util.TimeZone;

/**
 * Utils
 * <p>
 * Common utilities
 * </p>
 */
public class Utils {
    /**
     * Derive current device timezone
     *
     * @return
     */
    public static synchronized String getTimezone() {
        String timezone = "";
        try {
            TimeZone timeZone = TimeZone.getDefault();
            timezone = timeZone.getID();
        } catch (Exception e) {
            timezone = "";
        }

        return timezone;
    }

    /**
     * Check if provided value is empty
     *
     * @param string
     * @return
     */
    public static synchronized boolean isEmpty(String string) {
        boolean isEmpty = (string == null || string.isEmpty());
        return isEmpty;
    }

    /**
     * log debug message
     *
     * @param tag
     * @param msg
     */
    public static synchronized void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, isEmpty(msg) ? "" : msg);
        }
    }

    /**
     * log error message
     *
     * @param tag
     * @param msg
     */
    public static synchronized void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, isEmpty(msg) ? "" : msg);
        }
    }

    /**
     * log error message
     *
     * @param tag
     * @param msg
     */
    public static synchronized void e(String tag, String msg, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, e);
        }
    }
}
