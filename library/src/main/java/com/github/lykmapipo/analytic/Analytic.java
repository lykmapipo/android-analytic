package com.github.lykmapipo.analytic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

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
     * Analytic parameters
     */
    public static final String PARAM_TIMEZONE = "timezone";
    public static final String PARAM_TIME = "time";
    //TODO add other default parameters
    //TODO support event name prefix
    //TODO support event param name prefix

    /**
     * {@link FirebaseAnalytics} instance
     */
    private static FirebaseAnalytics analytics;

    /**
     * Default event parameter
     */
    private static Bundle defaultEventParams = new Bundle();

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

        //instantiate if not exist
        if (analytics == null) {
            analytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
        }

        //set default event parameters
        defaultEventParams.putString(PARAM_TIMEZONE, Utils.getTimezone());

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
     * Derive default analytic params
     *
     * @return {@link Bundle}
     */
    public static synchronized Bundle getDefaultEventParams() {
        Bundle params = new Bundle();
        params.putAll(defaultEventParams);

        params.putLong(PARAM_TIME, new Date().getTime());

        return params;
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

            //prepare event parameters
            Bundle params = getDefaultEventParams();
            if (eventParams != null) {
                params.putAll(eventParams);
            }

            //send event to firabase analytics
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
    public static synchronized void track(@NonNull Event event) {
        //prepare event data
        Bundle params = new Bundle();

        //set event time
        Date eventTime = event.getTime();
        if (eventTime != null) {
            params.putLong(PARAM_TIME, eventTime.getTime());
        }

        //obtain event params
        Bundle eventParams = event.getParams();
        if (eventParams != null) {
            params.putAll(eventParams);
        }

        //track event
        track(event.getName(), params);

    }


    /**
     * Track app events
     */
    public static class App {
        /**
         * Logs an app open event. Events with the same name must have the same parameters.
         *
         * @param params Additional parameter to track with event
         * @see Analytic#track(String, Bundle)
         * @see FirebaseAnalytics.Event#APP_OPEN
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#APP_OPEN">APP_OPEN</a>
         */
        public static synchronized void opened(@Nullable Bundle params) {

            String eventName = FirebaseAnalytics.Event.APP_OPEN;

            track(eventName, params);
        }

        /**
         * Logs an app open event. Events with the same name must have the same parameters.
         *
         * @see Analytic.App#opened(Bundle)
         * @see Analytic#track(String, Bundle)
         * @see FirebaseAnalytics.Event#APP_OPEN
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#APP_OPEN">APP_OPEN</a>
         */
        public static synchronized void opened() {
            Bundle params = new Bundle();
            opened(params);
        }
    }
}
