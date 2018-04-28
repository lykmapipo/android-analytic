package com.github.lykmapipo.analytic;

import java.util.TimeZone;

/**
 * Utils
 * <p>
 * Common utilities
 * </p>
 */
public final class Utils {
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
}
