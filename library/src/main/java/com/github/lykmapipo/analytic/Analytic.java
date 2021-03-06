package com.github.lykmapipo.analytic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.lykmapipo.common.Common;
import com.github.lykmapipo.common.provider.Provider;
import com.github.lykmapipo.log.Log;
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
    public static final String VALUE_MEDIUM_ANDROID = "android";
    public static final String VALUE_DEFAULT_CURRENCY = "USD";
    public static final String VALUE_CONTENT_TYPE_ACTION = "action_performed";
    public static final String VALUE_CONTENT_TYPE_SCREENVIEW = "screen";

    /**
     * {@link FirebaseAnalytics} instance
     */
    private static FirebaseAnalytics analytics;

    /**
     * {@link Provider} instance
     */
    private static Provider appProvider;

    /**
     * Default event parameter
     */
    private static Bundle defaultEventParams = new Bundle();

    /**
     * Initialize analytic
     *
     * @param provider {@link Provider}
     * @return {@link FirebaseAnalytics}
     */
    @RequiresPermission(
            allOf = {
                    "android.permission.INTERNET",
                    "android.permission.ACCESS_NETWORK_STATE",
                    "android.permission.WAKE_LOCK"
            }
    )
    public static synchronized FirebaseAnalytics of(@NonNull Provider provider) {

        //instantiate if not exist
        if (analytics == null) {
            appProvider = provider;
            Common.of(appProvider);
            Log.of(appProvider);
            analytics = FirebaseAnalytics.getInstance(appProvider.getApplicationContext());
        }
        return analytics;
    }

    /**
     * Clean up and reset {@link Analytic} internals
     */
    public static synchronized void dispose() {
        analytics = null;
        appProvider = null;
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
     * Set user identifier for {@link FirebaseAnalytics}
     *
     * @param identifier valid unique user identifier
     * @since 0.1.0
     */
    public static void setUserIdentifier(@NonNull String identifier) {
        if (analytics != null) {
            Log.setUserIdentifier(identifier);
            analytics.setUserId(identifier);
        }
    }

    /**
     * Set user properties to {@link FirebaseAnalytics}
     *
     * @param key   valid property key
     * @param value valid property value
     * @since 0.1.0
     */
    public static void setUserProperty(@NonNull String key, @NonNull Object value) {
        if (analytics != null) {
            Log.setUserProperty(key, value);
            analytics.setUserProperty(key, String.valueOf(value));
        }
    }

    /**
     * Derive default analytic params
     *
     * @return {@link Bundle}
     */
    public static synchronized Bundle getDefaultEventParams() {
        Bundle params = new Bundle();
        params.putAll(defaultEventParams);

        params.putString(Param.TIMEZONE, Common.Dates.timezone()); //timezone
        params.putLong(Param.TIME, new Date().getTime()); //time
        params.putString(Param.MEDIUM, VALUE_MEDIUM_ANDROID);//medium

        return params;
    }

    /**
     * Logs an app screen event. Events with the same name must have the same parameters.
     *
     * @param eventName   The name of the event
     * @param activity    current screen
     * @param screenName  viewed screen
     * @param eventParams The map of event parameters
     * @see FirebaseAnalytics#logEvent(String, Bundle)
     */
    public static synchronized void track(
            @NonNull String eventName, @NonNull FragmentActivity activity,
            @NonNull String screenName, @Nullable Bundle eventParams) {

        //ensure analytic and event name
        boolean canTrack =
                (analytics != null && !Common.Strings.areEmpty(eventName, screenName));

        if (canTrack) {

            //prepare event parameters
            Bundle params = getDefaultEventParams();
            if (eventParams != null) {
                params.putAll(eventParams);
            }

            // set current screen
            analytics.setCurrentScreen(activity, screenName, null);

            //send event to firebase analytics
            analytics.logEvent(eventName, params);

            //debug
            Log.d(TAG, params.toString());
        }

        //notify not tracked
        else {
            Log.d(TAG, "Fail to log event");
        }

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
        boolean canTrack = (analytics != null && !Common.Strings.isEmpty(eventName));

        if (canTrack) {

            //prepare event parameters
            Bundle params = getDefaultEventParams();
            if (eventParams != null) {
                params.putAll(eventParams);
            }

            //send event to firebase analytics
            analytics.logEvent(eventName, params);

            //debug
            Log.d(TAG, params.toString());
        }

        //notify not tracked
        else {
            Log.d(TAG, "Fail to log event");
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
            params.putLong(Param.TIME, eventTime.getTime());
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
     * Interface definition for a item params
     *
     * @since 0.1.0
     */
    public interface Itemable {
        @NonNull
        String getItemId();

        @NonNull
        String getItemCategory();
    }

    /**
     * params
     */
    public static class Param extends FirebaseAnalytics.Param {
        public static final String PAYMENT_METHOD = "payment_method";//method used to pay e.g paypal
        public static final String PAYMENT_REFERENCE = "payment_reference";//unique payment identifier e.g transaction id, receipt etc.
        public static final String TIMEZONE = "timezone"; //event timezone
        public static final String TIME = "time"; // event time
        public static final String MEDIUM = "medium"; //event medium(or channel)
    }


    /**
     * Track app events
     */
    public static class App {
        //OPEN

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


        //LOGIN

        /**
         * Logs user logged in event
         *
         * @see FirebaseAnalytics.Event#LOGIN
         * @see FirebaseAnalytics.Param#METHOD
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#LOGIN">LOGIN</a>
         */
        public static synchronized void loggedIn(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.LOGIN;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            track(eventName, bundle);
        }

        /**
         * Logs user logged in event
         *
         * @see FirebaseAnalytics.Event#LOGIN
         * @see FirebaseAnalytics.Param#METHOD
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#LOGIN">LOGIN</a>
         */
        public static synchronized void loggedIn(@NonNull String method, @Nullable Bundle params) {

            if (!Common.Strings.isEmpty(method)) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, method);
                if (params != null) {
                    bundle.putAll(params);
                }

                loggedIn(bundle);
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


        //SIGNUP

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
        public static synchronized void signedUp(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.SIGN_UP;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            track(eventName, bundle);

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

            if (!Common.Strings.isEmpty(method)) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, method);
                if (params != null) {
                    bundle.putAll(params);
                }

                signedUp(bundle);

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


        //SHARE

        /**
         * Logs share event
         *
         * @see FirebaseAnalytics.Event#SHARE
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#SHARE">SHARE</a>
         */
        public static synchronized void share(@NonNull Bundle params) {
            //TODO ensure content_type & item_id
            String eventName = FirebaseAnalytics.Event.SHARE;

            //prepare parameters
            Bundle bundle = new Bundle();
            if (params != null) {
                bundle.putAll(params);
            }

            track(eventName, bundle);

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
        public static synchronized void share(
                @NonNull String method, @NonNull String itemId,
                @NonNull String contentType, @Nullable Bundle params) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(method) && !Common.Strings.isEmpty(itemId) && !Common.Strings.isEmpty(contentType));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, method);
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
                if (params != null) {
                    bundle.putAll(params);
                }

                share(bundle);
            }
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
        public static synchronized void share(
                @NonNull String method, @NonNull String itemId, @NonNull String contentType) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(method) && !Common.Strings.isEmpty(itemId) && !Common.Strings.isEmpty(contentType));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                share(method, itemId, contentType, bundle);

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
        public static synchronized void item(
                @NonNull String id, @NonNull String name,
                @NonNull String category, @Nullable Bundle params) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(id) && !Common.Strings.isEmpty(name) && !Common.Strings.isEmpty(category));

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
        public static synchronized void item(
                @NonNull String name, @NonNull String category, @Nullable Bundle params) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(name) && !Common.Strings.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                item(name, name, category, bundle);
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
                    (!Common.Strings.isEmpty(name) && !Common.Strings.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                item(name, category, bundle);
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
                    (!Common.Strings.isEmpty(category));

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
                    (!Common.Strings.isEmpty(category));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                list(category, bundle);
            }

        }

        /**
         * View Screen event. Log this event when the user has view a specific UI screen.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         */
        public static synchronized void screen(
                @NonNull String screenName, @NonNull Fragment fragment) {
            screen(screenName, fragment.requireActivity());
        }

        /**
         * View Screen event. Log this event when the user has view a specific UI screen.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         */
        public static synchronized void screen(
                @NonNull String screenName, @NonNull FragmentActivity activity) {

            String eventName = FirebaseAnalytics.Event.SELECT_CONTENT;

            //prepare parameters
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, screenName);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, VALUE_CONTENT_TYPE_SCREENVIEW);

            //track
            track(eventName, activity, screenName, bundle);
        }

    }

    /**
     * Track ecommerce events
     */
    public static class Ecommerce {
        /**
         * E-Commerce Add To Wishlist event.
         * This event signifies that an item was added to a wishlist.
         * Use this event to identify popular gift items in your app.
         *
         * @see FirebaseAnalytics.Event#ADD_TO_WISHLIST
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#QUANTITY
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ADD_TO_WISHLIST">ADD_TO_WISHLIST</a>
         */
        public static synchronized void addToWishList(@NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.ADD_TO_WISHLIST;

            //prepare parameters
            Bundle bundle = new Bundle();

            //ensure defaults
            bundle.putLong(FirebaseAnalytics.Param.QUANTITY, 1);

            if (params != null) {
                bundle.putAll(params);
            }

            //track
            track(eventName, bundle);

        }

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
         * @see FirebaseAnalytics.Param#QUANTITY
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ADD_TO_WISHLIST">ADD_TO_WISHLIST</a>
         */
        public static synchronized void addToWishList(
                @NonNull String itemId, @NonNull String itemName,
                @NonNull String itemCategory, @NonNull Long quantity, @Nullable Bundle params) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(itemName) && !Common.Strings.isEmpty(itemName)
                            && !Common.Strings.isEmpty(itemCategory) && quantity != null);

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //ensure defaults
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory);
                bundle.putLong(FirebaseAnalytics.Param.QUANTITY, quantity);

                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                addToWishList(bundle);

            }

        }

        /**
         * E-Commerce Add To Wishlist event.
         * This event signifies that an item was added to a wishlist.
         * Use this event to identify popular gift items in your app.
         *
         * @see FirebaseAnalytics.Event#ADD_TO_WISHLIST
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#QUANTITY
         * @see Analytic.Ecommerce#addToWishList(String, String, String, Long, Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ADD_TO_WISHLIST">ADD_TO_WISHLIST</a>
         */
        public static synchronized void addToWishList(
                @NonNull String itemId, @NonNull String itemName, @NonNull String itemCategory) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(itemName) && !Common.Strings.isEmpty(itemName)
                            && !Common.Strings.isEmpty(itemCategory));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                addToWishList(itemId, itemName, itemCategory, 1L, bundle);

            }

        }

        /**
         * E-Commerce Add To Wishlist event.
         * This event signifies that an item was added to a wishlist.
         * Use this event to identify popular gift items in your app.
         *
         * @see FirebaseAnalytics.Event#ADD_TO_WISHLIST
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#QUANTITY
         * @see Analytic.Ecommerce#addToWishList(String, String, String)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#ADD_TO_WISHLIST">ADD_TO_WISHLIST</a>
         */
        public static synchronized void addToWishList(
                @NonNull String itemName, @NonNull String itemCategory) {

            boolean canTrack =
                    (!Common.Strings.isEmpty(itemName) && !Common.Strings.isEmpty(itemCategory));

            //track
            if (canTrack) {
                addToWishList(itemName, itemName, itemCategory);
            }

        }

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


        //CHECKOUT

        /**
         * E-Commerce Begin Checkout event.
         * This event signifies that a user has begun the process of checking out.
         *
         * @see FirebaseAnalytics.Event#BEGIN_CHECKOUT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_NAME
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see Analytic.Ecommerce#beginCheckout(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#BEGIN_CHECKOUT">BEGIN_CHECKOUT</a>
         */
        public static synchronized void beginCheckout() {

            //prepare parameters
            Bundle bundle = new Bundle();

            //track
            beginCheckout(bundle);

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

        /**
         * E-Commerce Checkout Progress event.
         *
         * @see FirebaseAnalytics.Event#CHECKOUT_PROGRESS
         * @see FirebaseAnalytics.Param#CHECKOUT_STEP
         * @see FirebaseAnalytics.Param#CHECKOUT_OPTION
         * @see Analytic.Ecommerce#checkoutProgress(Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#CHECKOUT_PROGRESS">CHECKOUT_PROGRESS</a>
         */
        public static synchronized void checkoutProgress(
                @NonNull Long step, @NonNull String option, @Nullable Bundle params) {

            boolean canTrack =
                    (step != null && !Common.Strings.isEmpty(option));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                if (params != null) {
                    bundle.putAll(params);
                }

                //track
                checkoutProgress(bundle);

            }

        }

        /**
         * E-Commerce Checkout Progress event.
         *
         * @see FirebaseAnalytics.Event#CHECKOUT_PROGRESS
         * @see FirebaseAnalytics.Param#CHECKOUT_STEP
         * @see FirebaseAnalytics.Param#CHECKOUT_OPTION
         * @see Analytic.Ecommerce#checkoutProgress(Long, String, Bundle)
         * @see <a href="https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event.html#CHECKOUT_PROGRESS">CHECKOUT_PROGRESS</a>
         */
        public static synchronized void checkoutProgress(
                @NonNull Long step, @NonNull String option) {

            boolean canTrack =
                    (step != null && !Common.Strings.isEmpty(option));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                checkoutProgress(step, option, bundle);

            }

        }

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


        //PURCHASE

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
        public static synchronized void purchase(
                @NonNull Double value, @NonNull String currency, @Nullable Bundle params) {

            boolean canTrack =
                    (value != null && !Common.Strings.isEmpty(currency));

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
        public static synchronized void purchase(
                @NonNull Double value, @NonNull String currency) {

            boolean canTrack =
                    (value != null && !Common.Strings.isEmpty(currency));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();

                //track
                purchase(value, currency, bundle);

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
        public static synchronized void purchase(
                @NonNull Double value, @NonNull String currency,
                @NonNull String method, @NonNull String reference) {

            boolean canTrack =
                    (value != null && !Common.Strings.isEmpty(currency));

            if (canTrack) {

                //prepare parameters
                Bundle bundle = new Bundle();
                bundle.putString(Param.PAYMENT_METHOD, method);
                bundle.putString(Param.PAYMENT_REFERENCE, reference);

                //track
                purchase(value, currency, bundle);

            }

        }

    }

    /**
     * Track common actions
     */
    public static class Action {
        // TODO: actor, acted on

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName) {
            performed(actionName, new Bundle());
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName, @NonNull String itemId) {
            performed(actionName, itemId, new Bundle());
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName,
                @NonNull String itemId, @NonNull Bundle params) {

            Bundle _params = new Bundle();
            params.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
            _params = Common.Bundles.from(_params, params);

            performed(actionName, _params);
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName, @NonNull Itemable itemable) {
            performed(actionName, itemable.getItemId(), itemable.getItemCategory());
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName, @NonNull String itemId,
                @NonNull String itemCategory) {
            performed(actionName, itemId, itemCategory, new Bundle());
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName, @NonNull Itemable itemable,
                @NonNull Bundle params) {
            performed(actionName, itemable.getItemId(), itemable.getItemCategory(), params);
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName, @NonNull String itemId,
                @NonNull String itemCategory, @NonNull Bundle params) {

            Bundle _params = new Bundle();
            params.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
            params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, itemCategory);
            _params = Common.Bundles.from(_params, params);

            performed(actionName, _params);
        }

        /**
         * Action performed event. Log this event when the user perform an action.
         *
         * @see FirebaseAnalytics.Event#SELECT_CONTENT
         * @see FirebaseAnalytics.Param#ITEM_ID
         * @see FirebaseAnalytics.Param#ITEM_CATEGORY
         * @see FirebaseAnalytics.Param#CONTENT_TYPE
         * @see FirebaseAnalytics.Param#GROUP_ID
         * @see FirebaseAnalytics.Param#METHOD
         * @see FirebaseAnalytics.Param#SUCCESS
         */
        public static synchronized void performed(
                @NonNull String actionName, @NonNull Bundle params) {

            String eventName = FirebaseAnalytics.Event.SELECT_CONTENT;

            //prepare parameters
            Bundle base = new Bundle();
            base.putString(FirebaseAnalytics.Param.CONTENT_TYPE, VALUE_CONTENT_TYPE_ACTION);
            base.putString(FirebaseAnalytics.Param.GROUP_ID, actionName);
            Bundle bundle = Common.Bundles.from(params, base);

            //track
            track(eventName, bundle);
        }
    }
}
