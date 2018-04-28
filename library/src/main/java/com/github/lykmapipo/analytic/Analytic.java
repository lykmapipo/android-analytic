package com.github.lykmapipo.analytic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

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

    /**
     * Logs an app event. Events with the same name must have the same parameters.
     *
     * @param eventName   The name of the event
     * @param eventParams The map of event parameters
     * @see FirebaseAnalytics#logEvent(String, Bundle)
     */
    public static synchronized void track(@NonNull String eventName, @Nullable Bundle eventParams) {
        //ensure analytic and event name
        boolean canTrack = (analytics != null && !TextUtils.isEmpty(eventName));

        if (canTrack) {
            analytics.logEvent(eventName, eventParams);
        }

        //TODO log error on debug
    }

    /**
     * Logs an app event. Events with the same name must have the same parameters.
     *
     * @param event The event to track
     * @see FirebaseAnalytics#logEvent(String, Bundle)
     */
    public static synchronized void track(@NonNull Trackable event) {

        //prepare event name
        String eventName = event.getEventName();

        //prepare event params
        Bundle eventParams = new Bundle();
        eventParams.putAll(event.getEventParams());

        //track event
        track(eventName, eventParams);

    }
}
