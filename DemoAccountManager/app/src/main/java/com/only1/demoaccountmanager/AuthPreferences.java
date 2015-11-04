package com.only1.demoaccountmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by layn01 on 11/4/15.
 */
public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences preferences;

    public AuthPreferences(Context context) {
        preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public void setUser(String user) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        editor.commit();
    }

    public void setToken(String token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getUser() {
        return preferences.getString(KEY_USER, null);
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }
}
