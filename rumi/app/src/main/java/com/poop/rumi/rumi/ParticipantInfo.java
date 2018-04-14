package com.poop.rumi.rumi;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Abe on 4/14/2018.
 */

public class ParticipantInfo {

    private String name;
    private ArrayList<String> mItems;
    private ArrayList<Float> mOgPrices;    //for original prices
    private ArrayList<Float> mPrices;      //for split prices


    public ParticipantInfo(String name) {
        this.name = name;

        mItems = new ArrayList<>();
        mOgPrices = new ArrayList<>();
        mPrices = new ArrayList<>();
    }

    public void addItemPrice(String item, Float origPrice, Float splitPrice){

        mItems.add(item);
        mOgPrices.add(origPrice);
        mPrices.add(splitPrice);

    }

    public String printInfo(){

        StringBuilder sb = new StringBuilder();
        String sep = "";

        sb.append(name +"\n===============\n");


        for(int i = 0; i < mItems.size(); i++) {
            sb.append(sep).append(mItems.get(i)).append(" : ").append(mPrices.get(i).toString());
            sep = "\n";
        }

        return sb.toString().trim();
    }


    public String getName() {
        return name;
    }

    public ArrayList<String> getmItems() {
        return mItems;
    }

    public ArrayList<Float> getmOgPrices() {
        return mOgPrices;
    }

    public ArrayList<Float> getmPrices() {
        return mPrices;
    }

    public Float getTotal(){

        Float sum = 0f;

        for(Float f : mPrices)
            sum += f;

        return sum;
    }


}
