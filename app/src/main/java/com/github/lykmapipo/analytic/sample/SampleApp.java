package com.github.lykmapipo.analytic.sample;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.github.lykmapipo.analytic.Analytic;
import com.github.lykmapipo.common.provider.Provider;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/18/16
 */
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize {@link Analytic} internals
        Analytic.of(new Provider() {
            @NonNull
            @Override
            public Context getApplicationContext() {
                return SampleApp.this;
            }

            @NonNull
            @Override
            public Boolean isDebug() {
                return BuildConfig.DEBUG;
            }
        });

    }
}
