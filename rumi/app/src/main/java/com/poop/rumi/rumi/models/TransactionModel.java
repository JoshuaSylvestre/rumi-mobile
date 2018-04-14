package com.poop.rumi.rumi.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dita on 4/11/18.
 */

public class TransactionModel extends DashboardContentModel {
    public String[] items;
    public String[] prices;
    public String[] roommateIds;
    public String[] roommateNames;
    public String groupName;
    public String companyName;
    public String receiptLink;
    public String transactionType;
    public String total;
    public String numFriends;
    public String eachPay;
    private JSONObject json;

    public TransactionModel(String[] items, String[] prices, String[] roommateIds, String[] roommateNames, String groupName, String companyName, String receiptLink, String transactionType, String total, String numFriends, String eachPay, String date) {
        this.items = items;
        this.prices = prices;
        this.roommateIds = roommateIds;
        this.roommateNames = roommateNames;
        this.groupName = groupName;
        this.companyName = companyName;
        this.receiptLink = receiptLink;
        this.transactionType = transactionType;
        this.total = total;
        this.numFriends = numFriends;
        this.eachPay = eachPay;
        convertDate(date);
        this.json = null;
    }

    public TransactionModel(JSONObject json) {
        this.json = json;
        convertJson();
    }

    private void convertJson() {
        try {
            this.items = convertToStringArr(json, "items");
            this.prices = convertToStringArr(json, "prices");
            this.roommateIds = convertToStringArr(json, "friends_ids");
            this.roommateNames = convertToStringArr(json, "friend_names");
            this.groupName = json.getString("group_name");
            this.companyName = json.getString("company_name");
            this.receiptLink = json.getString("receipt_link");
            this.transactionType = json.getString("transaction_type");
            this.total = json.getString("total");
            this.numFriends = json.getString("num_friends");
            this.eachPay = json.getString("each_pay");
            convertDate(json.getString("date_unix"));
        } catch(Exception ex) {
            Log.e("TRANSACTION-MODEL", "Error parsing JSON");
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
