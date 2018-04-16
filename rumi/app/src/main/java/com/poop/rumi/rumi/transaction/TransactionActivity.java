package com.poop.rumi.rumi.transaction;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.poop.rumi.rumi.R;
import com.poop.rumi.rumi.Receipt;
import com.poop.rumi.rumi.summary.SummaryActivity;

import android.app.AlertDialog;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionActivity extends AppCompatActivity {

    private static final String TAG = "TransactionActivity";

    private RecyclerViewAdapter nameListAdapter;
    private TransactionListAdapter transListAdapter;
    private ArrayList<Transaction> transactionList;

    // vars:
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    TextView store_restaurant;

    Receipt mReceipt;
    ArrayList<String> clean_input_items;
    ArrayList<Float> clean_input_prices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        Log.d(TAG, "onCreate: Started onCreate!");


        mReceipt = (Receipt) getIntent().getSerializableExtra("RECEIPT");


        Log.d("TEST 2" ,"==============================================");
        Log.d("TEST 2" ,mReceipt.getStoreName());
        Log.d("TEST 2" ,mReceipt.printItems());
        Log.d("TEST 2" ,mReceipt.printPrices());
        Log.d("TEST 2" ,"==============================================");


        // Add transactions to the arraylist: take Transactions objects
        transactionList = new ArrayList<>();

        ArrayList<String> inputItems = mReceipt.getItems();
        ArrayList<Float> inputPrices = mReceipt.getPrices();

        int maxLength = Math.max(mReceipt.getItems().size(), mReceipt.getPrices().size());

        String tempStr;
        Float tempFlt;
        for(int i = 0; i < maxLength; i++)
        {

            if(i >= inputPrices.size()) {
                tempStr = inputItems.get(i);
                tempFlt = 0f;
            }
            else if(i >= inputItems.size()){
                tempStr = "";
                tempFlt = inputPrices.get(i);
            }
            else {
                tempStr = inputItems.get(i);
                tempFlt = inputPrices.get(i);
            }

            transactionList.add(new Transaction(tempStr,tempFlt));
        }

        String storeName = mReceipt.getStoreName();
        String date = mReceipt.getDateOfCapture();



//        if(!storeName.equals(null))
//        {
//            int len = Math.min(inputItems.size(), inputPrices.size());
//            clean_input_items = new ArrayList<>();
//            clean_input_prices = new ArrayList<>();
//
//            for(int i = 0; i < len; i++){
//
//                String inputItem = inputItems.get(i);
//
//                if(!inputItem.equals("")
//                    && !inputItem.toLowerCase().equals("subtotal")
//                    && !inputItem.toLowerCase().equals("total")
//                    && !inputItem.toLowerCase().equals("debit")
//                    && !inputItem.toLowerCase().equals("debit tend")
//                    && !inputItem.toLowerCase().equals("change")
//                    && !inputItem.toLowerCase().equals("change due")
//                    && !inputItem.toLowerCase().equals("debit")
//                    && !inputItem.toLowerCase().equals("you saved")
//                    && !inputItem.toLowerCase().equals("tax")
//                    && !inputItem.toLowerCase().equals("tax 1")
//                    && !inputItem.toLowerCase().equals("tax 2")
//                    && !inputItem.toLowerCase().equals("order")
//                    && !inputItem.toLowerCase().equals("order total")
//                    && !inputItem.toLowerCase().equals("regular tax")
//                    && !inputItem.toLowerCase().equals("food tax")
//                    && !inputItem.toLowerCase().equals("grand total")
//                    && !inputItem.toLowerCase().equals("payment")
//                    && !inputItem.toLowerCase().equals("sales")
//                    && !inputItem.toLowerCase().equals("sale total")
//                    && !inputItem.toLowerCase().equals("ycu saved"))
//                {
//                    clean_input_items.add(inputItems.get(i));
//                }
//
//                if(!String.valueOf(inputPrices.get(i)).equals("")){
//                    clean_input_prices.add(inputPrices.get(i));
//                }
//            }
//
//            int clean_len = Math.min(clean_input_items.size(), clean_input_prices.size());
//
//            for(int i = 0; i < clean_len; i++)
//            {
//                transactionList.add(
//                        new Transaction(
//                                clean_input_items.get(i),
//                                Float.parseFloat(String.valueOf(clean_input_prices.get(i)))
//                        )
//
//                );
//            }
//
//
//        }


        final ListView listViewItems = (ListView)findViewById(R.id.vertical_list_item_price_name);

        initImageBitmaps();
        initRecyclerView();

        // take in the context, custom layout that made, arraylist(which is transactionList)
        transListAdapter = new TransactionListAdapter(this, R.layout.layout_transaction_view, transactionList);
        listViewItems.setAdapter(transListAdapter);

        // setting up co-dependencies
        // for sake of highlighting items based on most recently tapped name
        transListAdapter.setRecyclerViewAdapter(nameListAdapter);
        nameListAdapter.setTransactionListAdapter(transListAdapter);


        store_restaurant = findViewById(R.id.store_restaurant);
        store_restaurant.setText(mReceipt.getStoreName());
        store_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openEditStoreNameDialog();} });

        Button nextButton = (Button)findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSummaryActivity();
            }
        });

        Button backButton = (Button)findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button addButton = (Button)findViewById(R.id.button_add_person);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPersonDialog();
            }
        });

        // Add Item/Price Button
        Button add_item_price = (Button)findViewById(R.id.button_add_more_item_price);
        add_item_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddItemPriceDialog();

            }
        });

    }

    public void openAddPersonDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);

        LayoutInflater inflater = LayoutInflater.from(TransactionActivity.this);

        final View dialogView = inflater.inflate(R.layout.dialog_add_person,null);

        builder.setView(dialogView);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Finally, display the alert dialog
        dialog.show();

        final EditText editText_get_names = (EditText)dialogView.findViewById(R.id.editText_add_name);

        Button keep_adding = (Button)dialogView.findViewById(R.id.button_keep_adding);
        keep_adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("====== Keep adding button clicked!! ========== ");
                System.out.println(editText_get_names.getText().toString());
                System.out.println("====== Keep adding button clicked!! ========== ");

                if(editText_get_names.getText().toString().length() >= 7){
                    Toast.makeText(TransactionActivity.this, "Please limit the name/nickname under 7-letter", Toast.LENGTH_SHORT).show();
                }else if(mNames.contains(editText_get_names.getText().toString())) {

                    Toast.makeText(TransactionActivity.this, "Name already exists, please try a different name.", Toast.LENGTH_SHORT).show();
                }
                else{

                    mImageUrls.add("");
                    mNames.add(editText_get_names.getText().toString());

                    Toast.makeText(TransactionActivity.this, editText_get_names.getText().toString() + " added!", Toast.LENGTH_SHORT).show();

                    editText_get_names.setText(null);
                }


            }
        });

    }

    public void openAddItemPriceDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);

        LayoutInflater inflater = LayoutInflater.from(TransactionActivity.this);

        final View dialogView = inflater.inflate(R.layout.dialog_add_or_edit_item,null);

        builder.setView(dialogView);

        final EditText editText_item_name = (EditText) dialogView.findViewById(R.id.edit_item_name);
        final EditText editText_item_price = (EditText) dialogView.findViewById(R.id.edit_item_price);

        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set the positive button
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                System.out.println("Item: **** "+editText_item_name.getText().toString());
//                System.out.println("Price: **** "+editText_item_price.getText().toString());

                Transaction t;
                try{

                    t = new Transaction(
                            editText_item_name.getText().toString(),
                            Float.parseFloat(editText_item_price.getText().toString()));
                }catch(Exception e){
                    Log.e("InvalidNumber","Can not parse empty float");
                        return; // Or another exception handling.
                }

                transactionList.add(t);

                dialog.dismiss();
            }
        });

        // Finally, display the alert dialog
        dialog.show();

    }

    public void openEditStoreNameDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);

        LayoutInflater inflater = LayoutInflater.from(TransactionActivity.this);

        final View dialogView = inflater.inflate(R.layout.dialog_edit_store_name,null);

        builder.setView(dialogView);

        final EditText edit_store_restaurant_name = (EditText)dialogView.findViewById(R.id.edit_store_name);
        Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);

        // Create the alert dialog
        final AlertDialog dialog = builder.create();


        // Set positive/yes button click listener
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                store_restaurant.setText(edit_store_restaurant_name.getText().toString());

                dialog.dismiss();
            }
        });

        // Finally, display the alert dialog
        dialog.show();

    }


    private void initImageBitmaps(){

        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("");
        mNames.add("Steve");

        mImageUrls.add("");
        mNames.add("Abe");

        mImageUrls.add("");
        mNames.add("Dita");

        mImageUrls.add("");
        mNames.add("Alana");

        mImageUrls.add("");
        mNames.add("Joshua");

        mImageUrls.add("");
        mNames.add("John");

        mImageUrls.add("");
        mNames.add("Subhash");


        //initRecyclerView();

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        LinearLayoutManager layoutManager = new LinearLayoutManager( TransactionActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        nameListAdapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(nameListAdapter);

    }

    public void openSummaryActivity() {


        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra("TRANSACTION", transactionList);
        intent.putExtra("PARTICIPANTS", mNames);
        intent.putExtra("STORENAME", store_restaurant.getText().toString());
        //TODO: pass storename and date
        startActivity(intent);

    }



}
