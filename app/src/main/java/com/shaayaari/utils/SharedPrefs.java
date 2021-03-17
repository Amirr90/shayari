package com.shaayaari.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    static SharedPreferences sharedPreferences;

    public static void subscribeTopic(String topic) {
        sharedPreferences = App.context.getSharedPreferences(AppConstant.SHARED_PREFS, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(topic, "yes");
        editor.apply();
    }

    public static Boolean isSubscribeTopic(String topic) {
        if (null == sharedPreferences)
            sharedPreferences = App.context.getSharedPreferences(topic, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(topic, "");
        return !value.isEmpty();
    }

}
