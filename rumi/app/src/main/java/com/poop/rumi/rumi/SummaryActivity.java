package com.poop.rumi.rumi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.poop.rumi.rumi.ocr.Transaction;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    ArrayList<ArrayList<String>> people = new ArrayList<ArrayList<String>>();
    int numItems = 0;
    ArrayList<EachPersonInfor> eachPersonInforArrayList = new ArrayList<>();
    EachPersonInfor mPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        String itemName = new String();
        ArrayList<String> peopleNames = new ArrayList<>();
        String itemPrice = new String();


        for(int i = 0; i < Integer.MAX_VALUE; i++){
            if( (String)getIntent().getSerializableExtra(String.valueOf(i)+"item") == null )
            {
                numItems = i;
                System.out.println("***** num items: "+i);
                break;
            }
            itemName = (String)getIntent().getSerializableExtra( String.valueOf(i)+"item");
            peopleNames = (ArrayList<String>)getIntent().getSerializableExtra(String.valueOf(i)+"names");
            itemPrice = (String)getIntent().getSerializableExtra(String.valueOf(i)+"price");

            transactionArrayList.add(
                    new Transaction(itemName, Float.parseFloat(String.valueOf(itemPrice)))
            );
            people.add(i, peopleNames);
            System.out.println("**********************************************");
            System.out.println("transactionArrayList: "+transactionArrayList.get(i).getItem());
            System.out.println("transactionArrayList: "+transactionArrayList.get(i).getPrice());
            System.out.println("transactionArrayList: "+people.get(i));
            System.out.println("**********************************************");
        }

        //        ================ Output with data passed over: ==================

        //        D/TextView: setTypeface with style : 0
        //        I/System.out: **********************************************
        //        I/System.out: transactionArrayList: BUSCH NA 12PK CAN
        //        I/System.out: transactionArrayList: 7.59
        //        I/System.out: transactionArrayList: [Abe, Dita, Alana, Joshua, Jordan, Subhash]
        //        I/System.out: **********************************************
        //        I/System.out: **********************************************
        //        I/System.out: transactionArrayList: TOTINOS PIZZA
        //        I/System.out: transactionArrayList: 1.25
        //        I/System.out: transactionArrayList: [Abe, Dita, Alana, Joshua, Jordan, Subhash]
        //        I/System.out: **********************************************
        //        I/System.out: **********************************************
        //        I/System.out: transactionArrayList: APPLES MCINTOSH LG
        //        I/System.out: transactionArrayList: 1.4
        //        I/System.out: transactionArrayList: [Steve, Dita, Alana, Joshua, Jordan, Subhash]
        //        I/System.out: **********************************************
        //        I/System.out: **********************************************
        //        I/System.out: transactionArrayList: BANANAS
        //        I/System.out: transactionArrayList: 1.7
        //        I/System.out: transactionArrayList: [Steve, Dita, Alana, Joshua, Jordan, Subhash]
        //        I/System.out: **********************************************
        //        I/System.out: **********************************************
        //        I/System.out: transactionArrayList: PEARS BARTLETT
        //        I/System.out: transactionArrayList: 0.76
        //        I/System.out: transactionArrayList: [Steve, Dita, Alana, Joshua, Jordan, Subhash]
        //        I/System.out: **********************************************
        //        I/System.out: ***** num items: 5

        // TODO: Loop through these data and put the right items and prices into the right name:
        // TODO: Then calculate the number of items, total plus taxes


    }
}
