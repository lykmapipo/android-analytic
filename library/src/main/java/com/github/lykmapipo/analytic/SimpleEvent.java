package com.github.lykmapipo.analytic;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

public class SimpleEvent implements Event {
    private Date time;
    private String name;
    private Bundle params = new Bundle();

    public SimpleEvent() {
    }

    public SimpleEvent(@NonNull String name) {
        this.name = name;
        this.time = new Date();
    }

    public SimpleEvent(@NonNull String name, @NonNull Date time) {
        this.name = name;
        this.time = time;
    }

    public SimpleEvent(@NonNull String name, @NonNull Date time, @Nullable Bundle params) {
        this.time = time;
        this.name = name;
        this.setParams(params);
    }

    @Nullable
    @Override
    public Date getTime() {
        return this.time;
    }

    public SimpleEvent setTime(Date time) {
        this.time = time;
        return this;
    }

    @NonNull
    @Override
    public String getName() {
        return this.name;
    }

    public SimpleEvent setName(String name) {
        this.name = name;
        return this;
    }

    @Nullable
    @Override
    public Bundle getParams() {
        return this.params;
    }

    public SimpleEvent setParams(Bundle params) {
        if (params != null) {
            Bundle bundle = new Bundle();
            bundle.putAll(this.params);
            bundle.putAll(params);
            this.params = params;
        }
        return this;
    }

    @NonNull
    public SimpleEvent setParam(@NonNull String key, @NonNull String value) {
        Bundle bundle = new Bundle();
        boolean shouldSet = !TextUtils.isEmpty(key) && !TextUtils.isEmpty(value);
        if (shouldSet) {
            bundle.putString(key, value);
        }
        return this.setParams(bundle);
    }

    @NonNull
    public SimpleEvent setParam(@NonNull String key, @NonNull Long value) {
        Bundle bundle = new Bundle();
        boolean shouldSet = !TextUtils.isEmpty(key);
        if (shouldSet) {
            bundle.putLong(key, value);
        }
        return this.setParams(bundle);
    }

    @NonNull
    public SimpleEvent setParam(@NonNull String key, @NonNull Double value) {
        Bundle bundle = new Bundle();
        boolean shouldSet = !TextUtils.isEmpty(key);
        if (shouldSet) {
            bundle.putDouble(key, value);
        }
        return this.setParams(bundle);
    }

    @NonNull
    public SimpleEvent setParams(@NonNull String key, @NonNull Bundle value) {
        Bundle bundle = new Bundle();
        boolean shouldSet = !TextUtils.isEmpty(key);
        if (shouldSet) {
            bundle.putBundle(key, value);
        }
        return this.setParams(bundle);
    }

    @Override
    public String toString() {
        return "SimpleEvent{" +
                "time=" + time +
                ", name='" + name + '\'' +
                ", params=" + params +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEvent that = (SimpleEvent) o;

        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return params != null ? params.equals(that.params) : that.params == null;
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }
}
