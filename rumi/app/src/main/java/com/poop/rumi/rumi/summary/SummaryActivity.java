package com.poop.rumi.rumi.summary;

import android.graphics.Color;
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
import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    private final String TAG = "SummaryActivity";

    // Adapters
    private RecyclerViewAdapter nameListAdapter;
    private SummaryListAdapter summaryListAdapter;

    //
    private ArrayList<Transaction> transactionList;
    private ArrayList<ParticipantInfo> participantList;

    //
    private ArrayList<String> names;
    private ArrayList<String> mImageUrls = new ArrayList<>();


    private String storeName;


    int numTapped = 0;

    private String share_code;


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

        tooDeepToGetIntoThis(); // .. to fill participantList []

        final ListView listViewItems = (ListView)findViewById(R.id.vertical_list_participation);

        initImageBitmaps();
        initRecyclerView();

        // take in the context, custom layout that made, arraylist(which is transactionList)
        summaryListAdapter = new SummaryListAdapter(this, R.layout.layout_participation_view, participantList.get(0).getTriadList());
        listViewItems.setAdapter(summaryListAdapter);

        nameListAdapter.passSummaryData(summaryListAdapter, participantList, listViewItems);





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


        generateSharedCode();

    }

    private void tooDeepToGetIntoThis() {

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

        LinearLayoutManager layoutManager = new LinearLayoutManager( SummaryActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        nameListAdapter = new RecyclerViewAdapter(this, names, mImageUrls);
        recyclerView.setAdapter(nameListAdapter);

        // highlighting first element of names
//        layoutManager.findViewByPosition(0).setBackgroundColor(Color.rgb(238, 238, 255));


    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public void generateSharedCode(){

        TextView textView = (TextView)findViewById(R.id.share_code);
        share_code = getSaltString();
        textView.setText(share_code);
    }


}

