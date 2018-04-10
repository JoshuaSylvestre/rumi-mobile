package com.poop.rumi.rumi;


import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Receipt {


    public final String TAG = "ReceiptClass";

    private String storeName;

    private String dateOfCapture;

    private ArrayList<String> items;

    private ArrayList<Float> prices;


    // Looking for items with symbols
    private final Pattern symPttrn = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

    // Looking for string within items with at least 12 digits (as fruit items in Walmart
    // include  2 chars (`KF`) at the end of the code
    private final Pattern dgtPttrn = Pattern.compile("[0-9]{12,}");

    private Matcher m;



    String storeType;

    // Passing image file path allows for extracting the date the receipt was captured given the format
    // of the image file path
     public Receipt() {

        dateOfCapture = dateToString();

        Log.d(TAG, "dateOfCapture = " + dateOfCapture);

        this.items = new ArrayList<>();
        this.prices = new ArrayList<>();
    }


    private String dateToString() {

        DateFormat df = new SimpleDateFormat("E MMM dd yyyy");

        Date today = Calendar.getInstance().getTime();

        return df.format(today);
    }


    public void setStoreName(String storeName) {

        this.storeName = storeName;
    }

    public void addItems(String itemsIn) {

//        Log.d(TAG, "in addItems");

//        Log.d(TAG, "RAWITEMS:\n" + itemsIn);


        // Splitting items by new line
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(itemsIn.split("\n")));

        // For splitting by space
        ArrayList<String> spaces;

        String str;

        boolean chk = false;

        // Discarding items with symbols
        for (int idx = 0; idx < lines.size(); ++idx) {

            Log.d(TAG, "CHECK = " + (chk? "flipped" : "" ));
            chk = false;
            str = lines.get(idx);

            m = symPttrn.matcher(str);

            if (m.find()) {

                // subtracting from idx as to not skip lines
                lines.remove(idx);
                --idx;
                chk = true;
//                Log.d(TAG, "FOUND SYMPTRN: " + str);

                continue;

            }

//            Log.d(TAG, "AT STRING: " + str);

            spaces = new ArrayList<>(Arrays.asList(str.split(" ")));

            for(int i = 0; i < spaces.size(); i++)
            {
                str = spaces.get(i);

                m = dgtPttrn.matcher(str);

                if(m.find() || str.length() == 1) // check length equal 1 for that F at the end
                {
                    // subtracting i as to not skip strings
                    spaces.remove(i--);

//                    Log.d(TAG, "FOUND DIGITPTRN: "+ str);

                }

            }

//            for(String s : spaces)
//                Log.d(TAG, "AFTER CLEAR: " + s);

            StringBuilder sb = new StringBuilder();
            String sep = "";
            for(String s: spaces) {
                sb.append(sep).append(s);
                sep = " ";


            }

            str = sb.toString().trim();

            if(!str.equals(""))
                lines.set(idx, str);

            items.add(str);
        }

    }

    public void addPrices(String pricesIn) {


        Log.d(TAG, "in addPrices()");

        Log.d(TAG, pricesIn);

        ArrayList<String> lines = new ArrayList<>(Arrays.asList(pricesIn.split("\n")));

        String str;

        for (int idx = 0; idx < lines.size(); ++idx) {

            str = lines.get(idx);

            Log.d(TAG, "PRICE WAS: " + str);

            m = dgtPttrn.matcher(str);

            if (m.find())
                str = str.substring(11, str.length());

            // must maintain space after "..A-Z" to get rid of spaces
            str = str.replaceAll("[$a-zA-Z ]", "");

            Log.d(TAG, "STRING BECOMING: " + str);

            DecimalFormat df = new DecimalFormat("#.00");

            Float fl = Float.parseFloat(str);

            df.format(fl);

            Log.d(TAG, "PRICE IS: " + String.valueOf(fl));

            prices.add(fl);
        }

    }

    public String getStoreName() {
        return storeName;
    }

    public String getDateOfCapture() {
        return dateOfCapture;
    }

    public ArrayList<String> getItems() {
        return items;
    }


    public ArrayList<Float> getPrices() {
        return prices;
    }

    public String printItems(){

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for(String s : items){
            sb.append(sep).append(s);
            sep = " \n";

        }

        return sb.toString().trim();
    }


    public String printPrices(){

        StringBuilder sb = new StringBuilder();
        String sep = "";

        for(Float fl : prices) {
            sb.append(sep).append(String.valueOf(fl));
            sep = " \n";
        }

        return sb.toString().trim();
    }

    public int numItems(){

        return items.size();
    }

    public int numPrices(){

        return prices.size();
    }
}
