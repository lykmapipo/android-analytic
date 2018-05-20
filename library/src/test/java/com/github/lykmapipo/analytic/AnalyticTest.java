package com.github.lykmapipo.analytic;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com
 */

@Config(sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class AnalyticTest {
    private String TEST_EVENT = "test_event";
    private String TEST_PARAM = "test_param";
    private String TEST_CURRENCY = "USD";
    private Double TEST_VALUE = 20.20;
    private Context context;

    @Before
    public void setup() {
        context = ShadowApplication.getInstance().getApplicationContext();
    }


    @Test
    public void shouldBeAbleToGetAnalyticInstance_01() {

        FirebaseAnalytics instance = Analytic.initialize(context);

        assertThat(instance, is(not(equalTo(null))));
        assertThat(instance, is(instanceOf(FirebaseAnalytics.class)));
    }

    @Test
    public void shouldBeAbleToGetAnalyticInstance_02() {

        Analytic.initialize(context);
        FirebaseAnalytics instance = Analytic.getInstance();

        assertThat(instance, is(not(equalTo(null))));
        assertThat(instance, is(instanceOf(FirebaseAnalytics.class)));
    }

    @Test
    public void shouldBeAbleToGetSameAnalyticInstance() {

        FirebaseAnalytics instance_1 = Analytic.initialize(context);
        FirebaseAnalytics instance_2 = Analytic.initialize(context);

        assertThat(instance_1, is(not(equalTo(null))));
        assertThat(instance_1, is(instanceOf(FirebaseAnalytics.class)));

        assertThat(instance_2, is(not(equalTo(null))));
        assertThat(instance_2, is(instanceOf(FirebaseAnalytics.class)));

        assertThat(instance_1, is(sameInstance(instance_2)));
    }

    @Test
    public void shouldBeAbleToGetDefaultParameters() {
        Bundle params = Analytic.getDefaultEventParams();

        //assert timezone
        String timezone = params.getString(Analytic.PARAM_TIMEZONE);
        assertThat(timezone, is(not(equalTo(null))));

        //assert time
        Long time = params.getLong(Analytic.PARAM_TIME);
        assertThat(time, is(not(equalTo(null))));
    }

    @Test
    public void shouldBeAbleToTrack_01() {
        Exception exception = null;
        try {
            Analytic.track(TEST_EVENT, null);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrack_02() {
        Exception exception = null;
        try {
            SimpleEvent event =
                    new SimpleEvent()
                            .setName(TEST_EVENT)
                            .setTime(new Date())
                            .setParam(TEST_PARAM, TEST_PARAM);
            Analytic.track(event);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackAppOpened_01() {
        Exception exception = null;
        try {
            Analytic.App.opened();
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackAppOpened_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.App.opened(params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackAppSignUp_01() {
        Exception exception = null;
        try {
            Analytic.App.signedUp(TEST_PARAM);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackSignUp_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.App.signedUp(TEST_PARAM, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackAppLogin_01() {
        Exception exception = null;
        try {
            Analytic.App.loggedIn(TEST_PARAM);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackLogin_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.App.loggedIn(TEST_PARAM, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackTutorialBegin_01() {
        Exception exception = null;
        try {
            Analytic.Tutorial.begin();
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackTutorialBegin_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.Tutorial.begin(params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackTutorialComplete_01() {
        Exception exception = null;
        try {
            Analytic.Tutorial.complete();
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackTutorialComplete_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.Tutorial.complete(params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemView_01() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.View.item(params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemView_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.View.item(TEST_PARAM, TEST_PARAM, TEST_PARAM, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemView_03() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.View.item(TEST_PARAM, TEST_PARAM, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemView_04() {
        Exception exception = null;
        try {
            Analytic.View.item(TEST_PARAM, TEST_PARAM);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemView_05() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.View.item(TEST_PARAM, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemView_06() {
        Exception exception = null;
        try {
            Analytic.View.item(TEST_PARAM);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemListView_01() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.View.list(params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemListView_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            params.putString(TEST_PARAM, TEST_PARAM);
            Analytic.View.list(TEST_PARAM, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackItemListView_03() {
        Exception exception = null;
        try {
            Analytic.View.list(TEST_PARAM);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }


    @Test
    public void shouldBeAbleToTrackEcommercePurchase_01() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            Analytic.Ecommerce.purchase(params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackEcommercePurchase_02() {
        Exception exception = null;
        try {
            Bundle params = new Bundle();
            Analytic.Ecommerce.purchase(TEST_VALUE, TEST_CURRENCY, params);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }

    @Test
    public void shouldBeAbleToTrackEcommercePurchase_03() {
        Exception exception = null;
        try {
            Analytic.Ecommerce.purchase(TEST_VALUE, TEST_CURRENCY);
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception, is(equalTo(null)));
    }


    @After
    public void clean() {
        context = null;
    }
}
