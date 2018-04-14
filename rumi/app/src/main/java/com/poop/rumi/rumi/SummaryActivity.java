package com.poop.rumi.rumi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.poop.rumi.rumi.transaction_classes.Transaction;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    ArrayList<Transaction> transactionList;

    ArrayList<ArrayList<String>> people = new ArrayList<>();
    ArrayList<EachPersonInfor> eachPersonInforArrayList = new ArrayList<>();
    EachPersonInfor mPerson;
    String storeName;
    int numItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Bundle transData = getIntent().getExtras();

        assert transData != null;
        transactionList = transData.getParcelableArrayList("TRANSACTION");

        System.out.println("IN SUMMARY");

        for(Transaction t : transactionList){

            System.out.println(t.getItem());
            System.out.println(t.getPrice());
            System.out.println(t.printNames());
        }






    }

    {

//        System.out.println("Store name: "+store_name);}
//
//
//        String itemName = new String();
//        ArrayList<String> peopleNames = new ArrayList<>();
//        String itemPrice = new String();
//
//
//        for(int i = 0; i < Integer.MAX_VALUE; i++){
//            if( (String)getIntent().getSerializableExtra(String.valueOf(i)+"item") == null )
//            {
//                numItems = i;
//                System.out.println("***** num items: "+i);
//                break;
//            }
//            itemName = (String)getIntent().getSerializableExtra( String.valueOf(i)+"item");
//            peopleNames = (ArrayList<String>)getIntent().getSerializableExtra(String.valueOf(i)+"names");
//            itemPrice = (String)getIntent().getSerializableExtra(String.valueOf(i)+"price");
//
//            transactionArrayList.add(
//                    new Transaction(itemName, Float.parseFloat(String.valueOf(itemPrice)))
//            );
//            people.add(i, peopleNames);
//            System.out.println("**********************************************");
//            System.out.println("transactionArrayList getItem: "+transactionArrayList.get(i).getItem());
//            System.out.println("transactionArrayList getPrice: "+transactionArrayList.get(i).getPrice());
//            System.out.println("people: "+people.get(i));
//            System.out.println("**********************************************");
//        }
//
//
//
//        // TODO: Loop through these data and put the right items and prices into the right name:
//        // TODO: Then calculate the number of items, total plus taxes}
    }
}
