package com.github.lykmapipo.analytic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Analytic
 * <p>
 * Simplified wrapper for {@link com.google.firebase.analytics.FirebaseAnalytics} to provide
 * utility helpers
 * </p>
 *
 * @author lally elias<lallyelias87@gmail.com>
 * @version 0.1.0
 * @since 0.1.0
 */
public final class Analytic {
    /**
     * {@link FirebaseAnalytics} instance
     */
    private static FirebaseAnalytics analytics;

    /**
     * Initialize analytic
     *
     * @param context {@link Context}
     * @return {@link FirebaseAnalytics}
     */
    @RequiresPermission(
            allOf = {
                    "android.permission.INTERNET",
                    "android.permission.ACCESS_NETWORK_STATE",
                    "android.permission.WAKE_LOCK"
            }
    )
    public static synchronized FirebaseAnalytics initialize(@NonNull Context context) {
        if (analytics == null) {
            analytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
        }
        return analytics;
    }

    /**
     * Obtain current instance of {@link FirebaseAnalytics}
     *
     * @return {@link FirebaseAnalytics}
     */
    @Nullable
    public static synchronized FirebaseAnalytics getInstance() {
        return analytics;
    }
}
