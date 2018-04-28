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
    //TODO track if user signup
    //TODO track if user login
    //TODO track android(medium/channel/device)
    //TODO add default signin/login method
    //TODO make use of trackable

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
         * Logs an app open event
         * <p>
         * By logging this event when an App is moved to the foreground,
         * developers can understand how often users leave and return during the course of
         * a Session. Although Sessions are automatically reported, this event can
         * provide further clarification around the continuous engagement of app-users.
         * </p>
         *
         * @param params Additional parameter to track with event
         * @see FirebaseAnalytics.Event#APP_OPEN
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#APP_OPEN">APP_OPEN</a>
         */
        public static synchronized void opened(@Nullable Bundle params) {

            String eventName = FirebaseAnalytics.Event.APP_OPEN;

            track(eventName, params);
        }

        /**
         * Logs an app open event
         * <p>
         * By logging this event when an App is moved to the foreground,
         * developers can understand how often users leave and return during the course of
         * a Session. Although Sessions are automatically reported, this event can
         * provide further clarification around the continuous engagement of app-users.
         * </p>
         *
         * @see Analytic.App#opened(Bundle)
         * @see FirebaseAnalytics.Event#APP_OPEN
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#APP_OPEN">APP_OPEN</a>
         */
        public static synchronized void opened() {
            Bundle params = new Bundle();
            opened(params);
        }

        /**
         * Logs user logged in event
         *
         * @see FirebaseAnalytics.Event#LOGIN
         * @see FirebaseAnalytics.Param#METHOD
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#LOGIN">LOGIN</a>
         */
        public static synchronized void loggedIn(@NonNull String method, @Nullable Bundle params) {

            if (!TextUtils.isEmpty(method)) {

                String eventName = FirebaseAnalytics.Event.LOGIN;

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, method);
                if (params != null) {
                    bundle.putAll(params);
                }

                track(eventName, bundle);
            }
        }

        /**
         * Logs user logged in event
         *
         * @see Analytic.App#loggedIn(String, Bundle)
         * @see FirebaseAnalytics.Event#LOGIN
         * @see FirebaseAnalytics.Param#METHOD
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#LOGIN">LOGIN</a>
         */
        public static synchronized void loggedIn(@NonNull String method) {
            Bundle params = new Bundle();
            loggedIn(method, params);
        }


        /**
         * Logs user signed up event
         * <p>
         * This event indicates that a user has signed up for an account in your app.
         * The parameter signifies the method by which the user signed up.
         * Use this event to understand the different behaviors between logged in and
         * logged out users
         * </p>
         *
         * @see FirebaseAnalytics.Event#SIGN_UP
         * @see FirebaseAnalytics.Param#METHOD
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#SIGN_UP">SIGN_UP</a>
         */
        public static synchronized void signedUp(@NonNull String method, @Nullable Bundle params) {

            if (!TextUtils.isEmpty(method)) {

                String eventName = FirebaseAnalytics.Event.SIGN_UP;

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, method);
                if (params != null) {
                    bundle.putAll(params);
                }

                track(eventName, bundle);
            }
        }


        /**
         * Logs user signed up event
         * <p>
         * This event indicates that a user has signed up for an account in your app.
         * The parameter signifies the method by which the user signed up.
         * Use this event to understand the different behaviors between logged in and
         * logged out users
         * </p>
         *
         * @see Analytic.App#signedUp(String, Bundle)
         * @see FirebaseAnalytics.Event#SIGN_UP
         * @see FirebaseAnalytics.Param#METHOD
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#SIGN_UP">SIGN_UP</a>
         */
        public static synchronized void signedUp(@NonNull String method) {
            Bundle params = new Bundle();
            signedUp(method, params);
        }
    }


    /**
     * Track tutorial events
     */
    public static class Tutorial {
        /**
         * Logs tutorial begin event
         * <p>
         * This event signifies the start of the on-boarding process in your app.
         * Use this in a funnel with {@link FirebaseAnalytics.Event#TUTORIAL_COMPLETE} to understand
         * how many users complete this process and move on to the full app experience.
         * </p>
         *
         * @param params Additional parameter to track with event
         * @see FirebaseAnalytics.Event#TUTORIAL_BEGIN
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#TUTORIAL_BEGIN">TUTORIAL_BEGIN</a>
         */
        public static synchronized void begin(@Nullable Bundle params) {

            String eventName = FirebaseAnalytics.Event.TUTORIAL_BEGIN;

            track(eventName, params);
        }

        /**
         * Logs tutorial begin event
         * <p>
         * This event signifies the start of the on-boarding process in your app.
         * Use this in a funnel with {@link FirebaseAnalytics.Event#TUTORIAL_COMPLETE} to understand
         * how many users complete this process and move on to the full app experience.
         * </p>
         *
         * @see Analytic.Tutorial#begin(Bundle)
         * @see FirebaseAnalytics.Event#TUTORIAL_BEGIN
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#TUTORIAL_BEGIN">TUTORIAL_BEGIN</a>
         */
        public static synchronized void begin() {
            Bundle params = new Bundle();
            begin(params);
        }

        /**
         * Logs tutorial complete event
         * <p>
         * Use this event to signify the user's completion of your app's on-boarding process.
         * Add this to a funnel with {@link FirebaseAnalytics.Event#TUTORIAL_BEGIN} to gauge
         * the completion rate of your on-boarding process.
         * </p>
         *
         * @param params Additional parameter to track with event
         * @see FirebaseAnalytics.Event#TUTORIAL_COMPLETE
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#TUTORIAL_COMPLETE">TUTORIAL_COMPLETE</a>
         */
        public static synchronized void complete(@Nullable Bundle params) {

            String eventName = FirebaseAnalytics.Event.TUTORIAL_COMPLETE;

            track(eventName, params);
        }

        /**
         * Logs tutorrial complete event
         * <p>
         * Use this event to signify the user's completion of your app's on-boarding process.
         * Add this to a funnel with {@link FirebaseAnalytics.Event#TUTORIAL_BEGIN} to gauge
         * the completion rate of your on-boarding process.
         * </p>
         *
         * @see Analytic.Tutorial#complete(Bundle)
         * @see FirebaseAnalytics.Event#TUTORIAL_COMPLETE
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#TUTORIAL_COMPLETE">TUTORIAL_COMPLETE</a>
         */
        public static synchronized void complete() {
            Bundle params = new Bundle();
            complete(params);
        }
    }
}
