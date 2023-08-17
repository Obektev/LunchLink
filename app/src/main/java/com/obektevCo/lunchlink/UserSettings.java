package com.obektevCo.lunchlink;

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
    public static void setSchoolInMemory(Context context, String school_name) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences_editor = sharedPreferences.edit();
        preferences_editor.putString("school_name", school_name);
    }

    public static String getSchoolInMemory(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("school_name", null);
    }
    public static void setCityInMemory(Context context, String city_name) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences_editor = sharedPreferences.edit();
        preferences_editor.putString("city_name", city_name);
    }

    public static String getCityInMemory(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("city_name", null);
    }
    public static void setClassInMemory(Context context, String class_name) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences_editor = sharedPreferences.edit();
        preferences_editor.putString("class_name", class_name);
    }
    public static void setDate(Context context, String date) {
        date = date.replace('/', ':');
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences_editor = sharedPreferences.edit();
        preferences_editor.putString("today_date", date);
    }

    public static String getDate(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("today_date", null);
    }
    public static String getClassInMemory(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("class_name", null);
    }
}
