package com.poop.rumi.rumi.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dita on 4/13/18.
 */

public class UserModel implements Parcelable{

    public String id;
    public String name;
    public String username;
    public String email;
    public boolean isRegistered;
    public String[] roommates;

    public UserModel(String name, String username, String email, boolean isRegistered) {
        this.id = "";
        this.name = name;
        this.username = username;
        this.email = email;
        this.isRegistered = isRegistered;
        this.roommates = null;
    }

    public UserModel(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.email = json.getString("email");
            this.isRegistered = json.getBoolean("isRegistered");
            this.id = json.getString("_id");

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

    /**
     *
     * Parcelable Methods
     */

    private UserModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        isRegistered = in.readByte() != 0;
        roommates = in.createStringArray();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeByte((byte) (isRegistered ? 1 : 0));
        dest.writeStringArray(roommates);
    }
}
