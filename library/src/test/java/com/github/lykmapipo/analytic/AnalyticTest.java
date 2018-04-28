package com.github.lykmapipo.analytic;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

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

    @After
    public void clean() {
        context = null;
    }
}
