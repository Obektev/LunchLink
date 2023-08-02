package com.obektevCo.lunchlink;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class UserSettings {
    private static final String PREF_NAME = "MyPrefs";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor preferences_editor;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences_editor = sharedPreferences.edit();
    }

}
