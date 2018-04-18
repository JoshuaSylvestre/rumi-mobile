package com.poop.rumi.rumi.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dita on 4/13/18.
 */

public class UserModel {

    public String name;
    public String username;
    public String email;
    public boolean isRegistered;
    public String[] roommates;

    public UserModel(String name, String username, String email, boolean isRegistered) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.isRegistered = isRegistered;
    }

    public UserModel(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.email = json.getString("email");
            this.isRegistered = json.getBoolean("isRegistered");

            if(this.isRegistered) {
                this.username = json.getString("username");
                this.roommates = convertToStringArr(json, "roommates");
            }
        } catch(Exception ex) {
            Log.e("USER-MODEL", "Cannot parse user data");
        }
    }

    private String[] convertToStringArr(JSONObject json, String key) throws JSONException {
        JSONArray arr = json.getJSONArray(key);
        int len = arr.length();
        String[] res = new String[len];

        for(int i = 0; i < len; i++) {
            res[i] = arr.getString(i);
        }

        return res;
    }

}
