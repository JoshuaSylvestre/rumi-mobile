package com.poop.rumi.rumi.ocr;

/**
 * Created by Steve on 4/10/2018.
 */

public class Transaction {
    private String names;// figure out a way to make an array out of this
    private String item;
    private String price;

    // Alt + Insert/Constructor/Select all to make the constructor
    public Transaction(String item, String names, String price) {
        this.names = names;
        this.item = item;
        this.price = price;

    }

    // Alt + Insert/Setter and Getter/Select all to make the constructor
    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}


