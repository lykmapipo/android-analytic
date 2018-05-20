package com.github.lykmapipo.analytic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

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
public class Analytic {

    public static final String TAG = Analytic.class.getSimpleName();

    /**
     * Analytic parameters & values
     */
    public static final String PARAM_TIMEZONE = "timezone"; //event timezone
    public static final String PARAM_TIME = "time"; // event time
    public static final String PARAM_MEDIUM = "medium"; //event medium(or channel)
    public static final String VALUE_MEDIUM_ANDROID = "android";
    public static final String VALUE_DEFAULT_CURRENCY = "USD";

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

        params.putString(PARAM_TIMEZONE, Utils.getTimezone()); //timezone
        params.putLong(PARAM_TIME, new Date().getTime()); //time
        params.putString(PARAM_MEDIUM, VALUE_MEDIUM_ANDROID);//medium

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
        boolean canTrack = (analytics != null && !Utils.isEmpty(eventName));

        if (canTrack) {

            //prepare event parameters
            Bundle params = getDefaultEventParams();
            if (eventParams != null) {
                params.putAll(eventParams);
            }

            //send event to firebase analytics
            analytics.logEvent(eventName, params);

            //debug
            Utils.d(TAG, params.toString());
        }

        //notify not tracked
        else {
            Utils.d(TAG, "Fail to log event");
        }

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

            if (!Utils.isEmpty(method)) {

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

            if (!Utils.isEmpty(method)) {

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

        /**
         * Logs share event
         *
         * @see FirebaseAnalytics.Event#SHARE
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#SHARE">SHARE</a>
         */
        public static synchronized void share(@NonNull String method, @Nullable Bundle params) {
            //TODO ensure content_type & item_id
            if (!Utils.isEmpty(method)) {

                String eventName = FirebaseAnalytics.Event.SHARE;

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, method);
                if (params != null) {
                    bundle.putAll(params);
                }

                track(eventName, bundle);
            }
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


    /**
     * Track view events
     */
    public static class View {

        //ITEM

        /**
         * View Item event. This event signifies that some content was shown to the user.
         * This content may be a product, a webpage or just a simple image or text.
         * Use the appropriate parameters to contextualize the event.
         * Use this event to discover the most popular items viewed in your app.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM">VIEW_ITEM</a>
         */
        public static synchronized void item(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.VIEW_ITEM;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }

        /**
         * View Item event. This event signifies that some content was shown to the user.
         * This content may be a product, a webpage or just a simple image or text.
         * Use the appropriate parameters to contextualize the event.
         * Use this event to discover the most popular items viewed in your app.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#item(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM">VIEW_ITEM</a>
         */
        public static synchronized void item(@NonNull String id, @NonNull String name, @NonNull String category, @Nullable Bundle params) {

            boolean canTrack =
                    (!Utils.isEmpty(id) && !Utils.isEmpty(name) && !Utils.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                item(bundle);
            }

        }

        /**
         * View Item event. This event signifies that some content was shown to the user.
         * This content may be a product, a webpage or just a simple image or text.
         * Use the appropriate parameters to contextualize the event.
         * Use this event to discover the most popular items viewed in your app.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#item(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM">VIEW_ITEM</a>
         */
        public static synchronized void item(@NonNull String name, @NonNull String category, @Nullable Bundle params) {

            boolean canTrack =
                    (!Utils.isEmpty(name) && !Utils.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                item(bundle);
            }

        }

        /**
         * View Item event. This event signifies that some content was shown to the user.
         * This content may be a product, a webpage or just a simple image or text.
         * Use the appropriate parameters to contextualize the event.
         * Use this event to discover the most popular items viewed in your app.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#item(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM">VIEW_ITEM</a>
         */
        public static synchronized void item(@NonNull String name, @NonNull String category) {

            boolean canTrack =
                    (!Utils.isEmpty(name) && !Utils.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);

                //track
                item(bundle);
            }

        }


        /**
         * View Item event. This event signifies that some content was shown to the user.
         * This content may be a product, a webpage or just a simple image or text.
         * Use the appropriate parameters to contextualize the event.
         * Use this event to discover the most popular items viewed in your app.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#item(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM">VIEW_ITEM</a>
         */
        public static synchronized void item(@NonNull String name, @Nullable Bundle params) {

            boolean canTrack =
                    (!Utils.isEmpty(name));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                item(bundle);
            }

        }

        /**
         * View Item event. This event signifies that some content was shown to the user.
         * This content may be a product, a webpage or just a simple image or text.
         * Use the appropriate parameters to contextualize the event.
         * Use this event to discover the most popular items viewed in your app.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#item(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM">VIEW_ITEM</a>
         */
        public static synchronized void item(@NonNull String name) {

            boolean canTrack =
                    (!Utils.isEmpty(name));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                item(name, bundle);
            }

        }


        //LIST

        /**
         * View Item List event. Log this event when the user has been presented with a list of
         * items of a certain category.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM_LIST
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM_LIST">VIEW_ITEM_LIST</a>
         */
        public static synchronized void list(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.VIEW_ITEM_LIST;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }

        /**
         * View Item List event. Log this event when the user has been presented with a list of
         * items of a certain category.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM_LIST
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#list(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM_LIST">VIEW_ITEM_LIST</a>
         */
        public static synchronized void list(@NonNull String category, @Nullable Bundle params) {

            boolean canTrack =
                    (!Utils.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                list(bundle);
            }

        }

        /**
         * View Item List event. Log this event when the user has been presented with a list of
         * items of a certain category.
         *
         * @see FirebaseAnalytics.Event#VIEW_ITEM_LIST
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.View#list(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#VIEW_ITEM_LIST">VIEW_ITEM_LIST</a>
         */
        public static synchronized void list(@NonNull String category) {

            boolean canTrack =
                    (!Utils.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                list(category, bundle);
            }

        }
    }

    /**
     * Track ecommerce events
     */
    public static class Ecommerce {

        //WISHLIST

        /**
         * E-Commerce Add To Wishlist event.
         * This event signifies that an item was added to a wishlist.
         * Use this event to identify popular gift items in your app.
         *
         * @see FirebaseAnalytics.Event#ADD_TO_WISHLIST
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ADD_TO_WISHLIST">ADD_TO_WISHLIST</a>
         */
        public static synchronized void addToWishList(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.ADD_TO_WISHLIST;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }


        //CHECKOUT

        /**
         * E-Commerce Begin Checkout event.
         * This event signifies that a user has begun the process of checking out.
         *
         * @see FirebaseAnalytics.Event#BEGIN_CHECKOUT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#BEGIN_CHECKOUT">BEGIN_CHECKOUT</a>
         */
        public static synchronized void beginCheckout(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.BEGIN_CHECKOUT;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }

        /**
         * E-Commerce Checkout Progress event.
         *
         * @see FirebaseAnalytics.Event#CHECKOUT_PROGRESS
         * @see FirebaseAnalytics.Param#CHECKOUT_STEP
         * @see FirebaseAnalytics.Param#CHECKOUT_OPTION
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#CHECKOUT_PROGRESS">CHECKOUT_PROGRESS</a>
         */
        public static synchronized void checkoutProgress(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.CHECKOUT_PROGRESS;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }


        //PURCHASE


        /**
         * E-Commerce Purchase event.
         * This event signifies that an item was purchased by a user.
         *
         * @see FirebaseAnalytics.Event#ECOMMERCE_PURCHASE
         * @see FirebaseAnalytics.Param#CURRENCY
         * @see FirebaseAnalytics.Param#VALUE
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ECOMMERCE_PURCHASE">ECOMMERCE_PURCHASE</a>
         */
        public static synchronized void purchase(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.ECOMMERCE_PURCHASE;

            //prepare parameters
            Bundle bundle = new Bundle();

            //ensure defaults
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, VALUE_DEFAULT_CURRENCY);

            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }


        /**
         * E-Commerce Purchase event.
         * This event signifies that an item was purchased by a user.
         *
         * @see FirebaseAnalytics.Event#ECOMMERCE_PURCHASE
         * @see FirebaseAnalytics.Param#CURRENCY
         * @see FirebaseAnalytics.Param#VALUE
         * @see Analytic.Ecommerce#purchase(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ECOMMERCE_PURCHASE">ECOMMERCE_PURCHASE</a>
         */
        public static synchronized void purchase(@NonNull Double value, @NonNull String currency, @Nullable Bundle params) {

            boolean canTrack =
                    (value != null && !Utils.isEmpty(currency));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);

                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                purchase(bundle);

            }

        }

        /**
         * E-Commerce Purchase event.
         * This event signifies that an item was purchased by a user.
         *
         * @see FirebaseAnalytics.Event#ECOMMERCE_PURCHASE
         * @see FirebaseAnalytics.Param#CURRENCY
         * @see FirebaseAnalytics.Param#VALUE
         * @see Analytic.Ecommerce#purchase(Double, String, Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ECOMMERCE_PURCHASE">ECOMMERCE_PURCHASE</a>
         */
        public static synchronized void purchase(@NonNull Double value, @NonNull String currency) {

            boolean canTrack =
                    (value != null && !Utils.isEmpty(currency));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                purchase(value, currency, bundle);

            }

        }

    }
}
