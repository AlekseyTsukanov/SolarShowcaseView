package com.skytech.solar.config;

import android.content.Context;
import android.content.SharedPreferences;

public class SolarPreference {
    private static final String PREFERENCE_NAME = "solar_showcase_preference";
    private static final String SKIP_STATUS = "skip_status_";
    private static final String TEMPORAL_SKIP_STATUS = "temporal_skip_status_";
    private static final String DISMISS_STATUS = "dismiss_status_";

    private Context mContext;
    private String mShowcaseId;

    public SolarPreference(Context context, String showcaseId) {
        mContext = context;
        mShowcaseId = showcaseId;
    }

    public void setDismissed(boolean dismiss) {
        SharedPreferences dismissPreference = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        dismissPreference.edit().putBoolean(DISMISS_STATUS, dismiss).apply();
    }

    public boolean isDismissed() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(DISMISS_STATUS, false);
    }

    public void setSkipped(boolean skip) {
        SharedPreferences skipPreference = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        skipPreference.edit().putBoolean(SKIP_STATUS, skip).apply();
    }

    public boolean isSkipped() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(SKIP_STATUS, false);
    }

    public void setTemporalSkip(boolean temporalSkip) {
        SharedPreferences temporalSkipStatus = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        temporalSkipStatus.edit().putBoolean(TEMPORAL_SKIP_STATUS, temporalSkip).apply();
    }

    public boolean getTemporalSkip() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(TEMPORAL_SKIP_STATUS, false);
    }

    public static void resetAll(Context context) {
        SharedPreferences resetPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        resetPreferences.edit().clear().apply();
    }
}
