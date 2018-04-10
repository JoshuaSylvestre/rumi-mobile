package com.poop.rumi.rumi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dita on 4/8/18.
 */

public class UserSession {

    private SharedPreferences prefs;

    public UserSession(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setUser(String user) {
        prefs.edit().putString("user", user).apply();
    }

    public String getUser() {
        return prefs.getString("user", "");
    }

}
