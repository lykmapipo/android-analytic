package com.github.lykmapipo.analytic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Event
 * <p>
 * Contract to be implemented for an object to be track and logged to analytic
 * </p>
 *
 * @author lally elias<lallyelias87@gmail.com>
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Event {
    @Nullable
    Date getTime();

    @NonNull
    String getName();

    @Nullable
    Bundle getParams();
}
