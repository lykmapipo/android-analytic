package com.github.lykmapipo.analytic.sample;

import android.app.Application;

import com.github.lykmapipo.analytic.Analytic;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com
 * @date 10/18/16
 */
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //initialize analytic
        Analytic.initialize(getApplicationContext());

    }
}
