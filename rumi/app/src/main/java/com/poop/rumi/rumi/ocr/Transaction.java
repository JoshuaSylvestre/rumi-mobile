package com.poop.rumi.rumi.ocr;

import java.util.ArrayList;

/**
 * Created by Steve on 4/10/2018.
 */

public class Transaction {

    private String item;
    private Float price;

    ArrayList<String> names;

    // Alt + Insert/Constructor/Select all to make the constructor
    public Transaction(String item, Float price) {

        this.item = item;
        this.price = price;

        names = new ArrayList<>();

    }

    // Alt + Insert/Setter and Getter/Select all to make the constructor
    public ArrayList<String> getNames() {
        return names;
    }

    public String printNames(){

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for(String s : names){
            sb.append(sep).append(s);
            sep = ", ";

        }

        return sb.toString().trim();
    }

    public void addName(String name) {

        names.add(name);
    }

    public void removeName(String name){

        names.remove(name);
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}


