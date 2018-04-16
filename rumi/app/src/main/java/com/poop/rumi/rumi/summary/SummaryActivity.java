package com.poop.rumi.rumi.summary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.poop.rumi.rumi.R;
import com.poop.rumi.rumi.transaction.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    private final String TAG = "SummaryActivity";

    private RecyclerViewAdapter nameListAdapter;
    private SummaryListAdapter summaryListAdapter;
    private ArrayList<ParticipantInfo> participantList;

    private ArrayList<String> names;
    private ArrayList<String> mImageUrls = new ArrayList<>();


    private ArrayList<Transaction> transactionList;


    private String storeName;


    int numTapped = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Bundle transData = getIntent().getExtras();

        assert transData != null;
        transactionList = transData.getParcelableArrayList("TRANSACTION");
        names = transData.getStringArrayList("PARTICIPANTS");
        storeName = transData.getString("STORENAME");
        //TODO: get store name and data
        participantList = new ArrayList<>();

        doMath(); // .. to fill participantList []

        final ListView listViewItems = (ListView)findViewById(R.id.vertical_list_participation);

        initImageBitmaps();
        initRecyclerView();

        // take in the context, custom layout that made, arraylist(which is transactionList)
        summaryListAdapter = new SummaryListAdapter(this, R.layout.layout_participation_view, participantList.get(0).getTriadList());
        listViewItems.setAdapter(summaryListAdapter);

        // setting up co-dependencies
        // for sake of highlighting items based on most recently tapped name
        summaryListAdapter.setRecyclerViewAdapter(nameListAdapter);
        nameListAdapter.setTransactionListAdapter(summaryListAdapter);

        Button nextButton = (Button)findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Button backButton = (Button)findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    private void doMath() {

        DecimalFormat df = new DecimalFormat("#.00");

        for (String name : names)
            participantList.add(new ParticipantInfo(name));


        int numParticipants;
        Float eachPay;
        ArrayList<String> curNames;
        for (Transaction t : transactionList) {

            curNames = t.getNames();

            numParticipants = curNames.size();

            eachPay = t.getPrice() / numParticipants;
            df.format(eachPay);

            Toast.makeText(this, "ITEM :" + t.getItem() + "<= " + curNames.toString(), Toast.LENGTH_LONG).show();

            for (String name : curNames)
                for (ParticipantInfo p : participantList)
                    if (p.getName().equals(name))
                        p.addItemPrice(t.getItem(), t.getPrice(), eachPay);

        }
    }


    private void initImageBitmaps(){

        for(int i = 0; i <  names.size(); i++)
            mImageUrls.add("");

    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager( SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        nameListAdapter = new RecyclerViewAdapter(this, names, mImageUrls);
        recyclerView.setAdapter(nameListAdapter);

    }


}

