package com.poop.rumi.rumi.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dita on 4/11/18.
 */

public class TransactionModel extends DashboardContentModel {
    public String billCode;
    public String[] roommates;
    public String storeName;
    public String receiptLink;
    public String billDate;
    public String ownerId;
    public List<TransactionItemModel> transactionList;

    private JSONObject json;

    public TransactionModel(JSONObject json) {
        this.json = json;
        transactionList = new ArrayList<>();
        convertJson();
    }

    private void convertJson() {
        try {
            this.billCode = json.getString("bill_code");
            this.roommates = convertToStringArr(json, "roommates");
            this.storeName = json.getString("store_name");
            this.receiptLink = json.getString("receipt_link");
            this.billDate = json.getString("bill_date");
            this.ownerId = json.getString("owner_id");
            JSONArray arr = json.getJSONArray("transaction_list");
            int len = arr.length();

            for(int i = 0; i < len; i++) {
                transactionList.add(new TransactionItemModel(arr.getJSONObject(i)));
            }
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

    public class TransactionItemModel {
        public String name;
        public String[] items;
        public String[] ogPrices;
        public String[] splitPrices;

        TransactionItemModel(JSONObject json) {
            try {
                this.name = json.getString("name");
                this.items = convertToStringArr(json, "items");
                this.ogPrices = convertToStringArr(json, "og_prices");
                this.splitPrices = convertToStringArr(json, "split_prices");
            } catch(Exception ex) {
                Log.e("TRANSACTION-ITEM-MODEL", "Failed parsing JSON");
            }
        }
    }
}
