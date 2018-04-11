package com.poop.rumi.rumi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.poop.rumi.rumi.R;
import com.poop.rumi.rumi.ocr.RecyclerViewAdapter;
import com.poop.rumi.rumi.ocr.Transaction;
import com.poop.rumi.rumi.ocr.TransactionListAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Trans extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // vars:
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    EditText editText_get_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        Log.d(TAG, "onCreate: Started onCreate!");

        initImageBitmaps();

        Button addButton = (Button)findViewById(R.id.button_add_person);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Trans.this);

                LayoutInflater inflater = LayoutInflater.from(Trans.this);
                final View dialogView = inflater.inflate(R.layout.add_person_layout,null);

                builder.setView(dialogView);

                builder.setTitle("Add Person");





                // Set the positive button
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // Set the negative button
                builder.setNegativeButton("Cancel", null);

                // Create the alert dialog
                AlertDialog dialog = builder.create();

                // Finally, display the alert dialog
                dialog.show();

                // Get the alert dialog buttons reference
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);


                // Change the alert dialog buttons text and background color
                positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
                positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

                negativeButton.setTextColor(Color.parseColor("#FFFF0400"));
                negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));


                editText_get_names = (EditText)dialogView.findViewById(R.id.editText_add_name);

                Button keep_adding = (Button)dialogView.findViewById(R.id.button_keep_adding);
                keep_adding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("====== Keep adding button clicked!! ========== ");
                        System.out.println(editText_get_names.getText().toString());
                        System.out.println("====== Keep adding button clicked!! ========== ");

                        mImageUrls.add("");
                        mNames.add(editText_get_names.getText().toString());

                        Toast.makeText(Trans.this , editText_get_names.getText().toString()+" added!" , Toast.LENGTH_SHORT).show();

                        editText_get_names.setText(null);

                    }
                });

            }
        });


        ListView listViewItems = (ListView) findViewById(R.id.vertical_list_item_price_name);

        Transaction step_fart_nee = new Transaction("banana", "step_fart_nee", Float.parseFloat("34.9") );
        Transaction john = new Transaction("egg", "john", Float.parseFloat("3.9") );
        Transaction steve = new Transaction("beer", "steve", Float.parseFloat("4.89") );
        Transaction abe = new Transaction("steak", "abe", Float.parseFloat("5.93") );
        Transaction dita = new Transaction("banana", "dita", Float.parseFloat("6.88") );
        Transaction jordan = new Transaction("banana", "jordan", Float.parseFloat("23.4") );
        Transaction joshua = new Transaction("steak", "joshua", Float.parseFloat("12.3") );
        Transaction subhash = new Transaction("egg", "subhash", Float.parseFloat("6.7") );
        Transaction steven = new Transaction("beer", "steven", Float.parseFloat("21.3") );
        Transaction biem = new Transaction("banana", "biem", Float.parseFloat("43.2") );
        Transaction no_name = new Transaction("steak", "no_name", Float.parseFloat("89.2") );

// //     Objects with empty names:
//        Transaction step_fart_nee = new Transaction("banana", "", "34.9");
//        Transaction john = new Transaction("egg", "", "3.9");
//        Transaction steve = new Transaction("beer", "", "4.89");
//        Transaction abe = new Transaction("steak", "", "5.93");
//        Transaction dita = new Transaction("banana", "", "6.88");
//        Transaction jordan = new Transaction("banana", "", "23.4");
//        Transaction joshua = new Transaction("steak", "", "12.3");
//        Transaction subhash = new Transaction("egg", "", "6.7");
//        Transaction steven = new Transaction("beer", "", "21.3");
//        Transaction biem = new Transaction("banana", "", "43.2");
//        Transaction no_name = new Transaction("steak", "", "89.2");


        // Add transactions to the arraylist: take Transactions objects
        ArrayList<Transaction> transactionList = new ArrayList<>();
        transactionList.add(step_fart_nee );
        transactionList.add(john );
        transactionList.add(steve );
        transactionList.add(abe );
        transactionList.add(dita );
        transactionList.add(jordan );
        transactionList.add(joshua );
        transactionList.add(subhash );
        transactionList.add(biem );
        transactionList.add(no_name );

        // take in the context, custom layout that made, arraylist(which is transactionList)
        TransactionListAdapter adapter = new TransactionListAdapter(this, R.layout.adapter_view_layout, transactionList);
        listViewItems.setAdapter(adapter);


    }


    private void initImageBitmaps(){

        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

//        mImageUrls.add("");
//        mNames.add("Steve");
//
//        mImageUrls.add("");
//        mNames.add("Abe");

        initRecyclerView();

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");


        LinearLayoutManager layoutManager = new LinearLayoutManager( Trans.this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);


    }

}
